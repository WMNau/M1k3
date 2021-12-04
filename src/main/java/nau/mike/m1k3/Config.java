package nau.mike.m1k3;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joml.Vector3f;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {

  public static final boolean DEBUG = true;

  public static final String TITLE = "Tangerine Game Engine";
  public static final int WIDTH = 1920;
  public static final int HEIGHT = 1080;
  public static final boolean V_SYNC = true;
  public static final String glslVersion = "#version 400 core";

  public static final float FOV = 70.0f;
  public static final float Z_NEAR = 0.01f;
  public static final float Z_FAR = 1000.0f;

  public static final float CAMERA_MOVE_SPEED = 0.05f;
  public static final float CAMERA_ROTATE_SPEED = 0.007f;

  public static final Vector3f ambientLight = new Vector3f(1.3f);

  public static final float SPECULAR_POWER = 10.0f;
}
