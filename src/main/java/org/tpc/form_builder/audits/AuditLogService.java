package org.tpc.form_builder.audits;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.Order;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.CollectionUtils;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.tpc.form_builder.constants.CommonConstants;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Aspect
@Order(1)
@Service
@RequiredArgsConstructor
@Log4j2
public class AuditLogService {
    private final ApplicationContext applicationContext;
    private final AuditLogQueue auditLogQueue;

    private Map<String, Object> repositoryMap;

    @PostConstruct
    public void init() {
        repositoryMap = applicationContext.getBeansWithAnnotation(org.springframework.stereotype.Repository.class);
    }

    @Pointcut("execution(* org.springframework.data.mongodb.repository.MongoRepository.save(..)) && args(..)")
    public void mongoSavePointcut() {}

    @Around("mongoSavePointcut()")
    public Object afterSave(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Object[] args = joinPoint.getArgs();
        Object entity;
        if (args != null && args.length > 0) {
            entity = args[0];
        }
        else {
            log.error("No arguments found in method: {}. Thus audit log fails", methodSignature.getName());
            return null;
        }
        // If this is a save method
        if ("save".equalsIgnoreCase(joinPoint.getSignature().getName())) {
            String repositoryName = this.findRepositoryNameForSavedEntity(entity);
            String entityId = this.getSavedEntityIdentifier(entity);

            Object oldEntity = this.getPreviousObject(entity, entityId, repositoryMap.get(repositoryName));
            try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
                executor.submit(() -> {
                    // Compare and send objects
                    compareAndSendObjects(entity, oldEntity, entityId, repositoryName);
                });
            }
            return joinPoint.proceed();
        }
        log.debug("Audit Log for save executed in {}ms", System.currentTimeMillis() - startTime);
        return null;
    }

    private void compareAndSendObjects(Object entity, Object previousEntity, String entityId, String repositoryName) {
        long startTime = System.currentTimeMillis();
        log.info("Comparing objects for Audit Log");
        Map<String, ChangeDto> diffMap = new HashMap<>();
        try {
            if (entity != null && previousEntity != null) {
                Class<?> entityClass = entity.getClass();
                Field[] fields = entityClass.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object oldValue = field.get(previousEntity);
                    Object newValue = field.get(entity);

                    if ((oldValue != null && !oldValue.equals(newValue)) || (oldValue == null && newValue != null)) {
                        log.info("Audit Log for field {}: \n{} \n → \n{}", field.getName(), oldValue, newValue);
                        diffMap.put(
                                field.getName(),
                                ChangeDto.builder()
                                        .previousValue(oldValue)
                                        .currentValue(newValue)
                                        .build()
                        );
                    }
                }
            }
        }
        catch (Exception e) {
            log.error("Something went wrong comparing objects", e);
        }

        AuditDto auditDto = AuditDto.builder()
                .clientId(CommonConstants.DEFAULT_CLIENT)
                .entityId(entityId)
                .repository(repositoryName)
                // Change this to the actual user ID when we have authentication
                .associatedUserId(0L)
                .changes(diffMap)
                .build();
        auditLogQueue.enqueue(auditDto);
        long endTime = System.currentTimeMillis();
        log.info("Time taken to compare objects: {} ms", endTime - startTime);
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
