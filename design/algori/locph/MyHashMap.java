package design.algori.locph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

public class MyHashMap {

    public static void main(String[] args) {
        int key = 1;
        int value = 1;
        // Your MyHashMap object will be instantiated and called as such:
        MyHashMap obj = new MyHashMap();

        // String[] list1 = {"Shogun", "Tapioca Express", "Burger King", "KFC"};
        // String[] list2 = {"Piatti", "The Grill at Torrey Pines", "Hungry Hunter
        // Steakhouse", "Shogun"};
        // String[] result = obj.findRestaurant(list1, list2)
        // Create a test tree
        // TreeNode root = new TreeNode(1);
        // root.left = new TreeNode(2);
        // root.right = new TreeNode(3);
        // root.left.left = new TreeNode(4);
        // root.right.left = new TreeNode(2);
        // root.right.right = new TreeNode(4);
        // root.right.left.left = new TreeNode(4);
        //
        // List<TreeNode> duplicates = obj.findDuplicateSubtrees(root);
        //
        // System.out.println("Number of duplicate subtrees: " + duplicates.size());
        // int[] nums = {1, 0, 2, 1, 3, 1};
        // boolean result2 = obj.containsNearbyDuplicate(nums, 1);
        // System.out.println(result2);

        // int numJewelsInStones = obj.numJewelsInStones("z", "ZZ");
        //
        // System.out.println("numJewelsInStones: " + numJewelsInStones);

        // System.out.println(obj.lengthOfLongestSubstring("pwwkew"));

        // System.out.println(obj.fourSumCount([1,2],[-2,-1], [-1,2], [0,2]));

        // System.out.println(countingValleys(12, "DDUUDDUDUUUD"));
        // System.out.println(jumpingOnClouds(List.of(0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0,
        // 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0,
        // 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0
        // )));

        // System.out.println(112436 + "---" + repeatedString("jdiacikk", 899491));
        //
        // int[] nums = {1, 0, -1, 0, -2, 2};
        // System.out.println(obj.fourSum(nums, 0));

        // MyLinkedList myLinkedList = new MyLinkedList();
        // MyLinkedList obj1 = new MyLinkedList();
        // obj1.addAtHead(1);
        // obj1.addAtTail(3);
        // obj1.addAtIndex(1, 2);
        // obj1.deleteAtIndex(1);
        // int param_1 = obj1.get(1);

    }

