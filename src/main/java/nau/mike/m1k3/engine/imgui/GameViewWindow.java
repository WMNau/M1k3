package nau.mike.m1k3.engine.imgui;

import imgui.ImVec2;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nau.mike.m1k3.Application;
import nau.mike.m1k3.engine.input.MousePosition;
import org.joml.Vector2f;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiWindowFlags.NoScrollWithMouse;
import static imgui.flag.ImGuiWindowFlags.NoScrollbar;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameViewWindow {

  private static float leftX;
  private static float rightX;
  private static float topY;
  private static float bottomY;

  public static void imGui() {
    begin("Game View", NoScrollbar | NoScrollWithMouse);
    final ImVec2 windowSize = getLargestSizeForViewport();
    final ImVec2 windowPosition = getCenteredPositionForViewport(windowSize);
    setCursorPos(windowPosition.x, windowPosition.y);

    final ImVec2 topLeft = new ImVec2();
    getCursorScreenPos(topLeft);
    topLeft.x -= getScrollX();
    topLeft.y -= getScrollY();
    leftX = topLeft.x;
    bottomY = topLeft.y;
    rightX = topLeft.x + windowSize.x;
    topY = topLeft.y + windowSize.y;
    MousePosition.setViewport(
        new Vector2f(topLeft.x, topLeft.y), new Vector2f(windowSize.x, windowSize.y));

    final int textureId = Application.getFrameBuffer().getTextureId();
    image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);
    end();
  }

  public static boolean getWantCaptureMouse() {
    final float mouseX = MousePosition.getX();
    final float mouseY = MousePosition.getY();
    return mouseX >= leftX && mouseX <= rightX && mouseY >= bottomY && mouseY <= topY;
  }

  private static ImVec2 getLargestSizeForViewport() {
    final ImVec2 windowSize = getWindowSize();
    float aspectWidth = windowSize.x;
    float aspectHeight = windowSize.y;
    return new ImVec2(aspectWidth, aspectHeight);
  }

  private static ImVec2 getCenteredPositionForViewport(final ImVec2 aspectSize) {
    final ImVec2 windowSize = getWindowSize();
    float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
    float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);
    return new ImVec2(viewportX + getCursorPosX(), viewportY + getCursorPosY());
  }

  private static ImVec2 getWindowSize() {
    final ImVec2 windowSize = new ImVec2();
    getContentRegionAvail(windowSize);
    windowSize.x -= getScrollX();
    windowSize.y -= getScrollY();
    return windowSize;
  }
}
