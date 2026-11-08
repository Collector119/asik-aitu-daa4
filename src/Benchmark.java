import dp.EditDistance;
import dp.LongestCommonSubsequence;
import matching.KmpMatcher;
import matching.NaiveMatcher;
import matching.RabinKarpMatcher;
import suffix.Kasai;
import suffix.SuffixArray;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class Benchmark {

    private static final String ALPHABET = "abcd";
    private static final long SEED = 42;
    private static final int RUNS = 5;
    private static final int WARMUP_ROUNDS = 200;
    private static final int[] DP_LENGTHS = {100, 500, 1000};
    private static final int[] TEXT_LENGTHS = {10_000, 50_000, 100_000};
    private static final int[] KASAI_LENGTHS = {1000, 5000, 10_000};
    private static final int PATTERN_LENGTH = 20;
    private static final Path TABLES = Path.of("results", "tables");

    public static void main(String[] args) throws IOException {
        warmUp();
        Files.createDirectories(TABLES);
        runDynamicProgramming();
        runStringMatching();
        runKasai();
    }

    private static void warmUp() {
        String first = randomString(DP_LENGTHS[1], new Random(SEED));
        String second = randomString(DP_LENGTHS[1], new Random(SEED + 1));
        String text = randomString(TEXT_LENGTHS[1], new Random(SEED));
        String pattern = text.substring(text.length() / 2, text.length() / 2 + PATTERN_LENGTH);
        String kasaiText = randomString(KASAI_LENGTHS[1], new Random(SEED));
        int[] suffixArray = new SuffixArray(kasaiText).getPositions();
        for (int round = 0; round < WARMUP_ROUNDS; round++) {
            new LongestCommonSubsequence(first, second);
            new EditDistance(first, second);
            new NaiveMatcher(text, pattern);
            new KmpMatcher(text, pattern);
            new RabinKarpMatcher(text, pattern);
            new Kasai(kasaiText, suffixArray);
        }
    }

    private static void runDynamicProgramming() throws IOException {
        printTitle("LCS and Edit Distance (two random strings of equal length)");
        try (PrintWriter writer = openTable("dp.csv")) {
            writer.println("algorithm,length,avg_time_ns,result,cells_filled");
            for (int length : DP_LENGTHS) {
                Random random = new Random(SEED);
                String first = randomString(length, random);
                String second = randomString(length, random);
                write(writer, measureLcs(length, first, second));
                write(writer, measureEditDistance(length, first, second));
            }
        }
    }

    private static void runStringMatching() throws IOException {
        printTitle("String matching (pattern length " + PATTERN_LENGTH + ")");
        try (PrintWriter writer = openTable("matching.csv")) {
            writer.println("algorithm,text_length,pattern,avg_time_ns,occurrences,comparisons,spurious_hits");
            for (int length : TEXT_LENGTHS) {
                Random random = new Random(SEED);
                String text = randomString(length, random);
                String presentPattern = text.substring(length / 2, length / 2 + PATTERN_LENGTH);
                String absentPattern = randomString(PATTERN_LENGTH, random);
                measureAllMatchers(writer, length, "present", text, presentPattern);
                measureAllMatchers(writer, length, "absent", text, absentPattern);
            }
        }
    }

    private static void runKasai() throws IOException {
        printTitle("Kasai LCP array (random strings, suffix array built before timing)");
        try (PrintWriter writer = openTable("kasai.csv")) {
            writer.println("length,avg_time_ns,comparisons");
            for (int length : KASAI_LENGTHS) {
                String text = randomString(length, new Random(SEED));
                int[] suffixArray = new SuffixArray(text).getPositions();
                write(writer, measureKasai(length, text, suffixArray));
            }
        }
    }

    private static String measureLcs(int length, String first, String second) {
        long totalTime = 0;
        LongestCommonSubsequence lcs = null;
        for (int run = 0; run < RUNS; run++) {
            long start = System.nanoTime();
            lcs = new LongestCommonSubsequence(first, second);
            totalTime += System.nanoTime() - start;
        }
        return row("LCS", length, totalTime / RUNS, lcs.getLength(), lcs.getCellsFilled());
    }

    private static String measureEditDistance(int length, String first, String second) {
        long totalTime = 0;
        EditDistance editDistance = null;
        for (int run = 0; run < RUNS; run++) {
            long start = System.nanoTime();
            editDistance = new EditDistance(first, second);
            totalTime += System.nanoTime() - start;
        }
        return row("EditDistance", length, totalTime / RUNS, editDistance.getDistance(),
                editDistance.getCellsFilled());
    }

    private static void measureAllMatchers(PrintWriter writer, int length, String patternType,
                                           String text, String pattern) {
        write(writer, measureNaive(length, patternType, text, pattern));
        write(writer, measureKmp(length, patternType, text, pattern));
        write(writer, measureRabinKarp(length, patternType, text, pattern));
    }

    private static String measureNaive(int length, String patternType, String text, String pattern) {
        long totalTime = 0;
        NaiveMatcher matcher = null;
        for (int run = 0; run < RUNS; run++) {
            long start = System.nanoTime();
            matcher = new NaiveMatcher(text, pattern);
            totalTime += System.nanoTime() - start;
        }
        return row("Naive", length, patternType, totalTime / RUNS, matcher.getOccurrences().size(),
                matcher.getComparisons(), 0);
    }

    private static String measureKmp(int length, String patternType, String text, String pattern) {
        long totalTime = 0;
        KmpMatcher matcher = null;
        for (int run = 0; run < RUNS; run++) {
            long start = System.nanoTime();
            matcher = new KmpMatcher(text, pattern);
            totalTime += System.nanoTime() - start;
        }
        return row("KMP", length, patternType, totalTime / RUNS, matcher.getOccurrences().size(),
                matcher.getComparisons(), 0);
    }

    private static String measureRabinKarp(int length, String patternType, String text, String pattern) {
        long totalTime = 0;
        RabinKarpMatcher matcher = null;
        for (int run = 0; run < RUNS; run++) {
            long start = System.nanoTime();
            matcher = new RabinKarpMatcher(text, pattern);
            totalTime += System.nanoTime() - start;
        }
        return row("RabinKarp", length, patternType, totalTime / RUNS, matcher.getOccurrences().size(),
                matcher.getComparisons(), matcher.getSpuriousHits());
    }

    private static String measureKasai(int length, String text, int[] suffixArray) {
        long totalTime = 0;
        Kasai kasai = null;
        for (int run = 0; run < RUNS; run++) {
            long start = System.nanoTime();
            kasai = new Kasai(text, suffixArray);
            totalTime += System.nanoTime() - start;
        }
        return row(length, totalTime / RUNS, kasai.getComparisons());
    }

    private static String randomString(int length, Random random) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }

    private static String row(Object... cells) {
        StringBuilder line = new StringBuilder();
        for (Object cell : cells) {
            if (line.length() > 0) {
                line.append(',');
            }
            line.append(cell);
        }
        return line.toString();
    }

    private static PrintWriter openTable(String fileName) throws IOException {
        return new PrintWriter(Files.newBufferedWriter(TABLES.resolve(fileName)));
    }

    private static void write(PrintWriter writer, String row) {
        writer.println(row);
        System.out.println(row);
    }

    private static void printTitle(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
