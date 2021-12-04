package nau.mike.m1k3;

import imgui.ImGui;
import lombok.Getter;
import nau.mike.m1k3.engine.FrameBuffer;
import nau.mike.m1k3.engine.Window;
import nau.mike.m1k3.engine.imgui.ImGuiLayer;
import nau.mike.m1k3.engine.input.Keyboard;
import nau.mike.m1k3.engine.input.Keys;
import nau.mike.m1k3.engine.scenes.SceneManager;

public class Application {

  @Getter private static FrameBuffer frameBuffer;

  private static final Window window = Window.getInstance();
  private static final ImGuiLayer imGuiLayer = ImGuiLayer.getInstance();

  private final SceneManager sceneManager;

  private Application() {
    Application.frameBuffer =
        new FrameBuffer(window.getVidMode().width(), window.getVidMode().height());
    this.sceneManager = new SceneManager();
  }

  private void init() {
    sceneManager.init();
  }

  private void update() {
    sceneManager.update();
  }

  private void render() {
    sceneManager.render();
  }

  private void imGui() {
    sceneManager.imGui();
    ImGui.showDemoWindow();
  }

  private void save() {
    sceneManager.save();
  }

  private void clean() {
    sceneManager.clean();
  }

  private void run() {
    while (!window.shouldClose()) {
      frameBuffer.bind();
      window.update();
      update();
      render();
      frameBuffer.unbind();
      imGuiLayer.update();
      imGuiLayer.startFrame();
      imGui();
      imGuiLayer.endFrame();
      window.render();

      if (Keyboard.pressed(Keys.ESCAPE)) {
        window.close();
      }
    }
  }

  public static void main(String[] args) {
    imGuiLayer.init();
    final Application application = new Application();
    application.init();
    application.run();
    application.save();
    application.clean();
    imGuiLayer.clean();
    window.destroy();
  }
}
