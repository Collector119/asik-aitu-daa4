package matching;

import java.util.ArrayList;
import java.util.List;

public class RabinKarpMatcher {

    private static final long BASE = 256;
    private static final long MODULUS = 1_000_000_007L;

    private final List<Integer> occurrences = new ArrayList<>();
    private long comparisons;
    private long spuriousHits;

    public RabinKarpMatcher(String text, String pattern) {
        int textLength = text.length();
        int patternLength = pattern.length();
        if (patternLength == 0 || patternLength > textLength) {
            return;
        }
        long highestPower = 1;
        for (int i = 0; i < patternLength - 1; i++) {
            highestPower = highestPower * BASE % MODULUS;
        }
        long patternHash = 0;
        long windowHash = 0;
        for (int i = 0; i < patternLength; i++) {
            patternHash = (patternHash * BASE + pattern.charAt(i)) % MODULUS;
            windowHash = (windowHash * BASE + text.charAt(i)) % MODULUS;
        }
        for (int start = 0; start <= textLength - patternLength; start++) {
            if (windowHash == patternHash) {
                if (windowEqualsPattern(text, start, pattern)) {
                    occurrences.add(start);
                } else {
                    spuriousHits++;
                }
            }
            if (start < textLength - patternLength) {
                windowHash = (windowHash - text.charAt(start) * highestPower % MODULUS + MODULUS) % MODULUS;
                windowHash = (windowHash * BASE + text.charAt(start + patternLength)) % MODULUS;
            }
        }
    }

    private boolean windowEqualsPattern(String text, int start, String pattern) {
        for (int i = 0; i < pattern.length(); i++) {
            comparisons++;
            if (text.charAt(start + i) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public List<Integer> getOccurrences() {
        return occurrences;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSpuriousHits() {
        return spuriousHits;
    }
}
