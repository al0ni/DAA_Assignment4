package kz.aitu.daa;

import java.util.ArrayList;
import java.util.List;

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

    public static List<Integer> search(String text, String pattern) {
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be empty");
        }

        List<Integer> matches = new ArrayList<>();
        int[] lps = lps(pattern);
        int i = 0;
        int j = 0;

        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;

                if (j == pattern.length()) {
                    matches.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }

        return matches;
    }
}