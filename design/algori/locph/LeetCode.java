package design.algori.locph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LeetCode {

    /**
     * 17/03/2025
     * https://leetcode.com/problems/divide-array-into-equal-pairs/submissions/1576667483/?envType=daily-question&envId=2025-03-17
     *
     * @param nums
     * @return
     */
    public boolean divideArray(int[] nums) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        Set<Integer> set = new HashSet<Integer>();

        for (int i = 0; i < nums.length; i++) {
            if (set.contains(nums[i])) {
                set.remove(nums[i]);
            } else {
                set.add(nums[i]);
            }
        }

        if (set.size() > 1) {
            return false;
        }

        return true;
    }

    /**
     * https://leetcode.com/problems/longest-nice-subarray/?envType=daily-question&envId=2025-03-18
     * In binary operations:
     * <p>
     * - The ^ (bitwise XOR) operator compares each bit of the first operand with the corresponding bit of the second operand
     * - If the bits are different (one is 0 and one is 1), the result is 1
     * - If the bits are the same (both 0 or both 1), the result is 0
     * <p>
     * - The | (bitwise OR) operator compares each bit of the first operand with the corresponding bit of the second operand
     * - If either bit is 1, the resulting bit is 1. Otherwise, it's 0
     * <p>
     * <p>
     * The & only 1 & 1 = 1.
     * 1 & 0 = 0
     * 0 & 1 = 0
     * 0 & 0 = 0
     *
     * @param nums
     * @return
     */
    public int longestNiceSubarray(int[] nums) {
        int maxLen = 1; // chuỗi có 1 pần tử
        int bitNow = 0; // bắt đầu với bit 0
        int j = 0; // bắt đầu từ đây

        // & : Bitwise AND
        // ^ : Bitwise XOR
        // | : Bitwise OR

        for (int i = 0; i < nums.length; i++) {
            while ((nums[i] & bitNow) != 0) {
                bitNow = bitNow ^ nums[j];
                j++;
            }

            bitNow = bitNow | nums[i];

            maxLen = Math.max(maxLen, i - j + 1);
        }

        return maxLen;

    }

    //    https://leetcode.com/problems/minimum-operations-to-make-binary-array-elements-equal-to-one-i/?envType=daily-question&envId=2025-03-19
    public int minOperations(int[] nums) {
        int count = 0;
        int n = nums.length;
        for (int i = 0; i < n - 2; i++) {
            if (nums[i] == 0) {
                nums[i] ^= 1;
                nums[i + 1] ^= 1;
                nums[i + 2] ^= 1;
                count++;
            }
        }

        return (nums[n - 2] == 1 && nums[n - 1] == 1) ? count : -1;
    }

    public static void main(String[] args) {
        LeetCode lc = new LeetCode();
//        int[] nums = {3, 2, 3, 2, 2, 2};
//        System.out.println(lc.divideArray(nums));

        int[] nums = {1, 3, 8, 48, 10};
        System.out.println(lc.longestNiceSubarray(nums));
    }
}
