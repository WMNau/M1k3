package nau.mike.m1k3.engine.scenes;

public interface IScene {

  void init();

  void update();

  void render();

  void imGui();

  void save();

  void clean();
}
