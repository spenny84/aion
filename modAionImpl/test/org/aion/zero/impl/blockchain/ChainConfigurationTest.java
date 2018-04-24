/*******************************************************************************
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
 *     The aion network project leverages useful source code from other
 *     open source projects. We greatly appreciate the effort that was
 *     invested in these projects and we thank the individual contributors
 *     for their work. For provenance information and contributors
 *     please see <https://github.com/aionnetwork/aion/wiki/Contributors>.
 *
 * Contributors to the aion source files in decreasing order of code volume:
 *     Aion foundation.
 *     <ether.camp> team through the ethereumJ library.
 *     Ether.Camp Inc. (US) team through Ethereum Harmony.
 *     John Tromp through the Equihash solver.
 *     Samuel Neves through the BLAKE2 implementation.
 *     Zcash project team.
 *     Bitcoinj team.
 ******************************************************************************/
package org.aion.zero.impl.blockchain;

import org.aion.base.util.ByteUtil;
import org.aion.equihash.EquiUtils;
import org.aion.equihash.Equihash;
import org.aion.mcf.valid.BlockHeaderValidator;
import org.aion.zero.api.BlockConstants;
import org.aion.zero.exceptions.HeaderStructureException;
import org.aion.zero.impl.core.RewardsCalculator;
import org.aion.zero.types.A0BlockHeader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;

import static com.google.common.truth.Truth.assertThat;
import static org.aion.base.util.ByteUtil.toLEByteArray;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


public class ChainConfigurationTest {

    private static final Logger log = LoggerFactory.getLogger(ChainConfigurationTest.class);

    @Mock
    A0BlockHeader header;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Ignore //To be re-enabled later
    @Test
    public void testValidation() throws HeaderStructureException {
        int n = 210;
        int k = 9;
        byte[] nonce = {1,0,0,0,0,0,0,
                        0,0,0,0,0,0,0,
                        0,0,0,0,0,0,0,
                        0,0,0,0,0,0,0};
        // setup mock
//        A0BlockHeader.Builder builder = new A0BlockHeader.Builder();
//        builder.withDifficulty(BigInteger.valueOf(1).toByteArray());
//        builder.withNonce(nonce);
//        builder.withTimestamp(12345678910L);
//        A0BlockHeader header = builder.build();
//
//        // Static header bytes (portion of header which does not change per equihash iteration)
//        byte [] staticHeaderBytes = header.getStaticHash();
//
//        // Dynamic header bytes
//        long timestamp = header.getTimestamp();
//
//        // Dynamic header bytes (portion of header which changes each iteration0
//        byte[] dynamicHeaderBytes = ByteUtil.longToBytes(timestamp);
//
//        BigInteger target = header.getPowBoundaryBI();
//
//        //Merge H(static) and dynamic portions into a single byte array
//        byte[] inputBytes = new byte[staticHeaderBytes.length + dynamicHeaderBytes.length];
//        System.arraycopy(staticHeaderBytes, 0, inputBytes, 0 , staticHeaderBytes.length);
//        System.arraycopy(dynamicHeaderBytes, 0, inputBytes, staticHeaderBytes.length, dynamicHeaderBytes.length);
//
//        Equihash equihash = new Equihash(n, k);
//
//        int[][] solutions;
//
//        // Generate 3 solutions
//        solutions = equihash.getSolutionsForNonce(inputBytes, header.getNonce());
//
//        // compress solution
//        byte[] compressedSolution = EquiUtils.getMinimalFromIndices(solutions[0], n/(k+1));
//        header.setSolution(compressedSolution);
//
//        ChainConfiguration chainConfig = new ChainConfiguration();
//        BlockHeaderValidator<A0BlockHeader> blockHeaderValidator = chainConfig.createBlockHeaderValidator();
//        blockHeaderValidator.validate(header, log);
    }

