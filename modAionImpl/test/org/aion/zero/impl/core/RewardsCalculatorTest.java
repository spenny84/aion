package org.aion.zero.impl.core;

import org.aion.zero.api.BlockConstants;
import org.aion.zero.types.A0BlockHeader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;

import static org.mockito.Mockito.when;

public class RewardsCalculatorTest {

    @Mock
    A0BlockHeader mockHeader;

    private static final BlockConstants constants = new BlockConstants();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPrintAllRewards() {
        RewardsCalculator rewardsCalculator = new RewardsCalculator(new BlockConstants());

        // first print out genesis
        System.out.println("0,0");

        for (int i = 1; i <= constants.getRampUpUpperBound(); i++) {
            when(mockHeader.getNumber()).thenReturn((long) i) ;
            BigInteger baseReward = rewardsCalculator.calculateReward(mockHeader);
            System.out.println(i + "," + baseReward);
        }
    }
}
