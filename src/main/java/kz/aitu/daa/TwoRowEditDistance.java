package kz.aitu.daa;

public class TwoRowEditDistance {
    public record Result(int distance, long computedCells, long allocatedCells) {
    }

    public static int distance(String a, String b) {
        return distanceWithMetrics(a, b).distance();
    }

    public static Result distanceWithMetrics(String a, String b) {
        if (a.length() < b.length()) {
            String temp = a;
            a = b;
            b = temp;
        }

        int[] previous = new int[b.length() + 1];
        int[] current = new int[b.length() + 1];
        long allocatedCells = (long) previous.length + current.length;
        long computedCells = 0;

        for (int j = 0; j <= b.length(); j++) {
            previous[j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            current[0] = i;

            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                int delete = previous[j] + 1;
                int insert = current[j - 1] + 1;
                int replace = previous[j - 1] + cost;

                current[j] = Math.min(delete, Math.min(insert, replace));
                computedCells++;
            }

            int[] temp = previous;
            previous = current;
            current = temp;
        }

        return new Result(previous[b.length()], computedCells, allocatedCells);
    }
}