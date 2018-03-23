package org.aion.mcf.valid;

import org.aion.base.type.IBlockHeader;
import org.aion.mcf.blockchain.IBlockConstants;
import org.aion.mcf.types.AbstractBlock;
import org.aion.mcf.types.AbstractTransaction;

public class AbstractBlockValidator<TX extends AbstractTransaction, BH extends IBlockHeader> {
    protected IBlockConstants blockConstants;


    public AbstractBlockValidator(IBlockConstants blockConstants) {
        this.blockConstants = blockConstants;
    }

    public ValidateResult validateBlockStructure(AbstractBlock<BH, TX> block) {
        ValidateResult result = new ValidateResult();
        IBlockHeader header = block.getHeader();

//        if (!validateHashLengthRule(header.getCoinbase().toBytes()))
//            result.addViolatedRule(COINBASE_RULE, null);
//
//        if (!validateHashLengthRule(header.getStateRoot())) {
//            result.addViolatedRule();
//        }
        return null;
    }

    // define hash to be a byte array of length 32 that exists
    protected static boolean validateHashLengthRule(byte[] coinbase) {
        if (coinbase == null)
            return false;

        if (coinbase.length != 32)
            return false;
        return true;
    }
}
