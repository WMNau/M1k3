package nau.mike.m1k3.engine.imgui;

import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseCursor;
import imgui.flag.ImGuiTabBarFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.type.ImBoolean;
import lombok.Getter;
import nau.mike.m1k3.Config;
import nau.mike.m1k3.engine.Window;
import nau.mike.m1k3.engine.input.Keyboard;
import nau.mike.m1k3.engine.input.MouseButton;
import nau.mike.m1k3.engine.input.MousePosition;
import nau.mike.m1k3.engine.input.MouseScroll;
import nau.mike.m1k3.engine.utils.FileUtil;
import nau.mike.m1k3.engine.utils.Timer;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiBackendFlags.HasMouseCursors;
import static imgui.flag.ImGuiCond.Always;
import static imgui.flag.ImGuiConfigFlags.DockingEnable;
import static imgui.flag.ImGuiConfigFlags.NavEnableKeyboard;
import static imgui.flag.ImGuiStyleVar.WindowBorderSize;
import static imgui.flag.ImGuiStyleVar.WindowRounding;
import static imgui.flag.ImGuiWindowFlags.*;
import static nau.mike.m1k3.engine.input.Buttons.*;
import static org.lwjgl.glfw.GLFW.*;

public class ImGuiLayer {

  @Getter private static final ImGuiLayer instance = new ImGuiLayer();

  private final ImGuiImplGl3 imGuiImplGl3;

  private final long[] mouseCursors;

  private ImGuiIO io;

  private final ImVec2 windowSize;
  private final ImVec2 windowPosition;

  private final long glfwWindow;

  private ImGuiLayer() {
    this.imGuiImplGl3 = new ImGuiImplGl3();
    this.mouseCursors = new long[ImGuiMouseCursor.COUNT];
    final Window window = Window.getInstance();
    this.windowSize = new ImVec2(window.getWWidth(), window.getWHeight());
    this.windowPosition = new ImVec2(window.getPosition().x, window.getPosition().y);
    this.glfwWindow = window.getGlfwWindow();
  }

  public void init() {
    createContext();
    initIo();
    setKeyMap();
    mapMouseCursors();
    setCallbacks();
    setFonts();
    imGuiImplGl3.init(Config.glslVersion);
  }

  public void update() {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final IntBuffer fbWidth = stack.mallocInt(1);
      final IntBuffer fbHeight = stack.mallocInt(1);
      final IntBuffer winWidth = stack.mallocInt(1);
      final IntBuffer winHeight = stack.mallocInt(1);
      glfwGetFramebufferSize(glfwWindow, fbWidth, fbHeight);
      glfwGetWindowSize(glfwWindow, winWidth, winHeight);
      final float width = winWidth.get(0);
      final float height = winHeight.get(0);
      io.setDisplaySize(width, height);
      windowSize.x = width;
      windowSize.y = height;
      if (width > 0 && height > 0) {
        final float scaleX = fbWidth.get(0) / width;
        final float scaleY = fbHeight.get(0) / height;
        io.setDisplayFramebufferScale(scaleX, scaleY);
      }
      glfwGetWindowPos(glfwWindow, winWidth, winHeight);
      windowPosition.x = winWidth.get(0);
      windowPosition.y = winHeight.get(0);
    }
    io.setMousePos(MousePosition.getPosition().x, MousePosition.getPosition().y);
    io.setDeltaTime(Timer.getDelta());

