package com.tpc.form_builder.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility methods for working with entity collections.
 */
public final class EntityUtils {

    private EntityUtils() {
        throw new AssertionError("No instances allowed");
    }

    /**
     * Creates a map from a collection of entities using a derived attribute as the key.
     *
     * <p>Behavioral rules:
     * <ul>
     *   <li>{@code null} entities are always ignored</li>
     *   <li>If {@code ignoreNulls} is {@code true}, entities producing {@code null} keys are ignored</li>
     *   <li>If {@code ignoreNulls} is {@code false}, a {@link IllegalArgumentException} is thrown
     *       when a {@code null} key is extracted</li>
     *   <li>If {@code overwriteDuplicates} is {@code true}, later entities overwrite earlier ones</li>
     *   <li>If {@code overwriteDuplicates} is {@code false}, an {@link IllegalStateException} is thrown
     *       when duplicate keys are encountered</li>
     * </ul>
     *
     * @param entities the collection of entities to process; must not be {@code null}
     * @param attributeExtractor function used to extract the map key from an entity; must not be {@code null}
     * @param ignoreNulls whether entities with {@code null} keys should be ignored
     * @param overwriteDuplicates whether duplicate keys should overwrite existing entries
     * @param <T> the entity type
     * @param <K> the key type
     *
     * @return a map keyed by the extracted attribute
     *
     * @throws NullPointerException if {@code entities} or {@code attributeExtractor} is {@code null}
     * @throws IllegalArgumentException if a {@code null} key is extracted and {@code ignoreNulls} is {@code false}
     * @throws IllegalStateException if duplicate keys are encountered and {@code overwriteDuplicates} is {@code false}
     */
    public static <T, K> Map<K, T> mapByAttribute(
            Collection<T> entities,
            Function<T, K> attributeExtractor,
            boolean ignoreNulls,
            boolean overwriteDuplicates
    ) {
        Objects.requireNonNull(entities, "entities must not be null");
        Objects.requireNonNull(attributeExtractor, "attributeExtractor must not be null");

        return entities.stream()
                .filter(Objects::nonNull)
                .map(entity -> {
                    K key = attributeExtractor.apply(entity);

                    if (key == null && !ignoreNulls) {
                        throw new IllegalArgumentException(
                                "Null key extracted for entity: " + entity
                        );
                    }

                    return key == null ? null : Map.entry(key, entity);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        overwriteDuplicates
                                ? (_, replacement) -> replacement
                                : (existing, _) -> {
                            throw new IllegalStateException(
                                    "Duplicate key detected for key: " + existing
                            );
                        }
                ));
    }

    /**
     * Groups a collection of entities by a derived attribute.
     *
     * <p>Behavioral rules:
     * <ul>
     *   <li>{@code null} entities are always ignored</li>
     *   <li>If {@code ignoreNulls} is {@code true}, entities producing {@code null} keys are ignored</li>
     *   <li>If {@code ignoreNulls} is {@code false}, a {@link IllegalArgumentException} is thrown
     *       when a {@code null} key is extracted</li>
     *   <li>All entities with the same key are grouped together in insertion order</li>
     * </ul>
     *
     * @param entities the collection of entities to process; must not be {@code null}
     * @param attributeExtractor function used to extract the grouping key; must not be {@code null}
     * @param ignoreNulls whether entities with {@code null} keys should be ignored
     * @param <T> the entity type
     * @param <K> the key type
     *
     * @return a map grouping entities by the extracted attribute
     *
     * @throws NullPointerException if {@code entities} or {@code attributeExtractor} is {@code null}
     * @throws IllegalArgumentException if a {@code null} key is extracted and {@code ignoreNulls} is {@code false}
     */
    public static <T, K> Map<K, List<T>> groupByAttribute(
            Collection<T> entities,
            Function<T, K> attributeExtractor,
            boolean ignoreNulls
    ) {
        Objects.requireNonNull(entities, "entities must not be null");
        Objects.requireNonNull(attributeExtractor, "attributeExtractor must not be null");

        return entities.stream()
                .filter(Objects::nonNull)
                .map(entity -> {
                    K key = attributeExtractor.apply(entity);

                    if (key == null && !ignoreNulls) {
                        throw new IllegalArgumentException(
                                "Null key extracted for entity: " + entity
                        );
                    }

                    return key == null ? null : Map.entry(key, entity);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }
}

