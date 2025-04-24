package org.tpc.form_builder.audits;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.Order;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.CollectionUtils;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.tpc.form_builder.constants.CommonConstants;
import org.tpc.form_builder.models.FormFieldData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Aspect
@Order(1)
@Service
@RequiredArgsConstructor
@Log4j2
public class AuditLogger {
    private final ApplicationContext applicationContext;
    private final AuditLogQueue auditLogQueue;

    private Map<String, Object> repositoryMap;
    private ExecutorService executorService;

    private static final String FIELD_KEYWORD = "dataMap";

    @PostConstruct
    public void init() {
        repositoryMap = applicationContext.getBeansWithAnnotation(Repository.class);
        executorService = Executors.newFixedThreadPool(
                Math.max(2, Runtime.getRuntime().availableProcessors())
        );
    }

    @PreDestroy
    public void shutdownExecutor() {
        executorService.shutdown();
    }

    @Pointcut("execution(* org.springframework.data.mongodb.repository.MongoRepository.save(..)) && args(..)")
    public void mongoSavePointcut() {}

    @Pointcut("execution(* org.springframework.data.mongodb.repository.MongoRepository.saveAll(..)) && args(..)")
    public void mongoSaveAllPointcut() {}

    @Around("mongoSavePointcut() || mongoSaveAllPointcut()")
    public Object beforeSave(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0 || args[0] == null) {
            log.error("No arguments found in method: {}. Audit log skipped.", methodName);
            return joinPoint.proceed();
        }

        try {
            if ("save".equalsIgnoreCase(methodName) || "saveAndFlush".equalsIgnoreCase(methodName)) {
                processSingleSave(args[0]);
            } else if ("saveAll".equalsIgnoreCase(methodName) || "saveAllAndFlush".equalsIgnoreCase(methodName)) {
                processBatchSave((Collection<?>) args[0]);
            }
        } catch (Exception e) {
            log.error("Audit logging failed for method: {}", methodName, e);
        }

