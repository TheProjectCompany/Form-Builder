package com.tpc.form_builder.formula;

import com.tpc.form_builder.formula.model.Expression;
import com.tpc.form_builder.formula.model.ExpressionType;
import com.tpc.form_builder.formula.model.ResultType;
import com.tpc.form_builder.formula.model.VariableDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class Example {
    private final FormulaEvaluationService formulaEvaluationService;

    public void sampleUsage() {
        // build Expression once (define variable names + types, result type)
        Expression expr1 = new Expression(
                "expr1",
                "=IF(SUM(var1,var2) > 10, 'High', IF(SUM(var1,var2) > 5, 'Medium', 'Low'))",
                ExpressionType.CONDITIONAL,
                ResultType.STRING,
                Map.of(
                        "var1", new VariableDefinition("var1", Double.class),
                        "var2", new VariableDefinition("var2", Double.class)
                ),
                "Example for nested IF and SUM",
                1
        );

        // Evaluate later with runtime values
        var eval = formulaEvaluationService.evaluate(expr1, Map.of("var1", 3, "var2", 4), true);
        System.out.println("Result = " + eval.getResult());
    }
}

