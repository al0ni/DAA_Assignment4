package kz.aitu.daa;

import java.util.ArrayList;
import java.util.List;

public class NaiveSearch {
    public record Result(List<Integer> matches, long comparisons) {
    }

    public static List<Integer> search(String text, String pattern) {
        return searchWithMetrics(text, pattern).matches();
    }

    public static Result searchWithMetrics(String text, String pattern) {
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be empty");
        }

        List<Integer> matches = new ArrayList<>();
        long comparisons = 0;
        int lastStart = text.length() - pattern.length();

        for (int start = 0; start <= lastStart; start++) {
            int j = 0;

            while (j < pattern.length()) {
                comparisons++;
                if (text.charAt(start + j) != pattern.charAt(j)) {
                    break;
                }
                j++;
            }

            if (j == pattern.length()) {
                matches.add(start);
            }
        }

        return new Result(matches, comparisons);
    }
}