        Object result = joinPoint.proceed(); // Always proceed first
        log.debug("Audit Log for {} executed in {}ms", methodName, System.currentTimeMillis() - startTime);
        return result;
    }

    private void processSingleSave(Object entity) {
        String repositoryName = findRepositoryNameForSavedEntity(entity);
        String entityId = getSavedEntityIdentifier(entity);
        Object oldEntity = getPreviousObject(entity, entityId, repositoryMap.get(repositoryName));

        executorService.submit(() -> compareAndSendObjects(CommonConstants.DEFAULT_CLIENT, CommonConstants.DEFAULT_COMPANY_ID, CommonConstants.DEFAULT_USER_ID, entity, oldEntity, entityId, repositoryName));
    }

    private void processBatchSave(Collection<?> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            log.warn("Empty collection in saveAll. Skipping audit.");
            return;
        }

        List<Object> savedEntities = new ArrayList<>(collection);
        Object referenceEntity = savedEntities.getFirst();
        String repositoryName = findRepositoryNameForSavedEntity(referenceEntity);

        List<String> saveEntityIds = savedEntities.stream()
                .map(this::getSavedEntityIdentifier)
                .toList();

        List<Object> previousObjects = getPreviousObjects(saveEntityIds, repositoryName);

        if (previousObjects.size() != savedEntities.size()) {
            log.warn("Total Saves: {} :: Matched Existing: {} :: New Saves: {}",
                    savedEntities.size(), previousObjects.size(), savedEntities.size() - previousObjects.size());
        }

        Map<String, Object> previousObjectMap = previousObjects.stream()
                .collect(Collectors.toMap(this::getSavedEntityIdentifier, o -> o));

        for (Object savedEntity : savedEntities) {
            String id = getSavedEntityIdentifier(savedEntity);
            Object oldEntity = previousObjectMap.get(id); // maybe null for newly created entities

            executorService.submit(() -> compareAndSendObjects(CommonConstants.DEFAULT_CLIENT, CommonConstants.DEFAULT_COMPANY_ID, CommonConstants.DEFAULT_USER_ID, savedEntity, oldEntity, id, repositoryName));
        }
    }

    private void compareAndSendObjects(String clientId, String companyId, Long userId, Object entity, Object previousEntity, String entityId, String repositoryName) {
        long startTime = System.currentTimeMillis();
        log.info("Comparing objects for Audit Log");

        Map<String, ChangeDto> diffMap = new HashMap<>();
        boolean isDeleted = false;

        try {
            if (entity != null && previousEntity != null) {
                Class<?> entityClass = entity.getClass();
                Field[] fields = entityClass.getDeclaredFields();

                for (Field field : fields) {
                    field.setAccessible(true);
                    Object oldValue = field.get(previousEntity);
                    Object newValue = field.get(entity);

                    if (!Objects.equals(oldValue, newValue)) {
                        log.info("Audit Log for field {}: \n{} \n → \n{}", field.getName(), oldValue, newValue);
                        diffMap.putAll(compareDifferences(field.getName(), oldValue, newValue));

                        // Soft delete check: isActive changed to false
                        if ("isActive".equalsIgnoreCase(field.getName())
                                && newValue instanceof Boolean
                                && Boolean.FALSE.equals(newValue)) {
                            isDeleted = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Something went wrong comparing objects", e);
        }

        AuditAction action = AuditAction.UPDATE;
        if (StringUtils.isEmpty(entityId)) {
            action = AuditAction.CREATE;
        } else if (isDeleted) {
            action = AuditAction.DELETE;
        }

        AuditDto auditDto = AuditDto.builder()
                .clientId(clientId)
                .companyId(companyId)
                .instanceId(entityId)
                .action(action)
                .repository(repositoryName)
                .associatedUserId(userId)
                .changes(diffMap)
                .createdOn(Instant.now())
                .build();

        auditLogQueue.enqueue(auditDto);
        long endTime = System.currentTimeMillis();
        log.info("Time taken to compare objects: {} ms", endTime - startTime);
    }

    private Map<String, ChangeDto> compareDifferences(String fieldName, Object previousObject, Object newObject) {
        return switch (fieldName) {
            case FIELD_KEYWORD -> compareFieldObject(previousObject, newObject);
            default -> Map.of(fieldName, compareNormalObjects(previousObject, newObject));
        };
    }

    private ChangeDto compareNormalObjects(Object previousObject, Object newObject) {
        return ChangeDto.builder()
                .action(previousObject == null ? AuditAction.CREATE : newObject == null ? AuditAction.DELETE: AuditAction.UPDATE)
                .fieldType(null)
                .previousValues(List.of(previousObject != null ? previousObject : ""))
                .newValues(List.of(List.of(previousObject != null ? previousObject : "")))
                .build();
    }

    private Map<String, ChangeDto> compareFieldObject(Object previousObject, Object newObject) {
        Map<String, ChangeDto> diffMap = new HashMap<>();

        @SuppressWarnings("unchecked")
        Map<String, FormFieldData> previousData = (Map<String, FormFieldData>) previousObject;
        @SuppressWarnings("unchecked")
        Map<String, FormFieldData> newData = (Map<String, FormFieldData>) newObject;

        if (!CollectionUtils.isEmpty(previousData) && !CollectionUtils.isEmpty(newData)) {
            // Compare existing fields
            for (Map.Entry<String, FormFieldData> entry : previousData.entrySet()) {
                String key = entry.getKey();
                if (newData.containsKey(key)) {
                    FormFieldData previousValue = entry.getValue();
                    FormFieldData newValue = newData.get(key);

                    AuditAction action = previousValue.compareEquals(newValue);
                    if (action != null) {
                        diffMap.put(FIELD_KEYWORD + "." + key, ChangeDto.builder()
                                .action(action)
                                .fieldType(previousValue.getFieldType())
                                .previousValues(List.of(previousValue))
                                .newValues(List.of(newValue))
                                .build());
                    }
                }
            }

            // Newly Created Values
            for (Map.Entry<String, FormFieldData> entry : newData.entrySet()) {
                String key = entry.getKey();
                if (!previousData.containsKey(key)) {
                    FormFieldData newValue = entry.getValue();
                    diffMap.put(FIELD_KEYWORD + "." + key, ChangeDto.builder()
                            .action(AuditAction.CREATE)
                            .fieldType(newValue.getFieldType())
                            .previousValues(null)
                            .newValues(List.of(newValue))
                            .build());
                }
            }
        }
        return diffMap;
    }

    private List<Object> getPreviousObjects(List<String> entityIds, Object repository) {
        List<Object> previousEntities = findEntitiesByIds(repository, entityIds);
        if (previousEntities.size() < entityIds.size()) {
            log.error("Could not find all entities: {}", entityIds);
        }
        return previousEntities;
    }

    private Object getPreviousObject(Object entity, String entityId, Object repository) {
        Object previousEntity = findEntityById(repository, entityId);
        return previousEntity != null ? previousEntity : entity;
    }

    private Object findEntityById(@NotNull Object repository, @NotNull String entityId) {
        try {
            if (repository instanceof MongoRepository) {
                Method findByIdMethod = repository.getClass().getMethod("findById", Object.class);
                Optional<?> result = (Optional<?>) findByIdMethod.invoke(repository, entityId);
                return result.orElse(null);
            }
        } catch (Exception e) {
            log.error("Something went wrong getting previous entity", e);
        }
        return null;
    }

    private List<Object> findEntitiesByIds(@NotNull Object repository, @NotNull List<String> entityIds) {
        try {
            if (repository instanceof MongoRepository) {
                Method findAllByIdMethod = repository.getClass().getMethod("findAllById", Iterable.class);
                Iterable<?> result = (Iterable<?>) findAllByIdMethod.invoke(repository, entityIds);

                List<Object> entities = new ArrayList<>();
                result.forEach(entities::add);
                return entities;
            }
        } catch (Exception e) {
            log.error("Something went wrong getting previous entities by IDs", e);
        }
        return Collections.emptyList();
    }

    private String getSavedEntityIdentifier(Object entity) {
        try {
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    return value != null ? value.toString() : null;
                }
            }
            log.error("No field annotated with @Id found in class {}", entity.getClass().getName());
            return null;
        } catch (IllegalAccessException e) {
            log.error("Unable to access entity identifier", e);
            return null;
        }
    }

    private String findRepositoryNameForSavedEntity(Object entity) {
        Class<?> entityClass = entity.getClass();
        if (CollectionUtils.isEmpty(repositoryMap)) {
            log.error("No repositories found in application context");
            return null;
        }
        for (Map.Entry<String, ?> repository : repositoryMap.entrySet()) {
            if (repository.getValue() instanceof MongoRepository) {
                Class<?>[] typeArgs = GenericTypeResolver.resolveTypeArguments(repository.getValue().getClass(), MongoRepository.class);
                if (typeArgs != null && typeArgs.length > 0 && typeArgs[0].equals(entityClass)) {
                    return repository.getKey();
                }
            }
        }
        log.info("No repository found for entity: {}", entityClass.getName());
        return null;
    }
}
