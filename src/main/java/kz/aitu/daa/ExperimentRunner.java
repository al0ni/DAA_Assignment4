package kz.aitu.daa;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public class ExperimentRunner {
    private static final int WARMUP_RUNS = 3;
    private static final int MEASURED_RUNS = 5;
    private static volatile Metrics latestResult;

    private record Metrics(
            int result,
            long calls,
            long computedCells,
            long allocatedCells,
            long comparisons,
            long lpsComparisons,
            long searchComparisons
    ) {
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 2 && args[0].equals("probe")) {
            int n = Integer.parseInt(args[1]);
            String a = "a".repeat(n);
            String b = "b".repeat(n);

            long start = System.nanoTime();
            var result = RecursiveEditDistance.distanceWithMetrics(a, b);
            double timeMs = (System.nanoTime() - start) / 1_000_000.0;

            System.out.printf(Locale.ROOT, "%d,%d,%.6f,%d%n",
                    n, result.distance(), timeMs, result.recursiveCalls());
            return;
        }

        Path output = Path.of("results", "results.csv");
        Files.createDirectories(output.getParent());
        Random random = new Random(2509);

        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            writer.write("experiment,algorithm,input,n,m,run,time_ms,calls,"
                    + "cells,allocated_cells,comparisons,lps_comparisons,"
                    + "search_comparisons,result");
            writer.newLine();

            runEditSweep(writer, random, "edit_small", new int[]{2, 4, 6, 8, 10}, true);
            runEditSweep(writer, random, "edit_large", new int[]{128, 256, 512, 1024, 2048}, false);

            for (int n : new int[]{10000, 40000, 160000, 640000}) {
                runSearch(writer, "search_text", "random",
                        randomWord(random, n), randomWord(random, 32));

                String text = "a".repeat(n);
                runSearch(writer, "search_text", "near_miss", text, "a".repeat(31) + "b");
                runSearch(writer, "search_text", "overlap", text, "a".repeat(32));
            }

            String text = "a".repeat(160000);
            for (int m : new int[]{8, 32, 128, 512}) {
                runSearch(writer, "search_pattern", "near_miss", text, "a".repeat(m - 1) + "b");
            }
        }

        System.out.println("Saved " + output);
    }

    private static void runEditSweep(
            BufferedWriter writer, Random random, String experiment, int[] sizes,
            boolean includeRecursion
    ) throws IOException {
        for (int n : sizes) {
            String a = "a".repeat(n);
            runEdit(writer, experiment, "identical", a, a, includeRecursion);
            runEdit(writer, experiment, "disjoint", a, "b".repeat(n), includeRecursion);
            runEdit(writer, experiment, "random",
                    randomWord(random, n), randomWord(random, n), includeRecursion);
        }
    }

    private static void runEdit(
            BufferedWriter writer, String experiment, String input,
            String a, String b, boolean includeRecursion
    ) throws IOException {
        int n = a.length();
        int m = b.length();

        if (includeRecursion) {
            measure(writer, experiment, "Recursive", input, n, m, () -> {
                var r = RecursiveEditDistance.distanceWithMetrics(a, b);
                return new Metrics(r.distance(), r.recursiveCalls(), 0, 0, 0, 0, 0);
            });
        }

        measure(writer, experiment, "Table", input, n, m, () -> {
            var r = TableEditDistance.distanceWithMetrics(a, b);
            return new Metrics(r.distance(), 0, r.computedCells(), r.allocatedCells(), 0, 0, 0);
        });

        measure(writer, experiment, "TwoRows", input, n, m, () -> {
            var r = TwoRowEditDistance.distanceWithMetrics(a, b);
            return new Metrics(r.distance(), 0, r.computedCells(), r.allocatedCells(), 0, 0, 0);
        });
    }

    private static void runSearch(
            BufferedWriter writer, String experiment, String input,
            String text, String pattern
    ) throws IOException {
        int n = text.length();
        int m = pattern.length();

        measure(writer, experiment, "Naive", input, n, m, () -> {
            var r = NaiveSearch.searchWithMetrics(text, pattern);
            return new Metrics(r.matches().size(), 0, 0, 0, r.comparisons(), 0, r.comparisons());
        });

        measure(writer, experiment, "Kmp", input, n, m, () -> {
            var r = KmpSearch.searchWithMetrics(text, pattern);
            return new Metrics(r.matches().size(), 0, 0, 0,
                    r.comparisons(), r.lpsComparisons(), r.searchComparisons());
        });
    }

    private static void measure(
            BufferedWriter writer, String experiment, String algorithm, String input,
            int n, int m, Supplier<Metrics> operation
    ) throws IOException {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            latestResult = operation.get();
        }

        for (int run = 1; run <= MEASURED_RUNS; run++) {
            long start = System.nanoTime();
            Metrics r = operation.get();
            double timeMs = (System.nanoTime() - start) / 1_000_000.0;
            latestResult = r;

            writer.write(String.format(Locale.ROOT,
                    "%s,%s,%s,%d,%d,%d,%.9f,%d,%d,%d,%d,%d,%d,%d",
                    experiment, algorithm, input, n, m, run, timeMs,
                    r.calls(), r.computedCells(), r.allocatedCells(), r.comparisons(),
                    r.lpsComparisons(), r.searchComparisons(), r.result()));
            writer.newLine();
        }
    }

    private static String randomWord(Random random, int length) {
        StringBuilder word = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            word.append((char) ('a' + random.nextInt(26)));
        }
        return word.toString();
    }
}