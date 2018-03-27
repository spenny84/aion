package org.aion.db;

import org.aion.base.util.Utils;
import org.aion.crypto.ECKey;
import org.aion.db.impl.DBVendor;
import org.aion.db.impl.leveldb.LevelDBConstants;
import org.aion.mcf.core.ImportResult;
import org.aion.zero.impl.StandaloneBlockchain;
import org.aion.zero.impl.types.AionBlock;
import org.aion.zero.types.AionTransaction;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(Parameterized.class)
public class BlockchainBalanceTransferRandomizedTest {

    private String vendor;
    private int fd_alloc_size;
    private int block_size;
    private int write_buffer_size;
    private int cache_size;
    private String dbPath;
    public BlockchainBalanceTransferRandomizedTest(String name,
                                                   String vendor,
                                                   String dbPath,
                                                   int fd_alloc_size,
                                                   int block_size,
                                                   int write_buffer_size,
                                                   int cache_size) {
        this.vendor = vendor;
        this.dbPath = dbPath;
        this.fd_alloc_size = fd_alloc_size;
        this.block_size = block_size;
        this.write_buffer_size = write_buffer_size;
        this.cache_size = cache_size;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "TestH2 [Defaults]",
                        DBVendor.H2.toValue(),
                        "testH2",
                        LevelDBConstants.MAX_OPEN_FILES,
                        LevelDBConstants.BLOCK_SIZE,
                        LevelDBConstants.WRITE_BUFFER_SIZE,
                        LevelDBConstants.CACHE_SIZE
                },
                {
                        "LevelDB [fd=1024,bs=256kb,wb=16mb,cs=256mb]",
                        DBVendor.LEVELDB.toValue(),
                        "testLevelDB",
                        1024,
                        (int) (256 * Utils.KILO_BYTE),
                        (int) (16 * Utils.MEGA_BYTE),
                        (int) (256 * Utils.MEGA_BYTE)
                },
                {
                        "LevelDB [fd=1024,bs=4096kb,wb=256mb,cs=256mb]",
                        DBVendor.LEVELDB.toValue(),
                        "testLevelDB2",
                        1024,
                        (int) (4 * Utils.MEGA_BYTE),
                        (int) (64 * Utils.MEGA_BYTE),
                        (int) (256 * Utils.MEGA_BYTE)
                }
        });
    }

    private static RetCreateBundle createBundleAndCheck(StandaloneBlockchain bc, List<ECKey> keys, AionBlock parentBlock) {
        DatabaseBenchUtils benchUtils = new DatabaseBenchUtils();

        List<AionTransaction> transactions = benchUtils.generateTransactions(
                DatabaseBenchUtils.GenerationType.BALANCE,
                467,
                keys,
                bc);

        AionBlock block = bc.createNewBlock(parentBlock, transactions, true);
        assertThat(block.getTransactionsList().size()).isEqualTo(467);
        // clear the trie
        bc.getRepository().flush();

        // ms
        long startTime = System.nanoTime() / 1_000_000;
        ImportResult result = bc.tryToConnect(block);
        long endTime = System.nanoTime() / 1_000_000;
        System.out.println(block.getNumber() + "," + (endTime - startTime));

        assertThat(result).isEqualTo(ImportResult.IMPORTED_BEST);

        RetCreateBundle bundle = new RetCreateBundle();
        bundle.block = block;
        bundle.delta = endTime - startTime;
        return bundle;
    }

    /**
     * Test the effects of growing state of a single account
     */
    @Ignore
    @Test
    public void testAccountState() throws InterruptedException {
        StandaloneBlockchain bc = null;
        try {
            StandaloneBlockchain.Bundle bundle = new StandaloneBlockchain.Builder()
                    .withDefaultAccounts()
                    .withValidatorConfiguration("simple")
                    .withRepositoryConfig(new StandaloneBlockchain.DefaultTestRepositoryConfig() {
                        @Override
                        public String[] getVendorList() {
                            return new String[]{vendor};
                        }

                        @Override
                        public String getActiveVendor() {
                            return vendor;
                        }

                        @Override
                        public int getMaxFdAllocSize() {
                            return fd_alloc_size;
                        }

                        @Override
                        public String getDbPath() {
                            return dbPath;
                        }

                        @Override
                        public int getBlockSize() {
                            return block_size;
                        }

                        @Override
                        public int getWriteBufferSize() {
                            return write_buffer_size;
                        }

                        @Override
                        public int getCacheSize() {
                            return cache_size;
                        }
                    })
                    .build();

            bc = bundle.bc;
            // send a total of 100 bundles,
            // given the rate we're sending this should give us
            // a 400,000 accounts (not counting the 10 pre-generated for us)
            AionBlock previousBlock = bc.genesis;
            double total = 0.0;
            for (int i = 0; i < 500; i++) {
                RetCreateBundle retBundle = createBundleAndCheck(bc, bundle.privateKeys, previousBlock);
                previousBlock = retBundle.block;
                total = total + retBundle.delta;
                System.out.println("Current average: " + (total / (float) (i + 1)));
            }
        } finally {
            if (bc != null)
                bc.close();
            Thread.sleep(5000L);
        }
    }

    private static class RetCreateBundle {
        public AionBlock block;
        public long delta;
    }
}
