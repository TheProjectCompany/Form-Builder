package com.tpc.form_builder.formula.cache;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SpELCache {
    private final ExpressionParser parser = new SpelExpressionParser();
    private final ConcurrentHashMap<String, Expression> cache = new ConcurrentHashMap<>();

    public Expression getParsed(String expr) {
        return cache.computeIfAbsent(expr, parser::parseExpression);
    }

    public void clear() { cache.clear(); }
}

