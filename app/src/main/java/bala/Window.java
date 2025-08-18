package bala;

import java.awt.GraphicsEnvironment;
import java.awt.DisplayMode;

public class Window {
    private int width, height;
    private String title = "Bala";

    private Window() {
        DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDisplayMode();
        width = displayMode.getWidth();
        height = displayMode.getHeight();

    }
}
