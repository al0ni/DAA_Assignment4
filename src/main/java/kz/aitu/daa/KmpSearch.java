package kz.aitu.daa;

import java.util.ArrayList;
import java.util.List;

public class KmpSearch {
    public record Result(
            List<Integer> matches,
            long lpsComparisons,
            long searchComparisons
    ) {
        public long comparisons() {
            return lpsComparisons + searchComparisons;
        }
    }

    private record LpsResult(int[] values, long comparisons) {
    }

    public static int[] lps(String pattern) {
        return buildLps(pattern).values();
    }

    private static LpsResult buildLps(String pattern) {
        int[] values = new int[pattern.length()];
        long comparisons = 0;
        int length = 0;
        int i = 1;

        while (i < pattern.length()) {
            comparisons++;
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                values[i] = length;
                i++;
            } else if (length > 0) {
                length = values[length - 1];
            } else {
                i++;
            }
        }

        return new LpsResult(values, comparisons);
    }

    public static List<Integer> search(String text, String pattern) {
        return searchWithMetrics(text, pattern).matches();
    }

    public static Result searchWithMetrics(String text, String pattern) {
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be empty");
        }

        List<Integer> matches = new ArrayList<>();
        if (pattern.length() > text.length()) {
            return new Result(matches, 0, 0);
        }

        LpsResult prefix = buildLps(pattern);
        int[] lps = prefix.values();
        long comparisons = 0;
        int i = 0;
        int j = 0;

        while (i < text.length()) {
            comparisons++;
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

        return new Result(matches, prefix.comparisons(), comparisons);
    }
}