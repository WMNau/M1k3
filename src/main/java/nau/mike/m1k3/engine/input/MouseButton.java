package nau.mike.m1k3.engine.input;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import static org.lwjgl.glfw.GLFW.*;

/**
 * MouseButton is a singleton class that is used to statically know when a mouse button has been or
 * is being pressed.
 */
@SuppressWarnings("unused")
@Slf4j
public class MouseButton implements GLFWMouseButtonCallbackI {

  @Getter private static MouseButton instance;

  private static long window = -1;

  /**
   * Constructor
   *
   * @param window - instance of GlfwWindow
   */
  public MouseButton(long window) {
    log.debug("Creating MouseButton");
    MouseButton.window = window;
    glfwSetMouseButtonCallback(window, this);
    instance = this;
    log.debug("MouseButton created");
  }

  @Override
  public void invoke(long window, int button, int action, int mods) {
    MouseButton.window = window;
  }

  /**
   * Helper function to know if the given button has been pressed or is being pressed.
   *
   * @param button - int
   * @return boolean - true if given button is pressed or being pressed
   */
  public static boolean pressed(int button) {
    return glfwGetMouseButton(window, button) == GLFW_PRESS
        || glfwGetMouseButton(window, button) == GLFW_REPEAT;
  }

  /**
   * Helper function to know if the given button has been released.
   *
   * @param button - int
   * @return boolean - true if given button is released
   */
  public static boolean released(int button) {
    return glfwGetMouseButton(window, button) == GLFW_RELEASE;
  }
}
