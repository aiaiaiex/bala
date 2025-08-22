import java.util.Arrays;
import java.util.logging.Logger;
import utilities.GlobalLogger;
import window.Window;

public class Bala {
    private static final Logger logger = GlobalLogger.getLogger();

    public static void main(String[] args) {
        logger.fine(() -> String.format("Method called with: (args=%1$s)", Arrays.toString(args)));

        Window window = Window.getWindow();
        window.run();

        logger.fine("Method returned: void");
    }
}