    // assuming 100000 block ramp
    @Test
    public void testRampUpFunctionBoundaries() {
        long upperBound = 259200L;

        ChainConfiguration config = new ChainConfiguration();
        BigInteger increment = config.getConstants()
                .getBlockReward()
                .subtract(config.getConstants().getRampUpStartValue())
                .divide(BigInteger.valueOf(upperBound))
                .add(config.getConstants().getRampUpStartValue());

        // UPPER BOUND
        when(header.getNumber()).thenReturn(upperBound);
        BigInteger blockReward259200 = config.getRewardsCalculator().calculateReward(header);

        when(header.getNumber()).thenReturn(upperBound + 1);
        BigInteger blockReward259201 = config.getRewardsCalculator().calculateReward(header);

        // check that at the upper bound of our range (which is not included) blockReward is capped
        assertThat(blockReward259200).isEqualTo(new BigInteger("1497989283243258292"));

        // check that for the block after, the block reward is still the same
        assertThat(blockReward259201).isEqualTo(config.getConstants().getBlockReward());

        // check that for an arbitrarily large block, the block reward is still the same
        when(header.getNumber()).thenReturn(upperBound + 100000);
        BigInteger blockUpper = config.getRewardsCalculator().calculateReward(header);
        assertThat(blockUpper).isEqualTo(config.getConstants().getBlockReward());

        // LOWER BOUNDS
        when(header.getNumber()).thenReturn(0l);
        BigInteger blockReward0 = config.getRewardsCalculator().calculateReward(header);
        assertThat(blockReward0).isEqualTo(new BigInteger("748994641621655092"));

        // first block (should have gas value of increment)
        when(header.getNumber()).thenReturn(1l);
        BigInteger blockReward1 = config.getRewardsCalculator().calculateReward(header);
        assertThat(blockReward1).isEqualTo(increment);
    }

    @Mock
    A0BlockHeader mockHeader;


    /**
     * Less of a test, more for visual communication with business team about
     * the exact amount of block rewards
     */
    @Test
    public void testPrintRampUp() {
        long lastBlock = 259200;
        ChainConfiguration config = new ChainConfiguration();
        BigInteger increment = config.getConstants()
                .getBlockReward()
                .subtract(config.getConstants().getRampUpStartValue())
                .divide(BigInteger.valueOf(((BlockConstants) config.getConstants()).getRampUpUpperBound()));

        RewardsCalculator calculator = new RewardsCalculator((BlockConstants) config.getConstants());

        var rewards = new ArrayList<BigInteger>();
        // for the 0th day
        rewards.add(BigInteger.ZERO);
        // inclusive
        // genesis gives no rewards
        for (int i = 1; i <= lastBlock; i++) {
            when(mockHeader.getNumber()).thenReturn((long) i);
            BigInteger reward = calculator.calculateReward(mockHeader);
            rewards.add(reward);
//            System.out.println(i + "," + reward);
        }

        BigInteger accum = BigInteger.ZERO;
        for (int i = 0; i <= 8640; i++) {
            accum = accum.add(rewards.get(i));
        }


        BigInteger secondAccum = BigInteger.ZERO;
        for (int i = 8641; i <= (8640*2); i++) {
            secondAccum = secondAccum.add(rewards.get(i));
        }

        System.out.println("day 2-1 delta: " + secondAccum.subtract(accum));

        // note here that the 0th block does not give rewards
        System.out.println("first [0, 8640] blocks: " + accum);
        System.out.println("[8641, 8640 * 2] blocks: " + secondAccum);


        BigInteger secondLastAccum = BigInteger.ZERO;
        for (int i = rewards.size() - (8640 * 2); i < rewards.size() - 8640; i++) {
            secondLastAccum = secondLastAccum.add(rewards.get(i));
        }

        BigInteger lastAccum = BigInteger.ZERO;
        for (int i = rewards.size() - 8640; i < rewards.size(); i++) {
            lastAccum = lastAccum.add(rewards.get(i));
        }
        System.out.println("last [250560, 259200] blocks: " + lastAccum);
        System.out.println("second last: " + secondLastAccum);
        System.out.println("day 30-29 delta: " + lastAccum.subtract(secondLastAccum));

        System.out.println("post final block reward: " + config.getConstants().getBlockReward());

        System.out.println("total rewards given during rampup: " + rewards.stream().reduce(BigInteger.ZERO, BigInteger::add));
    }
}
