package th.in.ffc.util;

public class PGIDGenerator {
    private static int counter = 1;
    private static final String PREFIX = "PG";

    public static synchronized String generatePGID() {
        // Format counter with leading zeros (7 digits)
        String formattedCounter = String.format("%07d", counter);

        // Increment counter
        counter = (counter % 9999999) + 1;

        // Combine prefix + counter
        return PREFIX + formattedCounter;
    }

}