package nau.mike.m1k3.engine.scenes;

public class SceneManager {

  private IScene scene;

  public SceneManager() {
    this.scene = null;
    switchScene(ScenType.EDITOR);
  }

  public void init() {
    if (null != scene) {
      scene.init();
    }
  }

  public void switchScene(final ScenType type) {
    if (null != scene) {
      scene.save();
      scene.clean();
    }
    switch (type) {
      case LEVEL:
        scene = new LevelScene();
        break;
      case EDITOR:
      default:
        scene = new LevelEditorScene();
    }
    init();
  }

  public void update() {
    scene.update();
  }

  public void render() {
    scene.render();
  }

  public void imGui() {
    scene.imGui();
  }

  public void save() {
    scene.save();
  }

  public void clean() {
    scene.clean();
  }
}
