package kz.aitu.daa;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EditDistanceTest {
    @Test
    void handlesEmptyStrings() {
        assertAllVersions(0, "", "");
        assertAllVersions(3, "", "abc");
        assertAllVersions(3, "abc", "");
    }

    @Test
    void handlesKnownExamples() {
        assertAllVersions(0, "abc", "abc");
        assertAllVersions(1, "cat", "cut");
        assertAllVersions(2, "flaw", "lawn");
        assertAllVersions(3, "kitten", "sitting");
        assertAllVersions(5, "intention", "execution");
        assertAllVersions(5, "a", "abcdef");
        assertAllVersions(5, "abcdef", "a");
    }

    @Test
    void versionsAgreeOnRandomInputs() {
        Random random = new Random(2509);

        for (int i = 0; i < 100; i++) {
            String a = randomWord(random);
            String b = randomWord(random);
            int expected = RecursiveEditDistance.distance(a, b);
            String input = "a=\"" + a + "\", b=\"" + b + "\"";

            assertEquals(expected, TableEditDistance.distance(a, b), "Table: " + input);
            assertEquals(expected, TwoRowEditDistance.distance(a, b), "Two rows: " + input);
        }
    }

    private static void assertAllVersions(int expected, String a, String b) {
        String input = "a=\"" + a + "\", b=\"" + b + "\"";

        assertEquals(expected, RecursiveEditDistance.distance(a, b), "Recursive: " + input);
        assertEquals(expected, TableEditDistance.distance(a, b), "Table: " + input);
        assertEquals(expected, TwoRowEditDistance.distance(a, b), "Two rows: " + input);
    }

    private static String randomWord(Random random) {
        int length = random.nextInt(6);
        StringBuilder word = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            word.append((char) ('a' + random.nextInt(3)));
        }

        return word.toString();
    }
}