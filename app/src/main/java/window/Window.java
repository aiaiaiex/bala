package window;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import event.Event;
import event.Observer;
import event.Subject;

public class Window implements Observer {
    private static Window window = null;

    private int width, height;
    private String title = "Bala";

    private Window() {
        DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDisplayMode();
        width = displayMode.getWidth();
        height = displayMode.getHeight();
        Subject.addObserver(this);
    }

    public static Window getWindow() {
        if (window == null) {
            window = new Window();
        }

        return window;
    }

    @Override
    public void notify(Event event) {
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
    }
}
