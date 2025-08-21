package window;

import java.awt.GraphicsEnvironment;
import java.awt.DisplayMode;

public class Window {
    private static Window window = null;

    private int width, height;
    private String title = "Bala";

    private Window() {
        DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDisplayMode();
        width = displayMode.getWidth();
        height = displayMode.getHeight();

    }

    public static Window getWindow() {
        if (window == null) {
            window = new Window();
        }

        return window;
    }
}
