package kz.aitu.daa;

public class KmpSearch {
    public static int[] lps(String pattern) {
        int[] result = new int[pattern.length()];
        int length = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                result[i] = length;
                i++;
            } else if (length > 0) {
                length = result[length - 1];
            } else {
                i++;
            }
        }

        return result;
    }
}