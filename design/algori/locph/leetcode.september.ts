/**
https://leetcode.com/problems/maximum-height-of-a-triangle/?envType=problem-list-v2&envId=2cf6vun6
    độ dài lớn nhất của 1 cạnh tam giác xếp bởi red và blue
Constraints:

1 <= red, blue <= 100
 */

function maxHeightOfTriangle(red: number, blue: number): number {
  let i = 1;

  let red2 = red,
    blue2 = blue;

  for (i = 1; i <= 100; i++) {
    console.log("i+1", i + 1, " red", red, " blue", blue);
    if (i % 2 !== 0) {
      blue = blue - i;
      if (red < i + 1) {
        console.log("red", red);
        break;
      }
    } else {
      red -= i;
      if (blue < i + 1) {
        console.log("blue", blue);
        break;
      }
    }
  }

  let j = 1;

  for (j = 1; j <= 100; j++) {
    console.log("i+1", i + 1, " red2", red2, " blue", blue2);
    if (j % 2 !== 0) {
      red2 -= j;
      if (blue2 < j + 1) {
        break;
      }
    } else {
      blue2 -= j;
      if (red2 < j + 1) {
        break;
      }
    }
  }

  console.log(i, j);

  return Math.max(i, j);
}

console.log("kq", maxHeightOfTriangle(4, 9));

console.log(maxHeightOfTriangle(2, 4));

/**
https://leetcode.com/problems/maximum-difference-between-adjacent-elements-in-a-circular-array/?envType=problem-list-v2&envId=2cf6vun6
 */
function maxAdjacentDistance(nums: number[]): number {
  let max = 0;

  nums.push(nums[0]);

  for (let i = 0; i < nums.length - 1; i++) {
    max = Math.max(Math.abs(nums[i] - nums[i + 1]), max);
  }

  return max;
}

/**
https://leetcode.com/problems/surface-area-of-3d-shapes/submissions/1762541004/?envType=problem-list-v2&envId=2cf6vun6
    mỗi một cột có thể tính bằng phương thức 2+ v *4 (với 2 là 2 mặt đáy và v là số lượng stack của cột)

    đi theo bên trái (ví dụ 2 có 1 ở bên trái), bên trên (ví dụ 3 có 1 ở phía trên, theo exmaple 1) thì với mỗi stack giáp nhau trừ đi 2 mặt, do 2 mặt này tiếp xúc nên bị trừ

 */

function surfaceArea(grid: number[][]): number {
  const n = grid.length;

  if (n == 0) return 0;
  let count = 0;

  for (let row = 0; row < n; row++) {
    for (let col = 0; col < n; col++) {
      const v = grid[row][col];

      // trường hợp nếu là 0 thì bỏ qua luôn
      if (v > 0) {
        count = count + (2 + 4 * v); // trường hợp đẹp nhất

        // nếu bên trái có tiếp giáp col > 0, có nghĩa là không phải cột đầu tiên

        if (col > 0) {
          // lấy ra số lượng cột trùng nhau
          count = count - Math.min(v, grid[row][col - 1]) * 2;
        }
        // nếu bên phải có tiếp giáp
        if (row > 0) {
          // lấy ra số lượng cột trùng nhau
          count = count - Math.min(v, grid[row - 1][col]) * 2;
        }
      }
    }
  }

  return count;
}

/**
    https://leetcode.com/problems/number-of-even-and-odd-bits/?envType=problem-list-v2&envId=2cf6vun6

    - ta có thể dùng toString(2) => để giải 1 số thành số nhị phân
    - toString có thể nhận một tham số radix (hệ cơ số) để chuyển đổi số thành chuỗi trong hệ cơ số tương ứng. Khi bạn gọi a.toString(2), bạn đang yêu cầu JavaScript chuyển đổi số a (trong trường hợp này là 50) thành chuỗi nhị phân.
 */

function evenOddBit(n: number): number[] {
  const result: number[] = [];
  const binary = n.toString(2);
  let even = 0,
    odd = 0;
  const leng = binary.length;
  for (let i = 0; i < leng; i++) {
    if (binary[i] === "1") {
      if ((leng - i - 1) % 2 == 0) {
        even++;
      } else {
        odd++;
      }
    }
  }
  return [even, odd];
}

