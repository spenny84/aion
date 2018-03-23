package org.aion.mcf.valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValidateResult {

    // list of errors that occured during execution
    private List<Map.Entry<Integer, String>> violatedRules;

    // in cases where an unknown error caused the validation to throw
    // return that back with Rule(0) violated (UnknownViolation)
    private Throwable cause;

    public void addViolatedRule(Integer rule, String violation) {
        if (this.violatedRules == null)
            this.violatedRules = new ArrayList<>();
        this.violatedRules.add(Map.entry(rule, violation));
    }

    public List<Map.Entry<Integer, String>> getViolatedRules() {
        return this.violatedRules;
    }

    public boolean isCorrect() {
        return this.cause != null || this.violatedRules == null || this.violatedRules.isEmpty();
    }
}
