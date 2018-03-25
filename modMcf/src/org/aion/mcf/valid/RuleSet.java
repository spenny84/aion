package org.aion.mcf.valid;

import java.util.Map;

public class RuleSet {
    private RuleSet() {}

    // this is technically rule 0
    private static final String UNKNOWN_RULE = "UnknownViolation";

    public static final int COINBASE_LENGTH_RULE            = 1;
    public static final int STATE_ROOT_LENGTH_RULE          = 2;
    public static final int TX_TRIE_LENGTH_RULE             = 3;
    public static final int RECEIPT_TRIE_LENGTH_RULE        = 4;
    public static final int LOGS_BLOOM_LENGTH_RULE          = 5;
    public static final int NUMBER_MAX_SIZE_RULE            = 6;
    public static final int TIMESTAMP_MAX_SIZE_RULE         = 7;
    public static final int EXTRA_DATA_MAX_LENGTH_RULE      = 8;
    public static final int NONCE_MAX_SIZE_RULE             = 9;
    public static final int ENERGY_CONSUMED_MAX_SIZE_RULE   = 10;
    public static final int ENERGY_LIMIT_MAX_SIZE_RULE      = 11;

    // base independant validation rules (100)
    public static final int POW_DIFFICULTY_RULE             = 100;
    public static final int ENERGY_CONSUMED_LIMIT_RULE      = 101;

    // start dependant validation rules (200)
    public static final int BLOCK_NUMBER_RULE               = 200;
    public static final int ENERGY_LIMIT_RULE               = 201;
    public static final int TIMESTAMP_RULE                  = 202;

    // start post-execution dependant validation rules (300)
    public static final int ENERGY_MATCH_RULE               = 300;
    public static final int RECEIPT_HASH_MATCH_RULE         = 301;
    public static final int LOGS_BLOOM_MATCH_RULE           = 302;
    public static final int WORLD_STATE_MATCH_RULE          = 303;

    private static final Map<Integer, String> rules = new java.util.HashMap<>();
    static {
        // add in base rules
        // called when an unexpected event happened
        rules.put(0, "UnknownViolation");

        // start structural rules (1)
        rules.put(COINBASE_LENGTH_RULE,         "CoinbaseLengthRule");
        rules.put(STATE_ROOT_LENGTH_RULE,       "StateRootLengthRule");
        rules.put(TX_TRIE_LENGTH_RULE,          "TxTrieLengthRule");
        rules.put(RECEIPT_TRIE_LENGTH_RULE,     "ReceiptTrieLengthRule");
        rules.put(LOGS_BLOOM_LENGTH_RULE,       "LogsBloomLengthRule");
        rules.put(NUMBER_MAX_SIZE_RULE,         "NumberMaxSizeRule");
        rules.put(TIMESTAMP_MAX_SIZE_RULE,      "TimestampMaxSizeRule");
        rules.put(EXTRA_DATA_MAX_LENGTH_RULE,   "ExtradataMaxLengthRule");
        rules.put(NONCE_MAX_SIZE_RULE,          "NonceMaxSizeRule");
        rules.put(ENERGY_CONSUMED_MAX_SIZE_RULE,"EnergyConsumedMaxSizeRule");
        rules.put(ENERGY_LIMIT_MAX_SIZE_RULE,   "EnergyLimitMaxSizeRule");

        // start independant validation rules (100)
        rules.put(POW_DIFFICULTY_RULE,          "PoWDifficultyRule");
        rules.put(ENERGY_CONSUMED_LIMIT_RULE,   "EnergyConsumedLimitRule");

        // start dependant validation rules (200)
        rules.put(BLOCK_NUMBER_RULE,            "BlockNumberRule");
        rules.put(ENERGY_LIMIT_RULE,            "EnergyLimitRule");
        rules.put(TIMESTAMP_RULE,               "TimestampRule");

        // start post-execution dependant validation rules (300)
        rules.put(ENERGY_MATCH_RULE,            "EnergyMatchRule");
        rules.put(RECEIPT_HASH_MATCH_RULE,      "ReceiptHashMatchRule");
        rules.put(LOGS_BLOOM_MATCH_RULE,        "LogsBloomMatchRule");
        rules.put(WORLD_STATE_MATCH_RULE,       "WorldStateMatchRule");
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
