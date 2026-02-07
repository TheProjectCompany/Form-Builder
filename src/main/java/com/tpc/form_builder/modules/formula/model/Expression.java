package com.tpc.form_builder.modules.formula.model;
import java.util.Map;

public class Expression {
    private final String id; // optional id for registry
    private final String expression; // the formula string (may include variable names)
    private final ExpressionType type;
    private final ResultType resultType;
    private final Map<String, VariableDefinition> variables; // name -> def
    private final String description;
    private final int version;

    public Expression(String id, String expression, ExpressionType type, ResultType resultType,
                      Map<String, VariableDefinition> variables, String description, int version) {
        this.id = id;
        this.expression = expression;
        this.type = type;
        this.resultType = resultType;
        this.variables = variables;
        this.description = description;
        this.version = version;
    }

    public String getId() { return id; }
    public String getExpression() { return expression; }
    public ExpressionType getType() { return type; }
    public ResultType getResultType() { return resultType; }
    public Map<String, VariableDefinition> getVariables() { return variables; }
    public String getDescription() { return description; }
    public int getVersion() { return version; }
}

