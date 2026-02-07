package com.tpc.form_builder.formula.function;

import com.tpc.form_builder.formula.function.libraries.*;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry that collects static methods annotated with @FormulaFunction from configured classes.
 * By default, it includes built-in classes. You can extend it via constructor injection (list of classes).
 */
@Component
@NoArgsConstructor
@SuppressWarnings("unused")
public class FunctionRegistry {

    // default internal function classes - can add more by constructor injection if you extend this class
    // TODO - Make this through @Function Library annotation and classpath scanning, so we don't need to modify this class to add new libraries
    private final List<Class<?>> functionClasses = new ArrayList<>(List.of(
            MathFunctionLibrary.class,
            StringFunctionLibrary.class,
            LogicalFunctionsLibrary.class,
            NullFunctionLibrary.class,
            CollectionFunctionLibrary.class,
            DateFunctionLibrary.class
    ));

    // map: exposedName -> Method
    private final Map<String, Method> functions = new ConcurrentHashMap<>();
    private final Map<String, String> helpTexts = new ConcurrentHashMap<>();

    /**
     * Optionally allow adding more classes (if you create bean configuration to add)
     */
    public void addFunctionClass(Class<?> clazz) {
        functionClasses.add(clazz);
    }

    @PostConstruct
    public void init() {
        for (Class<?> clazz : functionClasses) {
            for (Method m : clazz.getDeclaredMethods()) {
                FormulaFunction ann = m.getAnnotation(FormulaFunction.class);
                if (ann != null) {
                    if (!java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
                        throw new IllegalStateException("FormulaFunction method must be static: " + m);
                    }
                    String name = ann.name().isEmpty() ? m.getName() : ann.name();
                    functions.put(name.toUpperCase(Locale.ROOT), m);
                    helpTexts.put(name.toUpperCase(Locale.ROOT), ann.help());
                }
            }
        }
    }

    public Map<String, Method> allFunctions() { return Collections.unmodifiableMap(functions); }

    public Optional<Method> getFunction(String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(functions.get(name.toUpperCase(Locale.ROOT)));
    }

    public List<String> listAvailableFunctions() {
        List<String> out = new ArrayList<>(functions.keySet());
        Collections.sort(out);
        return out;
    }

    public String getHelp(String name) {
        return helpTexts.get(name.toUpperCase(Locale.ROOT));
    }
}