    public List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length < 4)
            return result;

        Arrays.sort(nums);
        int n = nums.length;

        for (int i = 0; i < n - 3; i++) {
            // Skip duplicates for i
            if (i > 0 && nums[i] == nums[i - 1])
                continue;

            for (int j = i + 1; j < n - 2; j++) {
                // Skip duplicates for j
                if (j > i + 1 && nums[j] == nums[j - 1])
                    continue;

                int left = j + 1;
                int right = n - 1;

                while (left < right) {
                    long sum = (long) nums[i] + nums[j] + nums[left] + nums[right];

                    if (sum == target) {
                        result.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));

                        // Skip duplicates for left
                        while (left < right && nums[left] == nums[left + 1])
                            left++;
                        // Skip duplicates for right
                        while (left < right && nums[right] == nums[right - 1])
                            right--;

                        left++;
                        right--;
                    } else if (sum < target) {
                        left++;
                    } else {
                        right--;
                    }
                }
            }
        }

        return result;
    }

    // https://www.hackerrank.com/challenges/repeated-string/problem?isFullScreen=true&h_l=interview&playlist_slugs%5B%5D=interview-preparation-kit&playlist_slugs%5B%5D=warmup

    public static long repeatedString(String s, long n) {
        // Write your code here
        if (s.length() == 1)
            return n;
        int sodu = 0;

        long soLanLap = n / s.length();
        long du = n % s.length();

        int mutil = 0;

        int soLanLapNhieuNhatTrongChuoi = getMaxS(s);

        int soLanConLaiCanLap = getMaxS(s.substring(0, (int) du));

        return soLanLapNhieuNhatTrongChuoi * soLanLap + soLanConLaiCanLap;
    }

    public static int getMaxS(String s) {
        Map<Character, Integer> charCount = new HashMap<>();
        for (char c : s.toCharArray()) {
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);
        }

        // Get the maximum value from the map
        int maxFrequency = Collections.max(charCount.values());
        return maxFrequency;
    }

    // https://www.hackerrank.com/challenges/jumping-on-the-clouds/problem?isFullScreen=true&h_l=interview&playlist_slugs%5B%5D=interview-preparation-kit&playlist_slugs%5B%5D=warmup
    public static int jumpingOnClouds(List<Integer> c) {
        int jumps = 0;
        int i = 0;

        while (i < c.size() - 1) {
            // Check if we can jump 2 steps
            if (i + 2 < c.size() && c.get(i + 2) == 0) {
                i += 2;
            } else {
                i += 1;
            }
            jumps++;
        }

        return jumps;
    }

    // https://www.hackerrank.com/challenges/counting-valleys/problem?isFullScreen=true&h_l=interview&playlist_slugs%5B%5D=interview-preparation-kit&playlist_slugs%5B%5D=warmup
    public static int countingValleys(int steps, String path) {
        // Write your code here
        int level = 0;
        boolean inValley = false;
        int countValley = 0;

        for (char step : path.toCharArray()) {
            if (step == 'U') {
                level++;
            } else {
                level--;
            }

            if (level < 0) {
                inValley = true;
            } else if (inValley == true && level == 0) {
                countValley++;
                inValley = false;

            }
        }

        return countValley;

    }

    // https://leetcode.com/explore/learn/card/hash-table/187/conclusion-hash-table/1133/
    public int[] topKFrequent(int[] nums, int k) {
        // Count frequencies
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        // Create PriorityQueue with custom comparator to sort by frequency
        PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> map.get(b) - map.get(a));
        pq.addAll(map.keySet());

        // Get top k elements
        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = pq.poll();
        }

        return result;
    }

    // https://leetcode.com/explore/learn/card/hash-table/187/conclusion-hash-table/1134/
    public int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        Map<Integer, Integer> map = new HashMap<>();
        int count = 0;
        for (int i = 0; i < nums1.length; i++) {
            for (int j = 0; j < nums2.length; j++) {
                int sum = nums1[i] + nums2[j];
                map.put(sum, map.getOrDefault(sum, 0) + 1);
            }
        }

        for (int i = 0; i < nums3.length; i++) {
            for (int j = 0; j < nums4.length; j++) {
                int sum = nums3[i] + nums4[j];
                count += map.getOrDefault(-sum, 0);
            }
        }

        return count;
    }

    /**
     * https://leetcode.com/explore/learn/card/hash-table/187/conclusion-hash-table/1135/
     */
    public int lengthOfLongestSubstring(String s) {
        Set<Character> set = new HashSet<>();
        int max = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            // While we have a duplicate, remove characters from the left
            while (!set.add(s.charAt(right))) {
                set.remove(s.charAt(left));
                left++;
            }
            // Update max length
            max = Math.max(max, right - left + 1);
        }

        return max;
    }

    /**
     * https://leetcode.com/explore/learn/card/hash-table/187/conclusion-hash-table/1136/
     */
    public int numJewelsInStones(String jewels, String stones) {

        Map<Character, Integer> map = new HashMap<>();

        int result = 0;

        for (int i = 0; i < stones.length(); i++) {
            System.out.println(stones.charAt(i));
            map.put(stones.charAt(i), map.getOrDefault(stones.charAt(i), 0) + 1);
        }

        System.out.println(map.toString());

        for (int i = 0; i < jewels.length(); i++) {
            if (map.containsKey(jewels.charAt(i))) {
                // return map.size();
                result += map.get(jewels.charAt(i));
            }
        }

        return result;
    }

    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        List<TreeNode> result = new ArrayList<>();
        Map<String, Integer> count = new HashMap<>();
        result.stream().forEach(System.out::println);

        serialize(root, count, result);

        return result;
    }

    public int numJewelsInStones2(String jewels, String stones) {
        // Use HashSet for O(1) lookup instead of HashMap since we only need to check
        // existence
        Set<Character> jewelSet = new HashSet<>();
        int count = 0;

        // Store all jewels in the set
        for (char jewel : jewels.toCharArray()) {
            jewelSet.add(jewel);
        }

        // Count stones that are jewels
        for (char stone : stones.toCharArray()) {
            if (jewelSet.contains(stone)) {
                count++;
            }
        }

        return count;
    }

    private String serialize(TreeNode node, Map<String, Integer> count, List<TreeNode> result) {
        if (node == null)
            return "#";

        String serial = node.val + "," +
                serialize(node.left, count, result) + "," +
                serialize(node.right, count, result);

        count.put(serial, count.getOrDefault(serial, 0) + 1);

        if (count.get(serial) == 2) {
            result.add(node);
        }

        return serial;
    }

    /**
     * Input: board =
     * [["5","3",".",".","7",".",".",".","."]
     * ,["6",".",".","1","9","5",".",".","."]
     * ,[".","9","8",".",".",".",".","6","."]
     * ,["8",".",".",".","6",".",".",".","3"]
     * ,["4",".",".","8",".","3",".",".","1"]
     * ,["7",".",".",".","2",".",".",".","6"]
     * ,[".","6",".",".",".",".","2","8","."]
     * ,[".",".",".","4","1","9",".",".","5"]
     * ,[".",".",".",".","8",".",".","7","9"]]
     * Output: true
     *
     * @param
     * @return
     */

    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            List<String> list = map.getOrDefault(key, new ArrayList<>());
            list.add(str);
            map.put(key, list);
        }
        return new ArrayList<>(map.values());
    }

    public boolean containsNearbyDuplicate(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(nums[i])) {
                if (i - map.get(nums[i]) <= k) {
                    return true;
                }
            }
            map.put(nums[i], i);
        }
        return false;
    }

    public int[] intersect(int[] nums1, int[] nums2) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int num : nums1) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        List<Integer> list = new ArrayList<>();
        for (int num : nums2) {
            if (map.containsKey(num) && map.get(num) > 0) {
                list.add(num);
                map.put(num, map.get(num) - 1);
            }
        }
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    public int firstUniqChar2(String s) {
        // Use array instead of HashMap since we only have lowercase letters
        int[] frequency = new int[26];

        // Count frequency of each character
        for (char c : s.toCharArray()) {
            frequency[c - 'a']++;
        }

        // Find the first character with frequency 1
        for (int i = 0; i < s.length(); i++) {
            if (frequency[s.charAt(i) - 'a'] == 1) {
                return i;
            }
        }

        return -1;
    }

    public int firstUniqChar1(String s) {
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            map.put(s.charAt(i), map.getOrDefault(s.charAt(i), 0) + 1);
        }
        for (int i = 0; i < s.length(); i++) {
            if (map.get(s.charAt(i)) == 1) {
                return i;
            }
        }
        return -1;
    }

    public String[] findRestaurant(String[] list1, String[] list2) {
        Map<String, Integer> map = new HashMap<>();
        List<String> result = new ArrayList<>();
        int minSum = Integer.MAX_VALUE;

        // Store list1 restaurants and their indices
        for (int i = 0; i < list1.length; i++) {
            map.put(list1[i], i);
        }

        // Check list2 restaurants and find minimum index sum
        for (int i = 0; i < list2.length; i++) {
            if (map.containsKey(list2[i])) {
                int sum = i + map.get(list2[i]);
                if (sum < minSum) {
                    result.clear();
                    result.add(list2[i]);
                    minSum = sum;
                } else if (sum == minSum) {
                    result.add(list2[i]);
                }
            }
        }

        return result.toArray(new String[result.size()]);
    }

    public boolean isIsomorphic(String s, String t) {
        Map<Character, Character> map = new HashMap<>();
        Map<Character, Character> map2 = new HashMap<>();

        for (int i = 0; i < s.length(); i++) {
            if (map.containsKey(s.charAt(i))) {
                if (map.get(s.charAt(i)) != t.charAt(i)) {
                    return false;
                }
            } else if (map2.containsKey(t.charAt(i))) {
                if (map2.get(t.charAt(i)) != s.charAt(i)) {
                    return false;
                }
            } else {
                map.put(s.charAt(i), t.charAt(i));
                map2.put(t.charAt(i), s.charAt(i));
            }
        }

        return true;
    }

    // @Ok
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }
            map.put(nums[i], i);
        }

        return new int[] {};
    }

    // public int[] twoSum(int[] nums, int target) {
    // int[] result = new int[2];
    // for (int i = 0; i < nums.length; i++) {
    // for (int j = i + 1; j < nums.length; j++) {
    // if (nums[i] + nums[j] == target) {
    // result[0] = i;
    // result[1] = j;
    // }
    // }
    // }
    // return result;
    // }

    public boolean isHappy(int n) {
        Set<Integer> seen = new HashSet<>();

        while (n != 1 && !seen.contains(n)) {
            seen.add(n);
            n = getSquareSum(n);
        }

        return n == 1;
    }

    private int getSquareSum(int n) {
        int sum = 0;
        while (n > 0) {
            int digit = n % 10;
            sum += digit * digit;
            n /= 10;
        }
        return sum;
    }

    HashMap<Integer, Integer> hashMap;

    public MyHashMap() {
        hashMap = new HashMap<Integer, Integer>();
    }

    public void put(int key, int value) {
        if (!hashMap.containsKey(key)) {
            // set new value for key
            hashMap.put(key, value);
        } else {
            hashMap.put(key, value);

        }

    }

    public int get(int key) {
        if (!hashMap.containsKey(key))
            return -1;
        return hashMap.get(key);
    }

    public void remove(int key) {
        if (!hashMap.containsKey(key))
            return;
        hashMap.remove(key);
    }

    public boolean containsDuplicate(int[] nums) {
        int size = new HashSet(Arrays.asList(nums)).size();
        System.out.println(size + " " + Arrays.asList(nums));
        return size != nums.length;

    }

    public int singleNumber(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
            // System.out.println(result);
        }
        return result;
    }

    /**
     * Given two integer arrays nums1 and nums2, return an array of their
     * intersection. Each element in the result must be unique and you may return
     * the result in any order.
     * <p>
     * <p>
     * <p>
     * Example 1:
     * <p>
     * Input: nums1 = [1,2,2,1], nums2 = [2,2]
     * Output: [2]
     * Example 2:
     * <p>
     * Input: nums1 = [4,9,5], nums2 = [9,4,9,8,4]
     * Output: [9,4]
     * Explanation: [4,9] is also accepted.
     */
    public int[] intersection(int[] nums1, int[] nums2) {
        // HashSet<Integer> set = new HashSet<>();
        // for (int i = 0; i < nums1.length; i++) {
        // set.add(nums1[i]);
        // }
        // HashSet<Integer> set2 = new HashSet<>();
        // for (int i = 0; i < nums2.length; i++) {
        // if (set.contains(nums2[i])) {
        // set2.add(nums2[i]);
        // }
        // }
        //// int[] result = new int[set2.size()];
        //// int index = 0;
        //// for (Integer i : set2) {
        //// result[index++] = i;
        //// }
        // return Arrays.stream();

        Set<Integer> set1 = Arrays.stream(nums1).boxed().collect(Collectors.toSet());
        return Arrays.stream(nums2)
                .filter(set1::contains)
                .distinct()
                .toArray();
    }

}
