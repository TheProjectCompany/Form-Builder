package com.tpc.form_builder.formula.function.libraries;

import com.tpc.form_builder.formula.function.FormulaFunction;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CollectionFunctionLibrary {

    @FormulaFunction(name = "List", help = "LIST(a,b,...)")
    public static List<Object> list(Object... items) { return Arrays.asList(items); }

    @FormulaFunction(help = "SIZE(collection/map/string/array)")
    public static int size(Object value) {
        return switch (value) {
            case Collection<?> collection -> collection.size();
            case Map<?, ?> map -> map.size();
            case String string -> string.length();
            case Object array when array.getClass().isArray()
                    -> java.lang.reflect.Array.getLength(array);
            case null, default -> 0;
        };
    }

    @FormulaFunction(help = "CONTAINS(collection, item)")
    public static boolean contains(Object collection, Object item) {
        if (collection instanceof Collection<?> col) return col.contains(item);
        if (collection instanceof Map<?, ?> map) return map.containsKey(item) || map.containsValue(item);
        if (collection instanceof String string && item instanceof String substring) return string.contains(substring);
        return false;
    }

    @FormulaFunction(help = "CONTAINS_ALL(collection, items)")
    public static boolean containsAll(Object collection, Object items) {
        if (collection instanceof Collection<?> col) {
            Collection<Object> colList = new ArrayList<>(col);
            if (items instanceof Collection<?> item) {
                return colList.containsAll(new ArrayList<>(item));
            }
            if (items != null && items.getClass().isArray()) {
                return colList.containsAll(Arrays.asList((Object[]) items));
            }
            return col.contains(items);
        }
        if (collection instanceof String string && items instanceof String item) {
            return string.contains(item);
        }
        return false;
    }

    @FormulaFunction(help = "DISTINCT(list)")
    public static List<Object> distinct(Collection<?> c) {
        if (c == null) return Collections.emptyList();
        return c.stream().distinct().collect(Collectors.toList());
    }

    @FormulaFunction(help = "JOIN(list, separator)")
    public static String join(Collection<?> list, String sep) {
        if (list == null) return "";
        return list.stream().map(Objects::toString).collect(Collectors.joining(sep == null ? "" : sep));
    }

    @FormulaFunction(help = "GET(map, key)")
    public static Object get(Map<?, ?> map, Object key) {
        return map == null ? null : map.get(key);
    }

    // simple SORT(list, 'ASC'|'DESC') - only comparable elements
    @FormulaFunction(help = "SORT(list, dir)")
    public static List<Object> sort(Collection<?> list, String dir) {
        if (list == null) return Collections.emptyList();
        List<Object> out = new ArrayList<>(list);
        out.sort((a,b) -> {
            if (a instanceof Comparable aa && b instanceof Comparable bb) {
                return aa.compareTo(bb);
            }
            return 0;
        });
        if ("DESC".equalsIgnoreCase(dir)) Collections.reverse(out);
        return out;
    }
}
