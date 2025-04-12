package design.algori.locph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Node;

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
     * - The ^ (bitwise XOR) operator compares each bit of the first operand with
     * the corresponding bit of the second operand
     * - If the bits are different (one is 0 and one is 1), the result is 1
     * - If the bits are the same (both 0 or both 1), the result is 0
     * <p>
     * - The | (bitwise OR) operator compares each bit of the first operand with the
     * corresponding bit of the second operand
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

    // https://leetcode.com/problems/minimum-operations-to-make-binary-array-elements-equal-to-one-i/?envType=daily-question&envId=2025-03-19
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

    // https://leetcode.com/explore/learn/card/linked-list/214/two-pointer-technique/1296/
    public ListNode removeNthFromEnd(ListNode head, int n) {
        // Handle empty list
        if (head == null) {
            return null;
        }

        // Create dummy node to handle edge cases
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode fast = dummy;
        ListNode slow = dummy;

        // Move fast pointer n steps ahead
        for (int i = 0; i <= n; i++) {
            fast = fast.next;
        }

        // Move both pointers until fast reaches end
        while (fast != null) {
            fast = fast.next;
            slow = slow.next;
        }

        // Remove the nth node
        slow.next = slow.next.next;

        return dummy.next;
    }

    // https://leetcode.com/explore/learn/card/linked-list/219/classic-problems/1205/
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode current = head;

        while (current != null) {
            ListNode nextTemp = current.next;
            current.next = prev;
            prev = current;
            current = nextTemp;
        }

        return prev;
    }

    // https://leetcode.com/explore/learn/card/linked-list/219/classic-problems/1207/
    public ListNode removeElements(ListNode head, int val) {
        // Handle empty list
        if (head == null)
            return null;

        // Create dummy node to handle cases where head needs to be removed
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode current = dummy;

        while (current.next != null) {
            if (current.next.val == val) {
                current.next = current.next.next;
            } else {
                current = current.next;
            }
        }

        return dummy.next;
    }

    // https://leetcode.com/explore/learn/card/linked-list/219/classic-problems/1208/
    public ListNode oddEvenList(ListNode head) {

        if (head == null || head.next == null) {
            return head;
        }

        // First node is considered odd (index 1)
        ListNode odd = head;
        // Second node is even (index 2)
        ListNode even = head.next;
        // Keep track of even head to connect later
        ListNode evenHead = even;

        while (even != null && even.next != null) {
            // Connect odd nodes
            odd.next = even.next;
            odd = odd.next;

            // Connect even nodes
            even.next = odd.next;
            even = even.next;
        }

        // Connect odd list with even list
        odd.next = evenHead;

        return head;
    }

    // https://leetcode.com/explore/learn/card/linked-list/219/classic-problems/1209/

    /**
     * use stack, push all node, then pop and compare with head
     */
    public boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null) {
            return true;
        }

        ListNode slow = head;
        ListNode fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        ListNode secondHalf = reverseList(slow.next);

        ListNode firstHalf = head;
        while (secondHalf != null) {
            if (firstHalf.val != secondHalf.val) {
                return false;
            }
            firstHalf = firstHalf.next;
            secondHalf = secondHalf.next;
        }

        return true;
    }

    // https://leetcode.com/explore/learn/card/linked-list/213/conclusion/1227/
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        // Handle edge cases where either list is null
        if (list1 == null)
            return list2;
        if (list2 == null)
            return list1;

        // Create dummy node to handle edge cases
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        // Compare and merge nodes
        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
        }

        // Attach remaining nodes if any
        if (list1 != null) {
            current.next = list1;
        }
        if (list2 != null) {
            current.next = list2;
        }

        return dummy.next;
    }

    // public ListNode mergeTwoLists2(ListNode list1, ListNode list2) {
    // if (list1 == null || list2 == null) {
    // return list1 != null ? list1 : list2;
    // }
    // ListNode result = new ListNode(0);
    // ListNode current = result;
    // while (list1.next != null && list2.next != null) {
    // if (list1.next.val == list2.next.val) {
    // current.next = list1.next;
    // current.next.next = list2.next;
    // list1 = list1.next;
    // list2 = list2.next;
    // current = current.next.next;
    // } else {
    // if (list1.next.val < list2.next.val) {
    // current.next = list1.next;
    // list1 = list1.next;
    // current = current.next;
    // } else {
    // current.next = list2.next;
    // list2 = list2.next;
    // current = current.next;
    // }
    // }
    // }
    // if (list1.next != null) {
    // current.next = list1.next;
    // }
    // if (list2.next != null) {
    // current.next = list2.next;
    // }
    // return result.next;
    // }

    // https://leetcode.com/explore/learn/card/linked-list/213/conclusion/1228/
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        int carry = 0;

        // Continue while there are digits to process
        while (l1 != null || l2 != null || carry > 0) {
            int sum = carry;

            // Add l1 digit if exists
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }

            // Add l2 digit if exists
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }

            // Calculate new digit and carry
            carry = sum / 10;
            current.next = new ListNode(sum % 10);
            current = current.next;
        }

        return dummy.next;
    }

    /**
     * https://leetcode.com/explore/learn/card/linked-list/213/conclusion/1225/
     * class Node {
     * public int val;
     * public Node prev;
     * public Node next;
     * public Node child;
     * };
     *
     *
     */
    public Node flatten(Node head) {
        if (head == null)
            return null;

        Node current = head;
        while (current != null) {
            if (current.child != null) {
                Node next = current.next;
                Node child = flatten(current.child);

                // Connect current with child
                current.next = child;
                child.prev = current;
                current.child = null;

                // Find the end of child list
                while (child.next != null) {
                    child = child.next;
                }

                // Connect child end with next
                if (next != null) {
                    child.next = next;
                    next.prev = child;
                }
            }
            current = current.next;
        }

        return head;
    }

    // https://leetcode.com/explore/learn/card/linked-list/213/conclusion/1295/
    /**
     *
     */
    public ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null || k == 0)
            return head;

        ListNode curr = head;
        int length = 1;
        while (curr.next != null) {
            curr = curr.next;
            length++;
        }

        curr.next = head;
        // - Vì xoay length lần sẽ trở về vị trí ban đầu
        // - Nên chỉ cần xoay k % length lần
        k = k % length;

        curr = head;

        for (int i = 1; i < length - k; i++) {
            curr = curr.next;
        }

        head = curr.next;
        curr.next = null;

        return head;
    }

    // https://leetcode.com/problems/reverse-nodes-in-k-group/
    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || head.next == null || k == 1) {
            return head;
        }

        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode prev = dummy;
        ListNode curr = head;

        while (curr != null) {
            int count = 0;
            while (curr != null && count < k) {
                curr = curr.next;
                count++;
            }

            if (count == k) {
                prev = reverse(prev, curr);
                curr = prev.next;
            }
        }

        return dummy.next;
    }

    //https://leetcode.com/problems/minimum-operations-to-make-array-values-equal-to-k/?envType=daily-question&envId=2025-04-09
    public int minOperations(int[] nums, int k) {
        Set<Integer> st = new HashSet<>();
        for (int x : nums) {
            if (x < k) {
                return -1;
            } else if (x > k) {
                st.add(x);
            }
        }
        return st.size();
    }



    public static void main(String[] args) {
        LeetCode lc = new LeetCode();
        // int[] nums = {3, 2, 3, 2, 2, 2};
        // System.out.println(lc.divideArray(nums));

        int[] nums = { 1, 3, 8, 48, 10 };
        System.out.println(lc.longestNiceSubarray(nums));
    }
}
