import dp.EditDistance;
import dp.LongestCommonSubsequence;
import matching.KmpMatcher;
import matching.NaiveMatcher;
import matching.RabinKarpMatcher;
import suffix.Kasai;
import suffix.SuffixArray;

import java.util.Arrays;
import java.util.List;

public class Tests {

    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        testLcsIdenticalStrings();
        testLcsCompletelyDifferentStrings();
        testLcsEmptyString();
        testLcsTextbookExample();

        testEditDistanceIdenticalStrings();
        testEditDistanceOneEmptyString();
        testEditDistanceSubstitution();
        testEditDistanceInsertion();
        testEditDistanceDeletion();
        testEditDistanceAllThreeOperations();

        testMatchingPatternAtBeginning();
        testMatchingPatternInMiddle();
        testMatchingPatternAtEnd();
        testMatchingMultipleOccurrences();
        testMatchingPatternAbsent();
        testMatchingPatternLongerThanText();
        testMatchingEmptyPattern();

        testKasaiOnBanana();

        System.out.println();
        System.out.println(passed + "/" + (passed + failed) + " checks passed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testLcsIdenticalStrings() {
        check("lcs: identical strings \"abcd\" -> 4", new LongestCommonSubsequence("abcd", "abcd").getLength() == 4);
    }

    private static void testLcsCompletelyDifferentStrings() {
        check("lcs: completely different strings \"abc\", \"xyz\" -> 0",
                new LongestCommonSubsequence("abc", "xyz").getLength() == 0);
    }

    private static void testLcsEmptyString() {
        check("lcs: empty first string -> 0", new LongestCommonSubsequence("", "abc").getLength() == 0);
        check("lcs: empty second string -> 0", new LongestCommonSubsequence("abc", "").getLength() == 0);
    }

    private static void testLcsTextbookExample() {
        check("lcs: \"ABCBDAB\", \"BDCABA\" -> 4", new LongestCommonSubsequence("ABCBDAB", "BDCABA").getLength() == 4);
    }

    private static void testEditDistanceIdenticalStrings() {
        check("edit distance: identical strings -> 0", new EditDistance("abcd", "abcd").getDistance() == 0);
    }

    private static void testEditDistanceOneEmptyString() {
        check("edit distance: \"\" -> \"abc\" is 3 insertions", new EditDistance("", "abc").getDistance() == 3);
        check("edit distance: \"abc\" -> \"\" is 3 deletions", new EditDistance("abc", "").getDistance() == 3);
    }

    private static void testEditDistanceSubstitution() {
        check("edit distance: \"cat\" -> \"cut\" is 1 substitution", new EditDistance("cat", "cut").getDistance() == 1);
    }

    private static void testEditDistanceInsertion() {
        check("edit distance: \"cat\" -> \"cart\" is 1 insertion", new EditDistance("cat", "cart").getDistance() == 1);
    }

    private static void testEditDistanceDeletion() {
        check("edit distance: \"cart\" -> \"cat\" is 1 deletion", new EditDistance("cart", "cat").getDistance() == 1);
    }

    private static void testEditDistanceAllThreeOperations() {
        check("edit distance: \"kitten\" -> \"sitting\" -> 3", new EditDistance("kitten", "sitting").getDistance() == 3);
    }

    private static void testMatchingPatternAtBeginning() {
        checkMatchers("pattern at the beginning", "abcxyz", "abc", List.of(0));
    }

    private static void testMatchingPatternInMiddle() {
        checkMatchers("pattern in the middle", "xyabcxy", "abc", List.of(2));
    }

    private static void testMatchingPatternAtEnd() {
        checkMatchers("pattern at the end", "xyzabc", "abc", List.of(3));
    }

    private static void testMatchingMultipleOccurrences() {
        checkMatchers("multiple overlapping occurrences", "abababa", "aba", List.of(0, 2, 4));
    }

    private static void testMatchingPatternAbsent() {
        checkMatchers("pattern absent", "abcdef", "xyz", List.of());
    }

    private static void testMatchingPatternLongerThanText() {
        checkMatchers("pattern longer than text", "abc", "abcd", List.of());
    }

    private static void testMatchingEmptyPattern() {
        checkMatchers("empty pattern", "abc", "", List.of());
    }

    private static void checkMatchers(String name, String text, String pattern, List<Integer> expected) {
        List<Integer> naive = new NaiveMatcher(text, pattern).getOccurrences();
        List<Integer> kmp = new KmpMatcher(text, pattern).getOccurrences();
        List<Integer> rabinKarp = new RabinKarpMatcher(text, pattern).getOccurrences();
        check("matching: " + name + ": naive returns " + expected, naive.equals(expected));
        check("matching: " + name + ": naive, KMP and Rabin-Karp return the same list",
                naive.equals(kmp) && naive.equals(rabinKarp));
    }

    private static void testKasaiOnBanana() {
        int[] expectedSuffixArray = {5, 3, 1, 0, 4, 2};
        int[] expectedLcp = {0, 1, 3, 0, 0, 2};
        int[] suffixArray = new SuffixArray("banana").getPositions();
        check("kasai: suffix array of \"banana\" is " + Arrays.toString(expectedSuffixArray),
                Arrays.equals(suffixArray, expectedSuffixArray));
        int[] lcp = new Kasai("banana", expectedSuffixArray).getLcp();
        check("kasai: LCP array of \"banana\" is " + Arrays.toString(expectedLcp), Arrays.equals(lcp, expectedLcp));
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS " + name);
        } else {
            failed++;
            System.out.println("FAIL " + name);
        }
    }
}
