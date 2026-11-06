package kz.aitu.daa;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchTest {
    @Test
    void buildsLpsArrays() {
        assertArrayEquals(new int[]{0, 0, 1, 2, 3, 0, 1}, KmpSearch.lps("ababaca"));
        assertArrayEquals(new int[]{0, 1, 2, 3}, KmpSearch.lps("aaaa"));
        assertArrayEquals(new int[]{0, 0, 0, 0}, KmpSearch.lps("abcd"));
        assertArrayEquals(new int[]{}, KmpSearch.lps(""));
    }

    @Test
    void findsAllMatchesIncludingOverlaps() {
        assertBothSearches(List.of(0, 1, 2, 3), "aaaaa", "aa");
        assertBothSearches(List.of(1, 3), "banana", "ana");
        assertBothSearches(List.of(0), "abc", "abc");
        assertBothSearches(List.of(2), "abc", "c");
    }

    @Test
    void handlesMissingMatchesAndEmptyInputs() {
        assertBothSearches(List.of(), "abc", "z");
        assertBothSearches(List.of(), "ab", "abc");
        assertBothSearches(List.of(), "", "a");

        assertThrows(IllegalArgumentException.class, () -> NaiveSearch.search("abc", ""));
        assertThrows(IllegalArgumentException.class, () -> KmpSearch.search("abc", ""));
    }

    @Test
    void searchesAgreeOnRandomInputs() {
        Random random = new Random(2509);

        for (int i = 0; i < 100; i++) {
            String text = randomWord(random, random.nextInt(31));
            String pattern = randomWord(random, 1 + random.nextInt(8));
            String input = "text=\"" + text + "\", pattern=\"" + pattern + "\"";

            assertEquals(NaiveSearch.search(text, pattern), KmpSearch.search(text, pattern), input);
        }
    }

    private static void assertBothSearches(List<Integer> expected, String text, String pattern) {
        assertEquals(expected, NaiveSearch.search(text, pattern));
        assertEquals(expected, KmpSearch.search(text, pattern));
    }

    private static String randomWord(Random random, int length) {
        StringBuilder word = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            word.append((char) ('a' + random.nextInt(3)));
        }

        return word.toString();
    }
}