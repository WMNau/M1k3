package nau.mike.m1k3.engine.input;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Keyboard is a singleton class that is used to statically know when a key has been or is being
 * pressed.
 */
@SuppressWarnings("unused")
@Slf4j
public class Keyboard implements GLFWKeyCallbackI {

  @Getter private static Keyboard instance;

  private static long window = -1;

  /**
   * Constructor
   *
   * @param window - instance of GlfwWindow
   */
  public Keyboard(long window) {
    log.debug("Creating Keyboard");
    Keyboard.window = window;
    glfwSetKeyCallback(window, this);
    instance = this;
    log.debug("Keyboard created");
  }

  @Override
  public void invoke(long window, int key, int scancode, int action, int mods) {
    Keyboard.window = window;
  }

  /**
   * Helper function to know if the given button has been pressed or is being pressed.
   *
   * @param key - int
   * @return boolean - true if given key is pressed or being pressed
   */
  public static boolean pressed(int key) {
    return glfwGetKey(window, key) == GLFW_PRESS || glfwGetKey(window, key) == GLFW_REPEAT;
  }

  /**
   * Helper function to know if the given button has been released.
   *
   * @param key - int
   * @return boolean - true if given key is released
   */
  public static boolean released(int key) {
    return glfwGetKey(window, key) == GLFW_RELEASE;
  }
}
