package com.wwwjf.wanandroidkt.algorithm;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CanPartTest {

    @Test
    public void canPartitionTest() {
        int[] nums = {2, 5, 12, 5};
        assertTrue(canPartition(nums));
    }

    public boolean canPartition(int[] nums) {

        int sum = 0;
        for (int value : nums) {
            sum += value;
        }
        if (sum % 2 != 0){
            return false;
        }
        sum = sum/2;

        boolean[] result = new boolean[sum+1];
        result[0] = true;
        for (int num : nums) {
            for (int i = sum; i > num - 1; i--) {
                if (result[i - num]) {
                    result[i] = true;
                }
            }
        }
        return result[sum];
    }
}
