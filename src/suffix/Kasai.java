package suffix;

public class Kasai {

    private final int[] lcp;
    private long comparisons;

    public Kasai(String text, int[] suffixArray) {
        int length = text.length();
        int[] rank = new int[length];
        for (int i = 0; i < length; i++) {
            rank[suffixArray[i]] = i;
        }
        lcp = new int[length];
        int matched = 0;
        for (int position = 0; position < length; position++) {
            if (rank[position] == 0) {
                matched = 0;
                continue;
            }
            int previous = suffixArray[rank[position] - 1];
            while (position + matched < length && previous + matched < length) {
                comparisons++;
                if (text.charAt(position + matched) != text.charAt(previous + matched)) {
                    break;
                }
                matched++;
            }
            lcp[rank[position]] = matched;
            if (matched > 0) {
                matched--;
            }
        }
    }

    public int[] getLcp() {
        return lcp;
    }

    public long getComparisons() {
        return comparisons;
    }
}
