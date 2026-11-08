package dp;

public class EditDistance {

    private final int distance;
    private long cellsFilled;

    public EditDistance(String source, String target) {
        int rows = source.length();
        int columns = target.length();
        int[][] table = new int[rows + 1][columns + 1];
        for (int i = 0; i <= rows; i++) {
            table[i][0] = i;
        }
        for (int j = 0; j <= columns; j++) {
            table[0][j] = j;
        }
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                cellsFilled++;
                if (source.charAt(i - 1) == target.charAt(j - 1)) {
                    table[i][j] = table[i - 1][j - 1];
                } else {
                    int substitution = table[i - 1][j - 1];
                    int deletion = table[i - 1][j];
                    int insertion = table[i][j - 1];
                    table[i][j] = 1 + Math.min(substitution, Math.min(deletion, insertion));
                }
            }
        }
        distance = table[rows][columns];
    }

    public int getDistance() {
        return distance;
    }

    public long getCellsFilled() {
        return cellsFilled;
    }
}
