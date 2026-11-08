package suffix;

import java.util.Arrays;

public class SuffixArray {

    private final int[] positions;

    public SuffixArray(String text) {
        Integer[] starts = new Integer[text.length()];
        for (int i = 0; i < starts.length; i++) {
            starts[i] = i;
        }
        Arrays.sort(starts, (first, second) -> text.substring(first).compareTo(text.substring(second)));
        positions = new int[starts.length];
        for (int i = 0; i < starts.length; i++) {
            positions[i] = starts[i];
        }
    }

    public int[] getPositions() {
        return positions;
    }
}
