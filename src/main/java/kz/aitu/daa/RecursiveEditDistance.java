package kz.aitu.daa;

public class RecursiveEditDistance {
    public record Result(int distance, long recursiveCalls) {
    }

    private static class Counter {
        long calls;
    }

    public static int distance(String a, String b) {
        return distanceWithMetrics(a, b).distance();
    }

    public static Result distanceWithMetrics(String a, String b) {
        Counter counter = new Counter();
        int distance = solve(a, b, a.length(), b.length(), counter);
        return new Result(distance, counter.calls);
    }

    private static int solve(String a, String b, int i, int j, Counter counter) {
        counter.calls++;

        if (i == 0) {
            return j;
        }
        if (j == 0) {
            return i;
        }
        if (a.charAt(i - 1) == b.charAt(j - 1)) {
            return solve(a, b, i - 1, j - 1, counter);
        }

        int delete = solve(a, b, i - 1, j, counter);
        int insert = solve(a, b, i, j - 1, counter);
        int replace = solve(a, b, i - 1, j - 1, counter);

        return 1 + Math.min(delete, Math.min(insert, replace));
    }
}