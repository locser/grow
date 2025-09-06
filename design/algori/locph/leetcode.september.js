/**
 * Example 1:

Input: jewels = "aA", stones = "aAAbbbb"
Output: 3
Example 2:

Input: jewels = "z", stones = "ZZ"
Output: 0
 */
function numJewelsInStones(jewels, stones) {
    var count = 0;
    var mapJewel = {};
    for (var i = 0; i < jewels.length; i++) {
        mapJewel[jewels[i]] = 1;
    }
    for (var i = 0; i < stones.length; i++) {
        if (mapJewel[stones[i]]) {
            count++;
        }
    }
    return count;
}
console.log(numJewelsInStones("aA", "aAAbbbb"));
console.log(numJewelsInStones("z", "ZZ"));
//https://leetcode.com/problems/make-a-square-with-the-same-color/?envType=problem-list-v2&envId=2cf6vun6
function canMakeSquare(grid) {
    var mapColor = {};
    for (var i = 0; i < grid.length; i++) {
        for (var j = 0; j < grid[i].length; j++) {
            mapColor["".concat(i, ",").concat(j)] = grid[i][j] === "B" ? 1 : 0;
        }
    }
    var a = [0, 1, 3, 4];
    for (var i = 0; i < grid.length - 1; i++) {
        for (var j = 0; j < grid[i].length - 1; j++) {
            var sum = mapColor["".concat(i, ",").concat(j)] +
                mapColor["".concat(i, ",").concat(j + 1)] +
                mapColor["".concat(i + 1, ",").concat(j)] +
                mapColor["".concat(i + 1, ",").concat(j + 1)];
            if (sum !== 2) {
                return true;
            }
        }
    }
    return false;
}
console.log(canMakeSquare([
    ["B", "W", "B"],
    ["B", "W", "W"],
    ["B", "W", "B"],
]));
console.log(canMakeSquare([
    ["B", "W", "B"],
    ["W", "B", "W"],
    ["B", "W", "B"],
]));
console.log(canMakeSquare([
    ["B", "W", "B"],
    ["B", "W", "W"],
    ["B", "W", "W"],
]));
//https://leetcode.com/problems/redistribute-characters-to-make-all-strings-equal/?envType=problem-list-v2&envId=2cf6vun6
function makeEqual(words) {
    /**
          - dịch chuyển các kí tự ở các string sao cho tất cả các string đều bằng nhau. nếu có thể trả về true
          - tiếp cận: các string bằng nhau thì số các kí tự của 1 loại trong words cần phải được chia đều và không dư
          - nếu dư sẽ là không bằng nhau return false
          - ví dụ words = ["abc","aabc","bc"] thì có 3a, 3b,3c chia đều được cho số phần tử là 3
       */
    var characterCount = new Map();
    for (var _i = 0, words_1 = words; _i < words_1.length; _i++) {
        var word = words_1[_i];
        // Count each character in the current word
        for (var _a = 0, word_1 = word; _a < word_1.length; _a++) {
            var character = word_1[_a];
            characterCount.set(character, (characterCount.get(character) || 0) + 1);
        }
    }
    //  tất cả các phần tử trong map đều phải có value chia hết cho words.length
    var leng = words.length;
    return Array.from(characterCount.values()).every(function (count) { return count % leng === 0; });
}
console.log("makeEqual");
console.log(makeEqual(["abc", "aabc", "bc"]));
console.log(makeEqual(["abc", "aabc", "bc", "bcc"]));
console.log(makeEqual(["abc", "aabc", "bc", "bcc", "ccc"]));
console.log(makeEqual(["abc", "aabc", "bc", "bcc", "ccc", "cccc"]));
console.log(makeEqual(["abc", "aabc", "bc", "bcc", "ccc", "cccc", "ccccc"]));
console.log(makeEqual(["abc", "aabc", "bc", "bcc", "ccc", "cccc", "ccccc", "cccccc"]));
