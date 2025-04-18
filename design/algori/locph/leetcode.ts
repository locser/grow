class _Node {
  val: number;
  next: _Node | null;
  random: _Node | null;

  constructor(val?: number, next?: _Node, random?: _Node) {
    this.val = val === undefined ? 0 : val;
    this.next = next === undefined ? null : next;
    this.random = random === undefined ? null : random;
  }
}

class ListNode {
  val: number;
  next: ListNode | null;
  constructor(val?: number, next?: ListNode | null) {
    this.val = val === undefined ? 0 : val;
    this.next = next === undefined ? null : next;
  }
}

// https://leetcode.com/explore/learn/card/linked-list/213/conclusion/1229/
function copyRandomList(head: _Node | null): _Node | null {
  if (!head) return null;

  // Use Map instead of plain object
  const map = new Map<_Node, _Node>();

  // First pass: create copies of all nodes
  let curr: any = head;
  while (curr != null) {
    map.set(curr, new _Node(curr.val));
    curr = curr.next;
  }

  // Second pass: connect next and random pointers
  curr = head;
  while (curr != null) {
    const copy = map.get(curr)!;
    copy.next = curr.next ? map.get(curr.next)! : null;
    copy.random = curr.random ? map.get(curr.random)! : null;
    curr = curr.next;
  }

  return map.get(head)!;
}

//https://leetcode.com/explore/learn/card/linked-list/213/conclusion/1295/

function rotateRight(head: ListNode | null, k: number): ListNode | null {
  if (!head || !head.next || k == 0) return head;

  let n = 0;

  let temp: any = new ListNode(0, head);

  while (temp.next != null) {
    n++;
    temp = temp.next;
  }

  temp.next = head;

  k = k % n;
  temp = head;
  for (let i = 1; i < n - k; i++) {
    temp = temp.next;
  }

  head = temp.next;
  temp.next = null;

  return head;
}

function maximumWealth(accounts: number[][]): number {
  const accountsLength = accounts.length;

  let maxWealth = 0;
  for (let i = 0; i < accountsLength; i++) {
    const customerWealth = accounts[i].reduce((acc, cur) => acc + cur, 0);
    maxWealth = Math.max(maxWealth, customerWealth);
  }

  return maxWealth;
}

//https://leetcode.com/problems/append-k-integers-with-minimal-sum/
function minimalKSum(nums: number[], k: number): number {
  nums.sort((a, b) => a - b);
  const list: number[] = [];
  for (let i = 0; i < k; i++) {
    if (binarySearch(nums, nums[i])) {
      continue;
    } else {
      list.push(i);
    }
  }

  return list.reduce((acc, cur) => acc + cur, 0);
}

function binarySearch(nums: number[], arg1: number) {
  let left = 0;
  let right = nums.length - 1;
  while (left <= right) {
    const mid = Math.floor((left + right) / 2);
    if (nums[mid] === arg1) {
      return true;
    } else if (nums[mid] < arg1) {
      left = mid + 1;
    } else {
      right = mid - 1;
    }
  }

  return false;
}

function isPalindrome(x: number): boolean {
  if (x < 0) return false;
  if (x == 0) return true;

  const stringX = x.toString();
  const lengthX = stringX.length;

  for (let i = 0; i < stringX.length / 2; i++) {
    if (stringX[i] !== stringX[lengthX - 1 - i]) {
      return false;
    }
  }

  return true;
}

function romanToInt(s: string): number {
  const stringS = s.toString();
  const lengthS = stringS.length;
  let sum = 0;
  const map = {
    I: 1,
    V: 5,
    X: 10,
    L: 50,
    C: 100,
    D: 500,
    M: 1000,
  };

  for (let i = 0; i < lengthS; i++) {
    let current = map[stringS[i]];

    if (i + 1 < lengthS && map[stringS[i + 1]] > current) {
      sum -= current;
    } else {
      sum += current;
    }
  }

  return sum;
}

//https://leetcode.com/problems/longest-common-prefix/
function longestCommonPrefix(strs: string[]): string {
  if (strs.length === 1) return strs[0];
  const shortestLength = strs.reduce(
    (minLength, currentString) => Math.min(minLength, currentString.length),
    Infinity
  );

  for (let i = shortestLength; i > 0; i--) {
    const str = strs[0].substring(0, i);
    const result = strs.every((currentString) => currentString.startsWith(str));

    if (result) return str;
  }

  return "";
}

// https://leetcode.com/problems/valid-parentheses/
function isValid(s: string): boolean {
  if (s.length == 1) return false;
  const stack: string[] = [];
  const map = {
    "{": "}",
    "(": ")",
    "[": "]",
  };

  for (let char of s) {
    // nếu là kí tự mở ngoặc
    if (char in map) {
      stack.push(char);
    } else {
      const lastOpening = stack.pop() || "";
      if (char !== map[lastOpening]) {
        return false;
      }
    }
  }

  return stack.length ? false : true;
}
