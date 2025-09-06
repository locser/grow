package design.algori.locph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class LeetCodeMay {

    public static void main(String[] args) {
        LeetCodeMay leetCodeMay = new LeetCodeMay();
        System.out.println(leetCodeMay.similarPairs(new String[] { "aba", "aabb", "abcd", "bac", "aabc" }));
    }

    // https://leetcode.com/problems/count-pairs-of-similar-strings/?envType=problem-list-v2&envId=2cf6vun6
    public int similarPairs(String[] words) {
        int ans = 0;
        Map<String, Integer> freq = new HashMap<>();

        // Xử lý từng từ để tạo các chuỗi đã chuẩn hóa
        String[] normalizedWords = new String[words.length];

        for (int i = 0; i < words.length; i++) {
            // Cách 1: Sử dụng TreeSet để lọc và sắp xếp cùng lúc
            TreeSet<Character> uniqueChars = new TreeSet<>();
            for (char c : words[i].toCharArray()) {
                uniqueChars.add(c);
            }

            // Chuyển Set thành String
            StringBuilder sb = new StringBuilder();
            for (char c : uniqueChars) {
                sb.append(c);
            }
            normalizedWords[i] = sb.toString();

            // Hoặc cách 2: Sử dụng Stream API
            // normalizedWords[i] = words[i].chars()
            // .distinct()
            // .sorted()
            // .collect(StringBuilder::new,
            // StringBuilder::appendCodePoint,
            // StringBuilder::append)
            // .toString();
        }

        // Đếm cặp từ đã chuẩn hóa
        for (String normalizedWord : normalizedWords) {
            ans += freq.getOrDefault(normalizedWord, 0);
            freq.put(normalizedWord, freq.getOrDefault(normalizedWord, 0) + 1);
        }

        return ans;
    }

    // https://leetcode.com/problems/count-the-number-of-incremovable-subarrays-i/?envType=problem-list-v2&envId=2cf6vun6

    // https://leetcode.com/problems/number-of-different-integers-in-a-string/?envType=problem-list-v2&envId=2cf6vun6
    public int numDifferentIntegers(String word) {
        return Arrays.stream(
                word.replaceAll("[a-zA-Z]", " ").split(" "))
                .filter(s -> !s.isEmpty())
                .map(s -> s.replaceFirst("^0+", ""))
                .collect(Collectors.toSet())
                .size();
    }

    // https://leetcode.com/problems/decode-xored-array/?envType=problem-list-v2&envId=2cf6vun6
    public int[] decode(int[] encoded, int first) {
        int[] decoded = new int[encoded.length + 1];

        decoded[0] = first;

        for (int i = 0; i < encoded.length; i++) {
            decoded[i + 1] = encoded[i] ^ decoded[i];
        }
        return decoded;
    }

    /**
     * https://leetcode.com/problems/strong-password-checker-ii/?envType=problem-list-v2&envId=2cf6vun6
     * Checks if the given password is strong according to the following rules:
     * - At least 8 characters long
     * - Contains at least one lowercase letter
     * - Contains at least one uppercase letter
     * - Contains at least one digit
     * - Contains at least one special character from the set "!@#$%^&*()-+"
     *
     * @param password the password string to check
     * @return true if the password is strong, false otherwise
     */
    public boolean strongPasswordCheckerII(String password) {
        if (password.length() < 8)
            return false;

        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        char prevChar = ' ';

        for (char s : password.toCharArray()) {
            if (s == prevChar)
                return false;
            prevChar = s;
            if (Character.isLowerCase(s))
                hasLower = true;
            if (Character.isUpperCase(s))
                hasUpper = true;
            if (Character.isDigit(s))
                hasDigit = true;
            if ("!@#$%^&*()-+".indexOf(s) != -1)
                hasSpecial = true;
        }

        return hasLower && hasUpper && hasDigit && hasSpecial;
    }

}
