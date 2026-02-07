package com.tpc.form_builder.formula;

import com.tpc.form_builder.formula.model.Expression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FormulaEvaluationService {

    private final FormulaEngine engine;

    public FormulaEngine.ValidationResult validate(Expression expression) {
        return engine.validate(expression);
    }

    public FormulaEngine.EvaluationResult evaluate(Expression expression, Map<String,Object> vars) {
        return engine.evaluate(expression, vars, false);
    }

    public FormulaEngine.EvaluationResult evaluate(Expression expression, Map<String,Object> vars, boolean debug) {
        return engine.evaluate(expression, vars, debug);
    }

    // store / retrieve templates
//    public void saveExpression(Expression e) { repo.save(e); }
//    public Optional<Expression> getExpression(String id) { return repo.findById(id); }
//    public List<Expression> listExpressions() { return repo.list(); }

    // test runner
    public List<TestRunResult> runTests(Expression expression, List<TestCase> tests) {
        List<TestRunResult> out = new java.util.ArrayList<>();
        for (TestCase t : tests) {
            FormulaEngine.EvaluationResult r = engine.evaluate(expression, t.getVars(), false);
            boolean ok = t.getExpected() == null ? r.getResult() == null : t.getExpected().equals(r.getResult());
            out.add(new TestRunResult(t, r.getResult(), ok, r.getError()));
        }
        return out;
    }

    public static class TestCase {
        private final Map<String,Object> vars;
        private final Object expected;
        public TestCase(Map<String,Object> vars, Object expected){ this.vars=vars; this.expected=expected;}
        public Map<String,Object> getVars(){return vars;}
        public Object getExpected(){return expected;}
    }

    public static class TestRunResult {
        private final TestCase testcase;
        private final Object actual;
        private final boolean ok;
        private final String error;
        public TestRunResult(TestCase testcase, Object actual, boolean ok, String error){
            this.testcase=testcase; this.actual=actual; this.ok=ok; this.error=error;
        }
        public TestCase getTestcase(){return testcase;}
        public Object getActual(){return actual;}
        public boolean isOk(){return ok;}
        public String getError(){return error;}
    }
}