// https://leetcode.com/problems/number-of-changing-keys/?envType=problem-list-v2&envId=2cf6vun6
/**
    xem có bao nhiêu sự thay đổi nút khi gõ nhập chuỗi
    các phím CapsLock hay Shift không được tính
    ví dụ, nếu aA thì chỉ là 1 nút a không hề có sự thay đổi nút nào cả
    ví dụ ABCa, thì có 3, A-> B, B-> C C->a
 */
function countKeyChanges(s: string): number {
  let last: string = "";
  let count = 0;
  s.toLowerCase()
    .split("")
    .forEach((char: string) => {
      if (last !== char) {
        count++;
        last = char;
      }
    });

  return count - 1;
}

console.log(countKeyChanges("aAbBcC"));
console.log(countKeyChanges("AaAaAaaA"));

// /**
//  * Example 1:

// Input: jewels = "aA", stones = "aAAbbbb"
// Output: 3
// Example 2:

// Input: jewels = "z", stones = "ZZ"
// Output: 0
//  */
// function numJewelsInStones(jewels: string, stones: string): number {
//   let count = 0;

//   const mapJewel = {};

//   for (let i = 0; i < jewels.length; i++) {
//     mapJewel[jewels[i]] = 1;
//   }

//   for (let i = 0; i < stones.length; i++) {
//     if (mapJewel[stones[i]]) {
//       count++;
//     }
//   }
//   return count;
// }
// console.log(numJewelsInStones("aA", "aAAbbbb"));
// console.log(numJewelsInStones("z", "ZZ"));

// //https://leetcode.com/problems/make-a-square-with-the-same-color/?envType=problem-list-v2&envId=2cf6vun6
// function canMakeSquare(grid: string[][]): boolean {
//   const mapColor = {};

//   for (let i = 0; i < grid.length; i++) {
//     for (let j = 0; j < grid[i].length; j++) {
//       mapColor[`${i},${j}`] = grid[i][j] === "B" ? 1 : 0;
//     }
//   }

//   const a = [0, 1, 3, 4];

//   for (let i = 0; i < grid.length - 1; i++) {
//     for (let j = 0; j < grid[i].length - 1; j++) {
//       const sum =
//         mapColor[`${i},${j}`] +
//         mapColor[`${i},${j + 1}`] +
//         mapColor[`${i + 1},${j}`] +
//         mapColor[`${i + 1},${j + 1}`];

//       if (sum !== 2) {
//         return true;
//       }
//     }
//   }

//   return false;
// }

// console.log(
//   canMakeSquare([
//     ["B", "W", "B"],
//     ["B", "W", "W"],
//     ["B", "W", "B"],
//   ])
// );
// console.log(
//   canMakeSquare([
//     ["B", "W", "B"],
//     ["W", "B", "W"],
//     ["B", "W", "B"],
//   ])
// );
// console.log(
//   canMakeSquare([
//     ["B", "W", "B"],
//     ["B", "W", "W"],
//     ["B", "W", "W"],
//   ])
// );

// //https://leetcode.com/problems/redistribute-characters-to-make-all-strings-equal/?envType=problem-list-v2&envId=2cf6vun6
// function makeEqual(words: string[]): boolean {
//   /**
//         - dịch chuyển các kí tự ở các string sao cho tất cả các string đều bằng nhau. nếu có thể trả về true
//         - tiếp cận: các string bằng nhau thì số các kí tự của 1 loại trong words cần phải được chia đều và không dư
//         - nếu dư sẽ là không bằng nhau return false
//         - ví dụ words = ["abc","aabc","bc"] thì có 3a, 3b,3c chia đều được cho số phần tử là 3
//      */

//   const characterCount: Record<string, number> = {};

//   for (const word of words) {
//     // Count each character in the current word
//     for (const character of word) {
//       characterCount[character] = (characterCount[character] || 0) + 1;
//     }
//   }

//   //  tất cả các phần tử trong map đều phải có value chia hết cho words.length
//   const leng = words.length;

//   return Object.values(characterCount).every(
//     (count: any) => count % leng === 0
//   );
// }
// console.log("makeEqual");
