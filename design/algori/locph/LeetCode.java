package design.algori.locph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LeetCode {

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

    public static void main(String[] args) {
        LeetCode lc = new LeetCode();
        int[] nums = {3, 2, 3, 2, 2, 2};
        System.out.println(lc.divideArray(nums));
    }
}
