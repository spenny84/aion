/*
 * Copyright (c) 2017-2018 Aion foundation.
 *
 *     This file is part of the aion network project.
 *
 *     The aion network project is free software: you can redistribute it
 *     and/or modify it under the terms of the GNU General Public License
 *     as published by the Free Software Foundation, either version 3 of
 *     the License, or any later version.
 *
 *     The aion network project is distributed in the hope that it will
 *     be useful, but WITHOUT ANY WARRANTY; without even the implied
 *     warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *     See the GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with the aion network project source files.
 *     If not, see <https://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Aion foundation.
 */
package org.aion.zero.impl;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.aion.base.db.IContractDetails;
import org.aion.base.db.IPruneConfig;
import org.aion.base.db.IRepositoryConfig;
import org.aion.db.impl.DBVendor;
import org.aion.db.impl.DatabaseFactory;
import org.aion.log.AionLoggerFactory;
import org.aion.mcf.config.CfgPrune;
import org.aion.zero.impl.config.CfgAion;
import org.aion.zero.impl.db.ContractDetailsAion;
import org.aion.zero.impl.types.AionBlock;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;

@FixMethodOrder()
public class AionHubTest {

    private IRepositoryConfig repoConfig =
        new IRepositoryConfig() {
            @Override
            public String getDbPath() {
                return "";
            }

            @Override
            public IPruneConfig getPruneConfig() {
                return new CfgPrune(false);
            }

            @Override
            public IContractDetails contractDetailsImpl() {
                return ContractDetailsAion.createForTesting(0, 1000000).getDetails();
            }

            @Override
            public Properties getDatabaseConfig(String db_name) {
                Properties props = new Properties();
                props.setProperty(DatabaseFactory.Props.DB_TYPE, DBVendor.MOCKDB.toValue());
                props.setProperty(DatabaseFactory.Props.ENABLE_HEAP_CACHE, "false");
                return props;
            }
        };

    private void checkHubNullity(AionHub hub) {
        assertNotNull(hub);
        assertNotNull(hub.getBlockchain());
        assertNotNull(hub.getPendingState());
        assertNotNull(hub.getP2pMgr());
        assertNotNull(hub.getRepository());
        assertNotNull(hub.getEventMgr());
        assertNotNull(hub.getSyncMgr());
        assertNotNull(hub.getBlockStore());
        assertNotNull(hub.getPropHandler());
    }


    @BeforeClass
    public static void setup() {
        // logging to see errors
        Map<String, String> cfg = new HashMap<>();
        cfg.put("GEN", "INFO");

        AionLoggerFactory.init(cfg);
    }

    @Test
    public void AionHubInst() {

        AionHub hub = AionHub.inst();
        checkHubNullity(hub);

        hub.close();
        assertFalse(hub.isStart());
    }

    @Test
    public void AionHubfromTestCfg() {

        AionHub hub = AionHub.createHubForTest(repoConfig);
        checkHubNullity(hub);

        hub.close();
        assertFalse(hub.isStart());
    }

    @Test
    public void AionHubEnableTxBackup() {

        CfgAion cfg = CfgAion.inst();
        cfg.getTx().setPoolBackup(true);

        AionHub hub = AionHub.createHubForTest(repoConfig);
        checkHubNullity(hub);

        assertTrue(hub.isBackupTxLoaded());

        hub.close();
        assertFalse(hub.isStart());
    }

    @Test
    public void AionHubEnableReport() {

        CfgAion cfg = CfgAion.inst();
        cfg.getReports().setEnable();

        AionHub hub = AionHub.createHubForTest(repoConfig);
        checkHubNullity(hub);

        assertTrue(hub.isReports());

        hub.close();
        assertFalse(hub.isStart());
    }

    @Test
    public void AionHubEnableHeadDump() {

        CfgAion cfg = CfgAion.inst();
        cfg.getReports().setHeapDumpEnable();

        AionHub hub = AionHub.createHubForTest(repoConfig);
        checkHubNullity(hub);

        assertTrue(hub.isHeadDump());

        boolean match = false;
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals("dump-heap")) {
                match = true;
                break;
            }
        }

        assertTrue(match);

        hub.close();
        assertFalse(hub.isStart());
    }

    @Test
    public void AionHubGetRepoVersion() {
        assertEquals(AionHub.getRepoVersion(), Version.REPO_VERSION);
    }

    @Test
    public void AionHubGetStartingBlock() {
        AionHub hub = AionHub.createHubForTest(repoConfig);
        checkHubNullity(hub);

        AionBlock blk = hub.getStartingBlock();
        assertNotNull(blk);
        assertEquals(blk.getNumber(), 0);
    }
}
