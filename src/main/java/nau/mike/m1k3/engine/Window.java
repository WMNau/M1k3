package nau.mike.m1k3.engine;

import lombok.Getter;
import lombok.Setter;
import nau.mike.m1k3.engine.input.Keyboard;
import nau.mike.m1k3.engine.input.MouseButton;
import nau.mike.m1k3.engine.input.MousePosition;
import nau.mike.m1k3.engine.input.MouseScroll;
import nau.mike.m1k3.engine.utils.ColorUtil;
import nau.mike.m1k3.engine.utils.MathUtil;
import nau.mike.m1k3.engine.utils.Timer;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static nau.mike.m1k3.Config.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

  private static Window instance;

  @Getter private Matrix4f projectionMatrix;

  @Getter private long glfwWindow;
  @Getter private GLFWVidMode vidMode;

  @Getter private int wWidth;
  @Getter private int wHeight;
  private final String title;
  private final boolean vSync;

  @Getter private float aspectRatio;

  @Getter @Setter private boolean resized;

  private final GLFWErrorCallback glfwErrorCallback;
  @Getter private final Vector2i position;

  private Window() {
    this.wWidth = WIDTH;
    this.wHeight = HEIGHT;
    this.title = TITLE;
    this.vSync = V_SYNC;
    this.aspectRatio = (float) wWidth / wHeight;
    this.glfwErrorCallback = GLFWErrorCallback.createPrint(System.out);
    this.projectionMatrix = MathUtil.createProjectionMatrix(FOV, aspectRatio, Z_NEAR, Z_FAR);
    this.resized = true;
    this.position = new Vector2i(0);
  }

  private void init() {
    glfwErrorCallback.set();
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    configureGLFW();
    createGlfwWindow();
    glfwSwapInterval(vSync ? 1 : 0);
    GL.createCapabilities();
    setGlfwCallbacks();
    setResize(wWidth, wHeight);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_STENCIL_TEST);
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
    glfwShowWindow(glfwWindow);

    ColorUtil.blackColor();
  }

  public void update() {
    Timer.start();
    while (Timer.shouldUpdate()) {
      glfwPollEvents();
      Timer.update();
    }
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public void render() {
    glfwSwapBuffers(glfwWindow);
    Timer.render();
    if (Timer.shouldReset()) {
      if (DEBUG) {
        glfwSetWindowTitle(glfwWindow, String.format("%s | %s", title, Timer.message()));
      }
      Timer.reset();
    }
    MouseScroll.reset();
  }

  public boolean shouldClose() {
    return glfwWindowShouldClose(glfwWindow);
  }

  public void close() {
    close(true);
  }

  public void close(final boolean shouldClose) {
    glfwSetWindowShouldClose(glfwWindow, shouldClose);
  }

  public void destroy() {
    close();
    glfwFreeCallbacks(glfwWindow);
    glfwDestroyWindow(glfwWindow);

    glfwTerminate();
    glfwErrorCallback.free();
  }

  private void configureGLFW() {
    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);

    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
  }

  private void createGlfwWindow() {
    final long monitor = glfwGetPrimaryMonitor();
    vidMode = glfwGetVideoMode(monitor);
    boolean maximized = false;
    if (wWidth <= 0 || wHeight <= 0) {
      wWidth = 100;
      wHeight = 100;
      glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
      maximized = true;
    }
    glfwWindow = glfwCreateWindow(wWidth, wHeight, title, maximized ? monitor : NULL, NULL);
    if (glfwWindow == NULL) {
      throw new M1k3Exception("Failed to create the GLFW window");
    }
    if (maximized) {
      glfwMaximizeWindow(glfwWindow);
    } else {
      centerGlfwWindow();
    }
    glfwMakeContextCurrent(glfwWindow);
  }

  private void setGlfwCallbacks() {
    glfwSetFramebufferSizeCallback(glfwWindow, this::setFramebufferSizeCallback);
    glfwSetWindowSizeCallback(glfwWindow, this::setWindowSizeCallback);
    glfwSetWindowPosCallback(glfwWindow, this::setWindowPositionCallback);

    new Keyboard(glfwWindow);
    new MouseButton(glfwWindow);
    new MousePosition(glfwWindow);
    new MouseScroll(glfwWindow);
  }

  private void setWindowPositionCallback(final long window, final int x, final int y) {
    position.x = x;
    position.y = y;
  }

  private void setFramebufferSizeCallback(final long window, final int width, final int height) {
    setResize(width, height);
  }

  private void setWindowSizeCallback(final long window, final int width, final int height) {
    setResize(width, height);
  }

  private void setResize(final int width, final int height) {
    wWidth = width;
    wHeight = height;
    glViewport(0, 0, wWidth, wHeight);
    aspectRatio = (float) wWidth / wHeight;
    projectionMatrix = MathUtil.createProjectionMatrix(FOV, aspectRatio, Z_NEAR, Z_FAR);
    resized = true;
  }

  private void centerGlfwWindow() {
    try (final MemoryStack stack = stackPush()) {
      final IntBuffer pWidth = stack.mallocInt(1);
      final IntBuffer pHeight = stack.mallocInt(1);

      glfwGetWindowSize(glfwWindow, pWidth, pHeight);

      if (vidMode != null) {
        final int x = (vidMode.width() - pWidth.get(0)) / 2;
        final int y = (vidMode.height() - pHeight.get(0)) / 2;
        glfwSetWindowPos(glfwWindow, x, y);
        wWidth = vidMode.width();
        wHeight = vidMode.height();
        position.x = x;
        position.y = y;
      }
    }
  }

  public static Window getInstance() {
    if (instance == null) {
      instance = new Window();
      instance.init();
    }
    return instance;
  }
}