    final int cursor = getMouseCursor();
    glfwSetCursor(glfwWindow, mouseCursors[cursor]);
    glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
  }

  public void startFrame() {
    newFrame();
    setDockspace();
  }

  public void endFrame() {
    GameViewWindow.imGui();
    end();
    render();
    imGuiImplGl3.renderDrawData(getDrawData());
  }

  public void clean() {
    imGuiImplGl3.dispose();
    destroyContext();
  }

  private void initIo() {
    final int configFlags = NavEnableKeyboard | DockingEnable | NoTitleBar | ImGuiTabBarFlags.None;
    io = getIO();
    io.setIniFilename(".config/imgui.ini");
    io.addConfigFlags(configFlags);
    io.addBackendFlags(HasMouseCursors);
    io.setBackendPlatformName("imgui_java_impl_glfw");
  }

  private void setKeyMap() {
    final int[] keyMap = new int[ImGuiKey.COUNT];
    keyMap[ImGuiKey.Tab] = GLFW_KEY_TAB;
    keyMap[ImGuiKey.LeftArrow] = GLFW_KEY_LEFT;
    keyMap[ImGuiKey.RightArrow] = GLFW_KEY_RIGHT;
    keyMap[ImGuiKey.UpArrow] = GLFW_KEY_UP;
    keyMap[ImGuiKey.DownArrow] = GLFW_KEY_DOWN;
    keyMap[ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP;
    keyMap[ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN;
    keyMap[ImGuiKey.Home] = GLFW_KEY_HOME;
    keyMap[ImGuiKey.End] = GLFW_KEY_END;
    keyMap[ImGuiKey.Insert] = GLFW_KEY_INSERT;
    keyMap[ImGuiKey.Delete] = GLFW_KEY_DELETE;
    keyMap[ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE;
    keyMap[ImGuiKey.Space] = GLFW_KEY_SPACE;
    keyMap[ImGuiKey.Enter] = GLFW_KEY_ENTER;
    keyMap[ImGuiKey.Escape] = GLFW_KEY_ESCAPE;
    keyMap[ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER;
    keyMap[ImGuiKey.A] = GLFW_KEY_A;
    keyMap[ImGuiKey.C] = GLFW_KEY_C;
    keyMap[ImGuiKey.V] = GLFW_KEY_V;
    keyMap[ImGuiKey.X] = GLFW_KEY_X;
    keyMap[ImGuiKey.Y] = GLFW_KEY_Y;
    keyMap[ImGuiKey.Z] = GLFW_KEY_Z;
    io.setKeyMap(keyMap);
  }

  private void mapMouseCursors() {
    mouseCursors[ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    mouseCursors[ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
    mouseCursors[ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    mouseCursors[ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
    mouseCursors[ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
    mouseCursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    mouseCursors[ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    mouseCursors[ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
    mouseCursors[ImGuiMouseCursor.NotAllowed] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
  }

  private void setCallbacks() {
    glfwSetKeyCallback(glfwWindow, this::keyCallback);
    glfwSetMouseButtonCallback(glfwWindow, this::mouseButtonCallback);
    glfwSetScrollCallback(glfwWindow, this::scrollCallback);
    io.setSetClipboardTextFn(setClipboardTextFn());
    io.setGetClipboardTextFn(getClipboardTextFn());
    glfwSetCharCallback(glfwWindow, this::charCallback);
  }

  private void setFonts() {
    final ImFontAtlas fontAtlas = io.getFonts();
    final ImFontConfig fontConfig = new ImFontConfig();

    fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

    fontConfig.setPixelSnapH(true);
    final String fontFile = FileUtil.getTtfFontFile("segoeui");
    fontAtlas.addFontFromFileTTF(fontFile, 16, fontConfig);

    fontConfig.destroy();
  }

  private void keyCallback(
      final long window, final int key, final int scancode, final int action, final int mods) {
    if (action == GLFW_PRESS) {
      io.setKeysDown(key, true);
    } else if (action == GLFW_RELEASE) {
      io.setKeysDown(key, false);
    }

    io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
    io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
    io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
    io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));

    final Keyboard keyboard = Keyboard.getInstance();
    if (!io.getWantCaptureKeyboard()) {
      keyboard.invoke(window, key, scancode, action, mods);
    }
  }

  private void mouseButtonCallback(
      final long window, final int button, final int action, final int mods) {
    final boolean[] mouseDown = new boolean[8];

    mouseDown[0] = button == BUTTON1 && action != GLFW_RELEASE;
    mouseDown[1] = button == BUTTON2 && action != GLFW_RELEASE;
    mouseDown[2] = button == BUTTON3 && action != GLFW_RELEASE;
    mouseDown[3] = button == BUTTON4 && action != GLFW_RELEASE;
    mouseDown[4] = button == BUTTON5 && action != GLFW_RELEASE;
    mouseDown[5] = button == BUTTON6 && action != GLFW_RELEASE;
    mouseDown[6] = button == BUTTON7 && action != GLFW_RELEASE;
    mouseDown[7] = button == BUTTON8 && action != GLFW_RELEASE;

    io.setMouseDown(mouseDown);

    if (!io.getWantCaptureMouse() && mouseDown[1]) {
      setWindowFocus(null);
    }

    final MouseButton mouseButton = MouseButton.getInstance();
    if (!io.getWantCaptureMouse() || !GameViewWindow.getWantCaptureMouse()) {
      mouseButton.invoke(window, button, action, mods);
    }
  }

  private void scrollCallback(final long window, double xOffset, final double yOffset) {
    io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
    io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
    MouseScroll mouseScroll = MouseScroll.getInstance();
    mouseScroll.invoke(window, xOffset, yOffset);
  }

  private ImStrConsumer setClipboardTextFn() {
    return new ImStrConsumer() {
      @Override
      public void accept(String text) {
        glfwSetClipboardString(glfwWindow, text);
      }
    };
  }

  private ImStrSupplier getClipboardTextFn() {
    return new ImStrSupplier() {
      @Override
      public String get() {
        final String clipboard = glfwGetClipboardString(glfwWindow);
        return clipboard == null ? "" : clipboard;
      }
    };
  }

  private void charCallback(final long window, final int c) {
    io.addInputCharacter(c);
  }

  private void setDockspace() {
    int windowFlags = MenuBar | NoDocking;
    setNextWindowPos(0.0f, 0.0f, Always);
    setNextWindowSize(windowSize.x, windowSize.y);
    pushStyleVar(WindowRounding, 0.0f);
    pushStyleVar(WindowBorderSize, 0.0f);

    windowFlags |= NoTitleBar | NoCollapse | NoResize | NoMove | NoBringToFrontOnFocus | NoNavFocus;
    begin("Docking", new ImBoolean(true), windowFlags);
    popStyleVar(2);
    dockSpace(getID("Dockspace"));
  }
}
