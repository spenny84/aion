package org.aion.mcf.valid;

import java.util.Map;

public class RuleSet {
    private RuleSet() {}

    // this is technically rule 0
    private static final String UNKNOWN_RULE = "UnknownViolation";

    private static final Map<Integer, String> rules = new java.util.HashMap<>();
    static {
        // add in base rules
        // called when an unexpected event happened
        rules.put(0, "Unknown Violation");

        // start structural rules (1)
        rules.put(1,    "CoinbaseLengthRule");
        rules.put(2,    "StateRootLengthRule");
        rules.put(3,    "TxTrieLengthRule");
        rules.put(4,    "ReceiptTrieLengthRule");
        rules.put(5,    "LogsBloomLengthRule");
        rules.put(6,    "DifficultyMaxLengthRule");
        rules.put(7,    "NumberMaxSizeRule");
        rules.put(8,    "TimestampMaxSizeRule");
        rules.put(9,    "ExtradataMaxLengthRule");
        rules.put(10,   "NonceMaxSizeRule");
        rules.put(11,   "EnergyConsumedMaxSizeRule");
        rules.put(12,   "EnergyLimitMaxSizeRule");

        // start independant validation rules (100)
        rules.put(100,  "PoWDifficultyRule");
        rules.put(101,  "EnergyConsumedLimitRule");

        // start dependant validation rules (200)
        rules.put(200, "BlockNumberRule");
        rules.put(201, "EnergyLimitRule");
        rules.put(202, "TimestampRule");

        // start post-execution dependant validation rules (300)
        rules.put(300, "EnergyMatchRule");
        rules.put(301, "ReceiptHashMatchRule");
        rules.put(302, "LogsBloomMatchRule");
        rules.put(303, "WorldStateMatchRule");
    }

    private static String getRuleName(Integer ruleNumber) {
        String rule;
        if ((rule = rules.get(ruleNumber)) != null) {
            return rule;
        }
        return UNKNOWN_RULE;
    }

    /**
     * Adds additional rules to the rules mapping, allowing us to extend
     * the parameters the base implementation is concerned about.
     *
     * Anyone adding new elements into the map must not override
     * any existing elements.
     */
    public static void addRules(Map<Integer, String> additionalRules) {
        synchronized (rules) {
            rules.putAll(additionalRules);
        }
    }
}
