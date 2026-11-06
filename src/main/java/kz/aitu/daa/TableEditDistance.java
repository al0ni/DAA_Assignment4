package kz.aitu.daa;

public class TableEditDistance {
    public record Result(int distance, long computedCells, long allocatedCells) {
    }

    public static int distance(String a, String b) {
        return distanceWithMetrics(a, b).distance();
    }

    public static Result distanceWithMetrics(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        long computedCells = 0;
        long allocatedCells = 0;

        for (int[] row : dp) {
            allocatedCells += row.length;
        }

        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                int delete = dp[i - 1][j] + 1;
                int insert = dp[i][j - 1] + 1;
                int replace = dp[i - 1][j - 1] + cost;

                dp[i][j] = Math.min(delete, Math.min(insert, replace));
                computedCells++;
            }
        }

        return new Result(dp[a.length()][b.length()], computedCells, allocatedCells);
    }
}