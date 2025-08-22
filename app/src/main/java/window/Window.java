package window;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.util.logging.Logger;
import event.Event;
import event.Observer;
import event.Subject;
import utilities.GlobalLogger;

public class Window implements Observer {
    private static final Logger logger = GlobalLogger.getLogger();

    private static Window window = null;

    private int width, height;
    private static String title = "Bala";

    private Window() {
        logger.fine("Class instantiated");

        logger.fine(() -> String.format("Old width: %1$s", width));
        logger.fine(() -> String.format("Old height: %1$s", height));
        DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDisplayMode();
        width = displayMode.getWidth();
        height = displayMode.getHeight();
        logger.fine(() -> String.format("New width: %1$s", width));
        logger.fine(() -> String.format("New height: %1$s", height));

        Subject.addObserver(this);
    }

    public static Window getWindow() {
        logger.fine("Method called");

        if (window == null) {
            logger.fine(() -> String.format("Old window: %1$s", window));
            window = new Window();
            logger.fine(() -> String.format("New window: %1$s", window));
        }

        logger.fine(() -> String.format("Method returned: %1$s", window));
        return window;
    }

    public void run() {
        logger.fine("Method called");

        logger.fine("Method returned: void");
    }

    @Override
    public void notify(Event event) {
        logger.fine(() -> String.format("Method called with: (event=%1$s)", event));

        switch (event) {
            case START_GAME:
                // TODO Create reaction to START_GAME.
                break;
            case STOP_GAME:
                // TODO Create reaction to STOP_GAME.
                break;
            case SAVE_LEVEL:
                // TODO Create reaction to SAVE_LEVEL.
                break;
            case LOAD_LEVEL:
                // TODO Create reaction to LOAD_LEVEL.
                break;
        }

        logger.fine("Method returned: void");

    }
}
