package window;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.util.logging.Logger;
import event.Event;
import event.Observer;
import event.Subject;
import utilities.GlobalLogger;

public class Window implements Observer {
    private static final Logger LOGGER = GlobalLogger.getLogger();
    private static final String TITLE = "Bala";

    private static Window window = null;

    private int width, height;

    private Window() {
        LOGGER.fine("Class instantiated");

        LOGGER.fine(() -> String.format("Old width: %1$s", width));
        LOGGER.fine(() -> String.format("Old height: %1$s", height));
        DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDisplayMode();
        width = displayMode.getWidth();
        height = displayMode.getHeight();
        LOGGER.fine(() -> String.format("New width: %1$s", width));
        LOGGER.fine(() -> String.format("New height: %1$s", height));

        Subject.addObserver(this);
    }

    public static Window getWindow() {
        LOGGER.fine("Method called");

        if (window == null) {
            LOGGER.fine(() -> String.format("Old window: %1$s", window));
            window = new Window();
            LOGGER.fine(() -> String.format("New window: %1$s", window));
        }

        LOGGER.fine(() -> String.format("Method returned: %1$s", window));
        return window;
    }

    public void run() {
        LOGGER.fine("Method called");

        initialize();

        LOGGER.fine("Method returned: void");
    }

    private void initialize() {
        LOGGER.fine("Method called");

        LOGGER.fine("Method returned: void");
    }

    @Override
    public void notify(Event event) {
        LOGGER.fine(() -> String.format("Method called with: (event=%1$s)", event));

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

        LOGGER.fine("Method returned: void");

    }
}
