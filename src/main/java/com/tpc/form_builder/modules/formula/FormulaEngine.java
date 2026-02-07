package com.tpc.form_builder.modules.formula;

import com.tpc.form_builder.modules.formula.cache.SpELCache;
import com.tpc.form_builder.modules.formula.function.FunctionRegistry;
import com.tpc.form_builder.modules.formula.model.Expression;
import com.tpc.form_builder.modules.formula.model.VariableDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class FormulaEngine {

    private final FunctionRegistry functionRegistry;
    private final SpELCache spELCache;

    // simple var name regex — assumes variables are plain identifiers like var1, userId, etc.
    private static final Pattern VAR_PATTERN = Pattern.compile("\\b([A-Za-z_][A-Za-z0-9_]*)\\b");

    /** Preprocess - remove leading '=' and replace FUNCTION( with #FUNCTION( for SpEL */
    public String preprocess(String expr) {
        if (expr == null) return null;
        String e = expr.trim();
        if (e.startsWith("=")) e = e.substring(1);
        // register each function name found in registry
        for (String fn : functionRegistry.listAvailableFunctions()) {
            // replace only function calls like FUNC(    — case-insensitive
            e = e.replaceAll("(?i)\\b" + Pattern.quote(fn) + "\\s*\\(", "#" + fn + "(");
        }
        return e;
    }

    /** validate syntax and referenced variables exist in the expression metadata */
    public ValidationResult validate(Expression exprDef) {
        // basic syntax parse
        String processed = preprocess(exprDef.getExpression());
        try {
            spELCache.getParsed(processed); // will throw if invalid
        } catch (Exception ex) {
            return ValidationResult.invalid("Syntax error: " + ex.getMessage());
        }

        // check that variables declared match variables referenced
        Set<String> referenced = referencedVariables(exprDef.getExpression());
        Map<String, VariableDefinition> declared = exprDef.getVariables() != null ? exprDef.getVariables() : Collections.emptyMap();

        for (String var : referenced) {
            // skip known function names
            if (functionRegistry.allFunctions().containsKey(var.toUpperCase(Locale.ROOT))) continue;
            if (!declared.containsKey(var)) {
                return ValidationResult.invalid("Variable referenced but not declared: " + var);
            }
        }

        return ValidationResult.valid();
    }

    private Set<String> referencedVariables(String expression) {
        Set<String> out = new HashSet<>();
        Matcher m = VAR_PATTERN.matcher(expression);
        while (m.find()) {
            out.add(m.group(1));
        }
        return out;
    }

    /** Evaluate with runtime variables. Returns an EvaluationResult (can contain trace if debug true). */
    public EvaluationResult evaluate(Expression exprDef, Map<String,Object> runtimeVars, boolean debug) {
        String processed = preprocess(exprDef.getExpression());
        StandardEvaluationContext ctx = new StandardEvaluationContext();

        // register variables as SpEL variables: #varName
        if (runtimeVars != null) runtimeVars.forEach(ctx::setVariable);

        // register functions
        for (Map.Entry<String, Method> e : functionRegistry.allFunctions().entrySet()) {
            ctx.registerFunction(e.getKey(), e.getValue());
        }

        try {
            org.springframework.expression.Expression pars = spELCache.getParsed(processed);
            Object raw = pars.getValue(ctx);
            // type check
            if (exprDef.getResultType() != null && raw != null) {
                Class<?> expected = exprDef.getResultType().getJavaType();
                if (!expected.isAssignableFrom(raw.getClass())) {
                    // allow Number -> Double/integer flexibility
                    if (Number.class.isAssignableFrom(expected) && raw instanceof Number) {
                        // fine
                    } else {
                        throw new IllegalArgumentException("Result type mismatch. Expected " + expected.getSimpleName() + " but got " + raw.getClass().getSimpleName());
                    }
                }
            }
            EvaluationResult res = new EvaluationResult(raw);
            if (debug) {
                res.addTrace("Processed expression: " + processed);
                res.addTrace("Runtime vars: " + (runtimeVars == null ? "{}" : runtimeVars.toString()));
                res.addTrace("Raw result: " + raw + " (" + (raw == null ? "null" : raw.getClass().getSimpleName()) + ")");
            }
            return res;
        } catch (Exception ex) {
            EvaluationResult err = new EvaluationResult(null);
            err.setError(ex.getMessage());
            if (debug) err.addTrace("Error: " + ex.getMessage());
            return err;
        }
    }

    /** Simple structure for validation result */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        private ValidationResult(boolean v, String m) { valid = v; message = m; }
        public static ValidationResult valid() { return new ValidationResult(true, "OK"); }
        public static ValidationResult invalid(String msg) { return new ValidationResult(false, msg); }
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }

    /** result holder with trace */
    public static class EvaluationResult {
        @Getter
        private Object result;
        private final List<String> trace = new ArrayList<>();
        @Getter
        @Setter
        private String error;

        public EvaluationResult(Object result) { this.result = result; }

        public void addTrace(String t) { trace.add(t); }
    }
}

