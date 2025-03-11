package model.DAO;

public class KMP {
    @SuppressWarnings("unused")
    // Os métodos são estáticos, mas eu inicializo para garantir que o log esteja carregado
    private static LogDAO logDAO = new LogDAO();
    
    private static String log = LogDAO.getLog();
    

    public static int[] searchLog(String pattern) {
        // Return the indexes of the lines where the pattern was found
        return getLines(pattern);
    }


    private static int[] getLines(String pattern) {
        // Create the LPS array
        int[] lps = new int[pattern.length()];
        createLPSArray(pattern, lps);

        // Search for the pattern in the log
        int[] lines = new int[log.split("\n").length];
        searchPattern(pattern, log, lps, lines);

        return lines;
    }


    private static void searchPattern(String pattern, String log, int[] lps, int[] lines) {
        int i = 0;
        int j = 0;

        while (i < log.length()) {
            if (pattern.charAt(j) == log.charAt(i)) {
                i++;
                j++;
            }

            if (j == pattern.length()) {
                lines[getLineIndex(log, i, j)] = getLineIndex(log, i, j);
                j = lps[j - 1];
            } else if (i < log.length() && pattern.charAt(j) != log.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
    }


    private static int getLineIndex(String log, int i, int j) {
        int lineIndex = 0;

        for (int k = 0; k < i; k++) {
            if (log.charAt(k) == '\n') {
                lineIndex++;
            }
        }

        return lineIndex;
    }


    // Create the LPS array
    private static void createLPSArray(String pattern, int[] lps) {
        int len = 0;
        int i = 1;

        lps[0] = 0;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = len;
                    i++;
                }
            }
        }
    }


    public static void main(String[] args) {
        String pattern = "CACHE INSERT";

        // Print all the log lines where the pattern was found
        int[] lines = searchLog(pattern);
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] != 0) {
                System.out.println(LogDAO.getLine(i));
            }
        }
    }
}
