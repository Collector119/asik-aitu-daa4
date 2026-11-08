package matching;

import java.util.ArrayList;
import java.util.List;

public class NaiveMatcher {

    private final List<Integer> occurrences = new ArrayList<>();
    private long comparisons;

    public NaiveMatcher(String text, String pattern) {
        int textLength = text.length();
        int patternLength = pattern.length();
        if (patternLength == 0 || patternLength > textLength) {
            return;
        }
        for (int start = 0; start <= textLength - patternLength; start++) {
            int matched = 0;
            while (matched < patternLength) {
                comparisons++;
                if (text.charAt(start + matched) != pattern.charAt(matched)) {
                    break;
                }
                matched++;
            }
            if (matched == patternLength) {
                occurrences.add(start);
            }
        }
    }

    public List<Integer> getOccurrences() {
        return occurrences;
    }

    public long getComparisons() {
        return comparisons;
    }
}
