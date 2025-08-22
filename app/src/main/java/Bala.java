import java.util.Arrays;
import java.util.logging.Logger;
import utilities.GlobalLogger;
import window.Window;

public class Bala {
    private static final Logger LOGGER = GlobalLogger.getLogger();

    public static void main(String[] args) {
        LOGGER.fine(() -> String.format("Method called with: (args=%1$s)", Arrays.toString(args)));

        Window window = Window.getWindow();
        window.run();

        LOGGER.fine("Method returned: void");
    }
}
