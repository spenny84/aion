package org.aion.mcf.valid;

import org.aion.base.type.IBlockHeader;
import org.aion.mcf.blockchain.IBlockConstants;
import org.aion.mcf.types.AbstractBlock;
import org.aion.mcf.types.AbstractTransaction;

public abstract class AbstractBlockValidator<TX extends AbstractTransaction, BH extends IBlockHeader> {
    protected IBlockConstants blockConstants;

    public AbstractBlockValidator(IBlockConstants blockConstants) {
        this.blockConstants = blockConstants;
    }

    public ValidateResult validateBlockStructure(AbstractBlock<BH, TX> block) {
        ValidateResult result = new ValidateResult();
        IBlockHeader header = block.getHeader();

        if (!validateHashLengthRule(header.getCoinbase().toBytes()))
            result.addViolatedRule(RuleSet.COINBASE_LENGTH_RULE, null);

        if (!validateHashLengthRule(header.getStateRoot())) {
            result.addViolatedRule(RuleSet.STATE_ROOT_LENGTH_RULE, null);
        }

        if (!validateHashLengthRule(header.getTxTrieRoot())) {
            result.addViolatedRule(RuleSet.TX_TRIE_LENGTH_RULE, null);
        }

        if (!validateHashLengthRule(header.getReceiptsRoot())) {
            result.addViolatedRule(RuleSet.RECEIPT_TRIE_LENGTH_RULE, null);
        }

        if (!validateLogsBloomLengthRule(header.getLogsBloom())) {
            result.addViolatedRule(RuleSet.LOGS_BLOOM_LENGTH_RULE, null);
        }

        // TODO: missing number length rule
        // TODO: missing timestamp length rule

        if (!validateHashMaxLengthRule(header.getExtraData())) {
            result.addViolatedRule(RuleSet.EXTRA_DATA_MAX_LENGTH_RULE, null);
        }

        if (!validateHashMaxLengthRule(header.getNonce())) {
            result.addViolatedRule(RuleSet.EXTRA_DATA_MAX_LENGTH_RULE, null);
        }

        // TODO: missing energy consumed rule
        // TODO: missing energy limit rule

        return result;
    }

    public ValidateResult validateIndependantRule(AbstractBlock<BH, TX> block) {
        return null;
    }

    // define hash to be a byte array of length 32 that exists
    protected static boolean validateHashLengthRule(byte[] coinbase) {
        return validateLength(coinbase, 32);
    }

    protected static boolean validateLogsBloomLengthRule(byte[] logsBloom) {
        return validateLength(logsBloom, 2048);
    }

    protected static boolean validateLength(byte[] input, int len) {
        if (input == null)
            return false;

        if (input.length != len)
            return false;
        return true;
    }


    protected static boolean validateHashMaxLengthRule(byte[] input) {
        return validateMaxLength(input, 32);
    }

    protected static boolean validateMaxLength(byte[] input, int len) {
        if (input == null)
            return false;

        if (input.length > len)
            return false;
        return true;
    }
}
