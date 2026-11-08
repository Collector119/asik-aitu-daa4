package dp;

public class LongestCommonSubsequence {

    private final int length;
    private long cellsFilled;

    public LongestCommonSubsequence(String first, String second) {
        int rows = first.length();
        int columns = second.length();
        int[][] table = new int[rows + 1][columns + 1];
        for (int i = 0; i <= rows; i++) {
            table[i][0] = 0;
        }
        for (int j = 0; j <= columns; j++) {
            table[0][j] = 0;
        }
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                cellsFilled++;
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    table[i][j] = table[i - 1][j - 1] + 1;
                } else {
                    table[i][j] = Math.max(table[i - 1][j], table[i][j - 1]);
                }
            }
        }
        length = table[rows][columns];
    }

    public int getLength() {
        return length;
    }

    public long getCellsFilled() {
        return cellsFilled;
    }
}
