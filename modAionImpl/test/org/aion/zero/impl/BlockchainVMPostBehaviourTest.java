package org.aion.zero.impl;

import org.aion.crypto.ECKey;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class BlockchainVMPostBehaviourTest {

    private StandaloneBlockchain blockchain;
    private List<ECKey> accounts;

    @Before
    public void before() {
        StandaloneBlockchain.Bundle bundle = new StandaloneBlockchain.Builder()
                .withDefaultAccounts()
                .build();
        this.blockchain = bundle.bc;
        this.accounts = bundle.privateKeys;
        // TODO: this will have to change later
        this.blockchain.setBlockNumber(1_000_001L);
    }

    @Test
    public void testVMUpdatedBehaviour() {
        
    }
}
