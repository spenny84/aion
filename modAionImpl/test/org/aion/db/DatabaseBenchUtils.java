package org.aion.db;

import org.aion.base.type.Address;
import org.aion.base.util.ByteUtil;
import org.aion.crypto.ECKey;
import org.aion.zero.impl.StandaloneBlockchain;
import org.aion.zero.types.AionTransaction;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseBenchUtils {

    private static final SecureRandom random = new SecureRandom();
    /**
     * Keep memory of all past contracts deployed (assuming transactions go through)
     */
    Map<Address, String> contractMap = new HashMap<>();
    /**
     * Keep memory of all nonces of accounts used (assuming transactions go through)
     */
    Map<Address, BigInteger> nonceMap = new HashMap<>();

    /**
     *
     */
    public List<AionTransaction> generateTransactions(GenerationType type, int amount, List<ECKey> keySet, StandaloneBlockchain bc) {
        int index = random.nextInt(keySet.size() - 1);

        // reset all nonces for this run
        for (ECKey key : keySet) {
            Address addr = new Address(key.getAddress());
            nonceMap.put(addr, bc.getRepository().getNonce(addr));
        }

        List<AionTransaction> transactions = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            ECKey key = keySet.get(index);
            transactions.add(generateBalanceTransaction(key, new Address(key.getAddress())));
        }
        return transactions;
    }

    private AionTransaction generateBalanceTransaction(ECKey from, Address fromAddress) {
        BigInteger nonce = this.nonceMap.get(fromAddress);
        // retrieve the nonce from map

        // generate random bytes (the account we send to)
        byte[] nextRandomBytes = new byte[32];
        random.nextBytes(nextRandomBytes);

        Address destAddr = new Address(nextRandomBytes);
        AionTransaction sendTransaction = new AionTransaction(
                nonce.toByteArray(),
                destAddr,
                BigInteger.ONE.toByteArray(),
                ByteUtil.EMPTY_BYTE_ARRAY,
                21000,
                1);
        sendTransaction.sign(from);

        // increment the nonceMap
        this.nonceMap.put(fromAddress, nonce.add(BigInteger.ONE));
        return sendTransaction;
    }

    public void clear() {
        contractMap.clear();
    }

    public enum GenerationType {
        BALANCE,
        CONTRACT,
        MIXED,
    }
}
