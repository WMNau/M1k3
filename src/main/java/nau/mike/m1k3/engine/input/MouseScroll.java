package nau.mike.m1k3.engine.input;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWScrollCallbackI;

import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

/**
 * MouseScroll is a singleton class that is used to statically get the scrolls in the X and Y
 * positions.
 */
@SuppressWarnings("unused")
@Slf4j
public class MouseScroll implements GLFWScrollCallbackI {

  @Getter private static MouseScroll instance;

  @Getter private static final Vector2f offset = new Vector2f(0.0f);

  /**
   * Constructor
   *
   * @param window - instance of GlfwWindow
   */
  public MouseScroll(final long window) {
    log.debug("Creating MouseScroll");
    instance = this;
    glfwSetScrollCallback(window, this);
    log.debug("MouseScroll created");
  }

  @Override
  public void invoke(long window, double xOffset, double yOffset) {
    offset.x = (float) xOffset;
    offset.y = (float) yOffset;
  }

  /**
   * Helper function to get the x-axis.
   *
   * @return float - x-axis
   */
  public static float getX() {
    return offset.x;
  }

  /**
   * Helper function to get the y-axis.
   *
   * @return float - y-axis
   */
  public static float getY() {
    return offset.y;
  }

  /**
   * Useful if called once at the end of the game loop. If not used there, make sure you reset the
   * offset after each use or the offset will only grow.
   */
  public static void reset() {
    offset.set(0.0f);
  }
}
