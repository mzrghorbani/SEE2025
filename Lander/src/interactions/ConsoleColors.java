package interactions;

public class ConsoleColors {
    // Reset color
    public static final String RESET = "\u001B[0m";

    // Regular text colors
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Bold text colors
    public static final String BLACK_BOLD = "\u001B[1;30m";
    public static final String RED_BOLD = "\u001B[1;31m";
    public static final String GREEN_BOLD = "\u001B[1;32m";
    public static final String YELLOW_BOLD = "\u001B[1;33m";
    public static final String BLUE_BOLD = "\u001B[1;34m";
    public static final String PURPLE_BOLD = "\u001B[1;35m";
    public static final String CYAN_BOLD = "\u001B[1;36m";
    public static final String WHITE_BOLD = "\u001B[1;37m";

    // Background colors
    public static final String RED_BACKGROUND = "\u001B[41m";
    public static final String GREEN_BACKGROUND = "\u001B[42m";
    public static final String YELLOW_BACKGROUND = "\u001B[43m";
    public static final String BLUE_BACKGROUND = "\u001B[44m";
    public static final String PURPLE_BACKGROUND = "\u001B[45m";
    public static final String CYAN_BACKGROUND = "\u001B[46m";
    public static final String WHITE_BACKGROUND = "\u001B[47m";

    // Private constructor to prevent instantiation
    private ConsoleColors() {}

    // Logging Utility (Static Methods)
    public static void logInfo(String message) {
        System.out.println(GREEN_BOLD + "[INFO] " + message + RESET);
    }

    public static void logWarning(String message) {
        System.out.println(YELLOW_BOLD + "[WARNING] " + message + RESET);
    }

    public static void logError(String message) {
        System.out.println(RED_BOLD + "[ERROR] " + message + RESET);
    }
    
    public static void logStatus(String message) {
        System.out.println(CYAN_BOLD + "[STATUS] " + message + RESET);
    }
}
