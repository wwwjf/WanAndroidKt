package com.wwwjf.wanandroidkt.algorithm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    /**
     * 相同字符
     */
    @Test
    public void longestCommonPrefix() {
        String[] str1 = {"flower", "flower", "flower", "flower"};
        String[] str2 = {"dog", "racecar", "car"};
        String[] str3 = {"flower", "flow", "flight"};
        String[] str4 = {"ah", "a"};
        String[] str5 = {"flower", "flowe", "flow", "flo"};
        assertEquals("flower", longestCommonPrefix(str1));
        assertEquals("", longestCommonPrefix(str2));
        assertEquals("fl", longestCommonPrefix(str3));
        assertEquals("a", longestCommonPrefix(str4));
        assertEquals("flo", longestCommonPrefix(str5));

    }

    public String longestCommonPrefix(String[] strs) {
        String result;
        if (strs == null) {
            return "";
        }
        result = strs[0];
        for (int i = 1; i < strs.length; i++) {

            int len = Math.min(result.length(), strs[i].length());
            int index = 0;
//            while(index < len && result.charAt(index)==strs[i].charAt(index)){
            while (result.charAt(index) == strs[i].charAt(index)) {
                System.out.println("index=" + index);
                index++;
            }
            result = result.substring(0, index);
            /*if(result.length() == 0){
                return "";
            }*/
        }
        return result;
    }

    /**
     * 三数之和为0
     *
     * @return
     */
    @Test
    public void threeSum() {

//        int[] nums = {-4, -1, -1, 0, 1, 2};
        int[] nums = {-1, 0, 1, 2, -1, -4};
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> subResult0 = new ArrayList<>();
        subResult0.add(-1);
        subResult0.add(-1);
        subResult0.add(2);
        result.add(subResult0);
        List<Integer> subResult1 = new ArrayList<>();
        subResult1.add(-1);
        subResult1.add(0);
        subResult1.add(1);
        result.add(subResult1);
        assertEquals(result, threeSum(nums));
    }

    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null) {
            return result;
        }
        int len = nums.length;
        if (nums.length < 3) {
            return result;
        }
        Arrays.sort(nums);
        for (int i = 0; i < len; i++) {
            if (nums[i] > 0) {
                break;
            }
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            int start = i + 1;
            int end = len - 1;
            while (start < end) {
                int sum = nums[i] + nums[start] + nums[end];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[start], nums[end]));
                    start++;
                    end--;
                    while (start < end && nums[start] == nums[start - 1]) {
                        start++;
                    }
                    while (start < end && nums[end] == nums[end + 1]) {
                        end--;
                    }
                } else if (sum > 0) {
                    end--;
                } else {
                    start++;
                }
            }
        }
        return result;
    }

    @Test
    public void threeSumClosest() {

        int[] nums = {-1, 2, 1, -4};
        int target = 1;
        assertEquals(2, threeSumClosest(nums, target));
    }

    public int threeSumClosest(int[] nums, int target) {
        int n = nums.length;
        Arrays.sort(nums);
        int result = nums[0] + nums[1] + nums[2];
        for (int i = 0; i < n; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            int start = i + 1;
            int end = n - 1;
            while (start < end) {
                int sum = nums[i] + nums[start] + nums[end];
                if (sum == target) {
                    return sum;
                } else if (sum > target) {
                    end--;
                } else {
                    start++;
                }
                if (Math.abs(sum - target) < Math.abs(result - target)) {
                    result = sum;
                }
            }
        }
        return result;
    }

    /**
     * 电话号码的字母组合
     */
    @Test
    public void letterCombinations() {
        String digits = "23";
        List<String> result = new ArrayList<>();
        result.add("ad");
        result.add("ae");
        result.add("af");
        result.add("bd");
        result.add("be");
        result.add("bf");
        result.add("cd");
        result.add("ce");
        result.add("cf");
        assertEquals(result, letterCombinations(digits));

    }

    public List<String> letterCombinations(String digits) {
        HashMap<Character, String> digitsMap = new HashMap<>();
        List<String> result = new ArrayList<>();
        digitsMap.put('2',"abc");
        digitsMap.put('3',"def");
        digitsMap.put('4',"ghi");
        digitsMap.put('5',"jkl");
        digitsMap.put('6',"mno");
        digitsMap.put('7',"pqrs");
        digitsMap.put('8',"tuv");
        digitsMap.put('9',"wxyz");
        char[] digitArray = digits.toCharArray();
        int length = digitArray.length;
        for (int i = 0; i < length; i++) {//几个数字
            StringBuilder subStr = new StringBuilder();
//            combinateLetter(digitsMap.get(digitArray[i]));
            String letters = digitsMap.get(digitArray[i]);
            for (int j = 0; j < letters.length(); j++) {
                subStr.append(letters.charAt(j));
            }
            result.add(subStr.toString());
        }
        return result;
    }

    private String combinateLetter(int digitsLen,StringBuilder stringBuilder, String letters){
        char[] chars = letters.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            stringBuilder.append(chars[i]);
        }
        return "";
    }


}