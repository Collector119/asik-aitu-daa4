package matching;

import java.util.ArrayList;
import java.util.List;

public class KmpMatcher {

    private final List<Integer> occurrences = new ArrayList<>();
    private long comparisons;

    public KmpMatcher(String text, String pattern) {
        int textLength = text.length();
        int patternLength = pattern.length();
        if (patternLength == 0 || patternLength > textLength) {
            return;
        }
        int[] lps = buildLps(pattern);
        int textIndex = 0;
        int matched = 0;
        while (textIndex < textLength) {
            comparisons++;
            if (text.charAt(textIndex) == pattern.charAt(matched)) {
                textIndex++;
                matched++;
                if (matched == patternLength) {
                    occurrences.add(textIndex - patternLength);
                    matched = lps[matched - 1];
                }
            } else if (matched > 0) {
                matched = lps[matched - 1];
            } else {
                textIndex++;
            }
        }
    }

    private int[] buildLps(String pattern) {
        int[] lps = new int[pattern.length()];
        int prefixLength = 0;
        int index = 1;
        while (index < pattern.length()) {
            comparisons++;
            if (pattern.charAt(index) == pattern.charAt(prefixLength)) {
                prefixLength++;
                lps[index] = prefixLength;
                index++;
            } else if (prefixLength > 0) {
                prefixLength = lps[prefixLength - 1];
            } else {
                lps[index] = 0;
                index++;
            }
        }
        return lps;
    }

    public List<Integer> getOccurrences() {
        return occurrences;
    }

    public long getComparisons() {
        return comparisons;
    }
}
