package org.tpc.form_builder.audits;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.CollectionUtils;
import org.tpc.form_builder.models.AuditSnapShot;
import org.tpc.form_builder.models.repository.AuditSnapShotRepository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Aspect
@Order(1)
@Service
@RequiredArgsConstructor
@Log4j2
public class AuditLogger {
    private final AuditSnapShotRepository auditSnapShotRepository;
    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(
                Math.max(2, Runtime.getRuntime().availableProcessors())
        );
    }

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .findAndRegisterModules();

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
                saveObjectSnapShot(args[0]);
            } else if ("saveAll".equalsIgnoreCase(methodName) || "saveAllAndFlush".equalsIgnoreCase(methodName)) {
                saveObjectListSnapShots(new ArrayList<>((Collection<?>) args[0]));
            }
        } catch (Exception e) {
            log.error("Audit logging failed for method: {}", methodName, e);
        }

        Object result = joinPoint.proceed(); // Always proceed first
        log.debug("Audit Log for {} executed in {}ms", methodName, System.currentTimeMillis() - startTime);
        return result;
    }

    private void saveObjectSnapShot(Object savedEntity) {
        saveObjectListSnapShots(List.of(savedEntity));
    }

    private void saveObjectListSnapShots(List<Object> savedEntityList) {
        if (CollectionUtils.isEmpty(savedEntityList)) {
            return;
        }

        Object referenceObject = savedEntityList.getFirst();
        String className = getFullyQualifiedClassName(referenceObject);

        List<AuditSnapShot> auditEntities = savedEntityList.stream()
                .map(entity -> createAuditEntity(className, entity))
                .filter(Objects::nonNull)
                .toList();
        auditSnapShotRepository.saveAll(auditEntities);
    }

    private AuditSnapShot createAuditEntity(String className, Object savedEntity) {
        try {
            String entityId = getSavedEntityIdentifier(savedEntity);
            return AuditSnapShot.builder()
                    .className(className)
                    .instanceId(entityId)
                    .auditObject(objectMapper.writeValueAsString(savedEntity))
                    .build();
        }
        catch (Exception e) {
            log.error("Something went wrong creating audit entity", e);
            return null;
        }
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

    private String getFullyQualifiedClassName(Object entity) {
        return entity.getClass().getName();
    }

    public static Object deserialize(String json, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON to class: " + className, e);
        }
    }
}
