package gui;

import org.joml.Vector2f;
import event.Event;
import event.Subject;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import input.Mouse;
import window.Window;

public class GameViewWindow {
    private Subject subject;

    private boolean isPlaying;
    private boolean windowIsHovered;

    private Mouse mouse;
    private Window window;

    public GameViewWindow() {
        isPlaying = false;
        subject = Subject.getSubject();

        mouse = Mouse.getMouse();
        window = Window.getWindow();
    }

    public void imgui() {
        ImGui.begin("Game Scene", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse
                | ImGuiWindowFlags.MenuBar);

        ImGui.beginMenuBar();
        if (ImGui.menuItem("Play Game", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            subject.notifyObservers(Event.START_GAME);
        }
        if (ImGui.menuItem("Stop Game", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            subject.notifyObservers(Event.STOP_GAME);
        }

        if (ImGui.menuItem("Save Game Scene", "Ctrl+S", isPlaying, !isPlaying)) {
            subject.notifyObservers(Event.SAVE_LEVEL);
        }

        if (ImGui.menuItem("Load Game Scene", "", isPlaying, !isPlaying)) {
            subject.notifyObservers(Event.LOAD_LEVEL);
        }

        if (ImGui.menuItem("Clear Game Scene", "", isPlaying, !isPlaying)) {
            subject.notifyObservers(Event.CLEAR_LEVEL);
        }
        if (ImGui.menuItem("Procedurally Generate Non-Collidable Terrain", "", isPlaying,
                !isPlaying)) {
            subject.notifyObservers(Event.FILL_NON_COLLIDABLE_TERRAIN);
        }
        if (ImGui.menuItem("Generate Enemies", "", isPlaying, !isPlaying)) {
            subject.notifyObservers(Event.FILL_ENEMIES);
        }
        ImGui.endMenuBar();


        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        int textureId = Window.getFramebuffer().getTextureId();
        ImGui.imageButton(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);
        windowIsHovered = ImGui.isItemHovered();

        mouse.setGameViewportPos(
                new Vector2f(windowPos.x + ImGui.getWindowPosX() - window.getXPosition(),
                        windowPos.y + ImGui.getWindowPosY() - window.getYPosition()));
        mouse.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }

    public boolean getWantCaptureMouse() {
        return windowIsHovered;
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }
}
