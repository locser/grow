/**
 * Example 1:

Input: jewels = "aA", stones = "aAAbbbb"
Output: 3
Example 2:

Input: jewels = "z", stones = "ZZ"
Output: 0
 */
function numJewelsInStones(jewels: string, stones: string): number {
  let count = 0;

  const mapJewel = {};

  for (let i = 0; i < jewels.length; i++) {
    mapJewel[jewels[i]] = 1;
  }

  for (let i = 0; i < stones.length; i++) {
    if (mapJewel[stones[i]]) {
      count++;
    }
  }
  return count;
}
console.log(numJewelsInStones("aA", "aAAbbbb"));
console.log(numJewelsInStones("z", "ZZ"));

//https://leetcode.com/problems/make-a-square-with-the-same-color/?envType=problem-list-v2&envId=2cf6vun6
function canMakeSquare(grid: string[][]): boolean {
  const mapColor = {};

  for (let i = 0; i < grid.length; i++) {
    for (let j = 0; j < grid[i].length; j++) {
      mapColor[`${i},${j}`] = grid[i][j] === "B" ? 1 : 0;
    }
  }

  const a = [0, 1, 3, 4];

  for (let i = 0; i < grid.length - 1; i++) {
    for (let j = 0; j < grid[i].length - 1; j++) {
      const sum =
        mapColor[`${i},${j}`] +
        mapColor[`${i},${j + 1}`] +
        mapColor[`${i + 1},${j}`] +
        mapColor[`${i + 1},${j + 1}`];

      if (sum !== 2) {
        return true;
      }
    }
  }

  return false;
}

console.log(
  canMakeSquare([
    ["B", "W", "B"],
    ["B", "W", "W"],
    ["B", "W", "B"],
  ])
);
console.log(
  canMakeSquare([
    ["B", "W", "B"],
    ["W", "B", "W"],
    ["B", "W", "B"],
  ])
);
console.log(
  canMakeSquare([
    ["B", "W", "B"],
    ["B", "W", "W"],
    ["B", "W", "W"],
  ])
);

//https://leetcode.com/problems/redistribute-characters-to-make-all-strings-equal/?envType=problem-list-v2&envId=2cf6vun6
function makeEqual(words: string[]): boolean {
  /**
        - dịch chuyển các kí tự ở các string sao cho tất cả các string đều bằng nhau. nếu có thể trả về true
        - tiếp cận: các string bằng nhau thì số các kí tự của 1 loại trong words cần phải được chia đều và không dư
        - nếu dư sẽ là không bằng nhau return false
        - ví dụ words = ["abc","aabc","bc"] thì có 3a, 3b,3c chia đều được cho số phần tử là 3
     */

  const characterCount: Record<string, number> = {};

  for (const word of words) {
    // Count each character in the current word
    for (const character of word) {
      characterCount[character] = (characterCount[character] || 0) + 1;
    }
  }

  //  tất cả các phần tử trong map đều phải có value chia hết cho words.length
  const leng = words.length;

  return Object.values(characterCount).every(
    (count: any) => count % leng === 0
  );
}
console.log("makeEqual");
console.log(makeEqual(["abc", "aabc", "bc"]));
console.log(makeEqual(["abc", "aabc", "bc", "bcc"]));
console.log(makeEqual(["abc", "aabc", "bc", "bcc", "ccc"]));
console.log(makeEqual(["abc", "aabc", "bc", "bcc", "ccc", "cccc"]));
console.log(makeEqual(["abc", "aabc", "bc", "bcc", "ccc", "cccc", "ccccc"]));
console.log(
  makeEqual(["abc", "aabc", "bc", "bcc", "ccc", "cccc", "ccccc", "cccccc"])
);
