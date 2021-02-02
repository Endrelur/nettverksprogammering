package øvinger.en.web;

import java.io.Closeable;

public class Utils {

    public static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            // Shh...
        }
    }
}
