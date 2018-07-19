package org.aion.zero.impl;

import org.aion.base.type.Address;
import org.aion.base.util.ByteUtil;
import org.aion.crypto.ECKey;
import org.aion.crypto.HashUtil;
import org.aion.mcf.core.ImportResult;
import org.aion.mcf.vm.types.Log;
import org.aion.zero.impl.types.AionBlock;
import org.aion.zero.impl.types.AionTxInfo;
import org.aion.zero.types.AionTransaction;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class BlockchainVMPostBehaviourTest {

    private StandaloneBlockchain blockchain;
    private List<ECKey> accounts;

    private static final byte[] INTERNAL_EVENT_CONTRACT = ByteUtil.hexStringToBytes("605060405234156100105760006000fd5b610015565b61020a806100246000396000f30060506040526000356c01000000000000000000000000900463ffffffff1680632411e5861461005f57806326e946f314610075578063514bc7941461008b57806370f46c64146100a1578063ca0a9de5146100b757610059565b60006000fd5b341561006b5760006000fd5b6100736100cd565b005b34156100815760006000fd5b6100896100fc565b005b34156100975760006000fd5b61009f610139565b005b34156100ad5760006000fd5b6100b5610176565b005b34156100c35760006000fd5b6100cb6101aa565b005b7f5278c0faa73f0ffb89413565943e77a57b668f737bdc1c381f6b8a3dc6c77cd160405160405180910390a15b565b7f5278c0faa73f0ffb89413565943e77a57b668f737bdc1c381f6b8a3dc6c77cd160405160405180910390a1600015156101365760006000fd5b5b565b7f5278c0faa73f0ffb89413565943e77a57b668f737bdc1c381f6b8a3dc6c77cd160405160405180910390a1600015156101735760006000fd5b5b565b7f5278c0faa73f0ffb89413565943e77a57b668f737bdc1c381f6b8a3dc6c77cd160405160405180910390a160006000fd5b565b7f5278c0faa73f0ffb89413565943e77a57b668f737bdc1c381f6b8a3dc6c77cd160405160405180910390a160006000fd5b5600a165627a7a72305820fd1e866f9cfec916ae93848cfba3e7312cd33965220ee1472e5d0b31fe35439a0029");
    private static final byte[] EMIT_EVENT_PASS = ByteUtil.hexStringToBytes("2411e586");
    private static final byte[] EMIT_EVENT_THROW = ByteUtil.hexStringToBytes("ca0a9de5");
    private static final byte[] EMIT_EVENT_REVERT = ByteUtil.hexStringToBytes("514bc794");

    private static final byte[] EMIT_WRAPPER_PASS = ByteUtil.hexStringToBytes("41343876");
    private static final byte[] EMIT_WRAPPER_THROW = ByteUtil.hexStringToBytes("6130baf9");

    private static final byte[] EXTERNAL_EVENT_CONTRACT = ByteUtil.hexStringToBytes("605060405234156100105760006000fd5b610015565b61047b806100246000396000f30060506040526000356c01000000000000000000000000900463ffffffff16806341343876146100495780636050f2ac1461005f5780636130baf91461007557610043565b60006000fd5b34156100555760006000fd5b61005d61008b565b005b341561006b5760006000fd5b61007361010d565b005b34156100815760006000fd5b61008961018f565b005b60006000610097610211565b604051809103906000f08015821516156100b15760006000fd5b915091508181632411e5866040518163ffffffff166c010000000000000000000000000281526004016000604051808303816000888881813b15156100f65760006000fd5b5af115156101045760006000fd5b505050505b5050565b60006000610119610211565b604051809103906000f08015821516156101335760006000fd5b91509150818163514bc7946040518163ffffffff166c010000000000000000000000000281526004016000604051808303816000888881813b15156101785760006000fd5b5af115156101865760006000fd5b505050505b5050565b6000600061019b610211565b604051809103906000f08015821516156101b55760006000fd5b91509150818163ca0a9de56040518163ffffffff166c010000000000000000000000000281526004016000604051808303816000888881813b15156101fa5760006000fd5b5af115156102085760006000fd5b505050505b5050565b60405161022e80610222833901905600605060405234156100105760006000fd5b610015565b61020a806100246000396000f30060506040526000356c01000000000000000000000000900463ffffffff1680632411e5861461005f57806326e946f314610075578063514bc7941461008b57806370f46c64146100a1578063ca0a9de5146100b757610059565b60006000fd5b341561006b5760006000fd5b6100736100cd565b005b34156100815760006000fd5b6100896100fc565b005b34156100975760006000fd5b61009f610139565b005b34156100ad5760006000fd5b6100b5610176565b005b34156100c35760006000fd5b6100cb6101aa565b005b7f5278c0faa73f0ffb89413565943e77a57b668f737bdc1c381f6b8a3dc6c77cd160405160405180910390a15b565b7f5278c0faa73f0ffb89413565943e77a57b668f737bdc1c381f6b8a3dc6c77cd160405160405180910390a1600015156101365760006000fd5b5b565b7f5278c0faa73f0ffb89413565943e77a57b668f737bdc1c381f6b8a3dc6c77cd160405160405180910390a1600015156101735760006000fd5b5b565b7f5278c0faa73f0ffb89413565943e77a57b668f737bdc1c381f6b8a3dc6c77cd160405160405180910390a160006000fd5b565b7f5278c0faa73f0ffb89413565943e77a57b668f737bdc1c381f6b8a3dc6c77cd160405160405180910390a160006000fd5b5600a165627a7a72305820fd1e866f9cfec916ae93848cfba3e7312cd33965220ee1472e5d0b31fe35439a0029a165627a7a723058206707450ac4df913214f804fa9d9897d4079b235e877ec578ba447f4a00c32b7a0029");

    // all function calls emit this event sig
    private static final byte[] EVENT_SIG = ByteUtil.hexStringToBytes("5278c0faa73f0ffb89413565943e77a57b668f737bdc1c381f6b8a3dc6c77cd1");

    private Address contractAddress = null;

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

    private ImportResult execute(AionTransaction tx) {
        AionBlock block = this.blockchain.createNewBlock(this.blockchain.getBestBlock(), Arrays.asList(tx), false);
        assertThat(block.getTransactionsList().size()).isEqualTo(1);
        ImportResult result = this.blockchain.tryToConnect(block);

        if (tx.getTo() == null) {
            this.contractAddress = tx.getContractAddress();
            assertThat(this.contractAddress).isNotNull();
        }
        return result;
    }

    // for contract creations
    private AionTransaction deployContract(byte[] data) {
        return createContract(data, null, 1_000_000L);
    }

    private AionTransaction callContract(byte[] data, long nrg) {
        return createContract(data, this.contractAddress, nrg);
    }

    private AionTransaction createContract(byte[] data, Address to, long nrg) {
        ECKey sender = this.accounts.get(0);
        byte[] nonce = this.blockchain.getRepository().getNonce(new Address(sender.getAddress())).toByteArray();
        AionTransaction tx = new AionTransaction(
                nonce, to, ByteUtil.EMPTY_BYTE_ARRAY, data, nrg, 1);
        tx.sign(sender);
        return tx;
    }

    private AionTxInfo getInfo(byte[] hash) {
        return this.blockchain.getRepository().getTransactionStore().get(hash).get(0);
    }

    private AionTxInfo executeInternalContract(byte[] call) {
        return executeInternalContract(call, 1_000_000L);
    }

    private AionTxInfo executeInternalContract(byte[] call, long nrg) {
        return executeContract(INTERNAL_EVENT_CONTRACT, call, nrg);
    }

    private AionTxInfo executeWrapperContract(byte[] call) {
        return executeWrapperContract(call, 1_000_000L);
    }

    private AionTxInfo executeWrapperContract(byte[] call, long nrg) {
        return executeContract(EXTERNAL_EVENT_CONTRACT, call, nrg);
    }

    private AionTxInfo executeContract(byte[] contract, byte[] call, long nrg) {
        AionTransaction createInternalEventContractTx = deployContract(contract);
        ImportResult result = execute(createInternalEventContractTx);
        assertThat(result).isEqualTo(ImportResult.IMPORTED_BEST);

        // function call
        AionTransaction eventTx = callContract(call, nrg);
        result = execute(eventTx);
        assertThat(result).isEqualTo(ImportResult.IMPORTED_BEST);
        return getInfo(eventTx.getHash());
    }


    // tests purely on contract logic throwing, reverting

    @Test
    public void testVMPassingEventBehaviour() {
        AionTxInfo info = executeInternalContract(EMIT_EVENT_PASS);
        assertThat(info.getReceipt().getLogInfoList().size()).isEqualTo(1);

        // ensure that the log is emit from the contractAddress
        assertThat(info.getReceipt().getLogInfoList().get(0).getAddress()).isEqualTo(this.contractAddress);

        // we don't have any indexed parameters, assume only one parameter (topic)
        assertThat(info.getReceipt().getLogInfoList().get(0).getTopics().size()).isEqualTo(1);
        assertThat(info.getReceipt().getLogInfoList().get(0).getTopics().get(0)).isEqualTo(EVENT_SIG);
    }

    @Test
    public void testVMThrowEventBehaviour() {
        AionTxInfo info = executeInternalContract(EMIT_EVENT_THROW);
        assertThat(info.getReceipt().getLogInfoList().size()).isEqualTo(0);
    }

    @Test
    public void testVMRevertEventBehaviour() {
        AionTxInfo info = executeInternalContract(EMIT_EVENT_REVERT);
        assertThat(info.getReceipt().getLogInfoList().size()).isEqualTo(0);
    }

    // test out-of-gas behaviour
    @Test
    public void testVMOOGBehaviour() {
        // 22296 is passing, take one less
        AionTxInfo info = executeInternalContract(EMIT_EVENT_PASS, 22295);
        assertThat(info.getReceipt().getError()).isEqualTo("OUT_OF_NRG");
        assertThat(info.getReceipt().getLogInfoList().size()).isEqualTo(0);
    }


    // test wrapper contract
    @Test
    public void testWrapperEventPass() {
        AionTxInfo info = executeWrapperContract(EMIT_WRAPPER_PASS);
        assertThat(info.getReceipt().getLogInfoList().size()).isEqualTo(1);

        Address expectedAddr = new Address(HashUtil.calcNewAddr(this.contractAddress.toBytes(),
                BigInteger.ZERO.toByteArray()));
        Log l = info.getReceipt().getLogInfoList().get(0);
        assertThat(l.getAddress()).isEqualTo(expectedAddr);

        assertThat(l.getTopics().get(0)).isEqualTo(EVENT_SIG);
    }

    @Test
    public void testWrapperEventThrow() {
        AionTxInfo info = executeWrapperContract(EMIT_WRAPPER_THROW);
        assertThat(info.getReceipt().getLogInfoList().size()).isEqualTo(0);
        assertThat(info.getReceipt().getError()).isEqualTo("REVERT");
    }
}
