package nau.mike.m1k3.engine.input;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;

/** MousePosition is a singleton class that is used to statically know the mouse position. */
@SuppressWarnings("unused")
@Slf4j
public class MousePosition implements GLFWCursorPosCallbackI {

  @Getter private static final Vector2f position;
  private static final Vector2f lastPosition;
  private static final Vector2f viewportPosition;
  private static final Vector2f viewportSize;

  @Getter private static boolean inWindow;

  static {
    position = new Vector2f(0.0f);
    lastPosition = new Vector2f(0.0f);
    viewportPosition = new Vector2f(0.0f);
    viewportSize = new Vector2f(0.0f);
    inWindow = false;
  }

  /**
   * Constructor
   *
   * @param window - instance of GlfwWindow
   */
  public MousePosition(final long window) {
    log.debug("Creating MousePosition");
    glfwSetCursorPosCallback(window, this);
    glfwSetCursorEnterCallback(window, (win, entered) -> inWindow = entered);
    log.debug("MousePosition created");
  }

  @Override
  public void invoke(long window, double xPos, double yPos) {
    lastPosition.x = position.x;
    lastPosition.y = position.y;
    position.x = (float) xPos;
    position.y = (float) yPos;
  }

  /**
   * Helper function to get the x position.
   *
   * @return float - (position.x - viewportPosition.x) / viewportSize.x
   */
  public static float getX() {
    float x = position.x - viewportPosition.x;
    if (viewportSize.x != 0.0f) {
      x = x / viewportSize.x;
    }
    return x;
  }

  /**
   * Helper function to get the y position.
   *
   * @return float - (position.y - viewportPosition.y) / viewportSize.y
   */
  public static float getY() {
    float y = position.y - viewportPosition.y;
    if (viewportSize.y != 0.0f) {
      y = y / viewportSize.y;
    }
    return y * -1.0f;
  }

  /**
   * Helper function to get the deltaX position.
   *
   * @return float - (position.x - lastPosition.x)
   */
  public static float getDx() {
    return position.x - lastPosition.x;
  }

  /**
   * Helper function to get the deltaY position.
   *
   * @return float - (position.y - lastPosition.y)
   */
  public static float getDy() {
    return position.y - lastPosition.y;
  }

  /**
   * Helper function to assist in debugging
   *
   * @return String position
   */
  public static String positionString() {
    final String result = String.format("Mouse Position: ( %f, %f)", getX(), getY());
    log.info(result);
    return result;
  }

  /**
   * Helper function to assist in debugging
   *
   * @return String last position
   */
  public static String lastPositionString() {
    final String result = String.format("Last Mouse Position( %f, %f)", getDx(), getDy());
    log.info(result);
    return result;
  }

  public static void setViewportPosition(final Vector2f viewportPosition) {
    MousePosition.viewportPosition.set(viewportPosition);
  }

  public static void setViewportSize(final Vector2f viewportSize) {
    MousePosition.viewportSize.set(viewportSize);
  }

  public static void setViewport(final Vector2f viewportPosition, final Vector2f viewportSize) {
    setViewportPosition(viewportPosition);
    setViewportSize(viewportSize);
  }
}
