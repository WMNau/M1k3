package nau.mike.m1k3.engine.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11C.glClearColor;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ColorUtil {

  @Getter private static Vector4f clearColor = new Vector4f(0.0f);

  public static void maroonClearColor() {
    setClearColor(0.502f, 0, 0);
  }

  public static void blackColor() {
    setClearColor(0.0f, 0.0f, 0.0f);
  }

  public static void setClearColor(
      final float red, final float green, final float blue, final float alpha) {
    clearColor.x = MathUtil.clamp(red, 0.0f, 1.0f);
    clearColor.y = MathUtil.clamp(green, 0.0f, 1.0f);
    clearColor.z = MathUtil.clamp(blue, 0.0f, 1.0f);
    clearColor.w = MathUtil.clamp(alpha, 0.0f, 1.0f);
    setClearColor();
  }

  public static void setClearColor(final float rgb, final float alpha) {
    float color = MathUtil.clamp(rgb, 0.0f, 1.0f);
    clearColor.x = color;
    clearColor.y = color;
    clearColor.z = color;
    clearColor.w = MathUtil.clamp(alpha, 0.0f, 1.0f);
    setClearColor();
  }

  public static void setClearColor(final float rgb) {
    setClearColor(rgb, 1.0f);
  }

  public static void setClearColor(final float red, final float green, final float blue) {
    setClearColor(red, green, blue, 1.0f);
  }

  public static void setClearColor(
      final int red, final int green, final int blue, final int alpha) {
    clearColor.x = MathUtil.clamp(red, 0, 255) / 255.0f;
    clearColor.y = MathUtil.clamp(green, 0, 255) / 255.0f;
    clearColor.z = MathUtil.clamp(blue, 0, 255) / 255.0f;
    clearColor.w = MathUtil.clamp(alpha, 0, 255) / 255.0f;
    setClearColor();
  }

  public static void setClearColor(final int red, final int green, final int blue) {
    setClearColor(red, green, blue, 1.0f);
  }

  public static void setClearColor(final Vector4f value) {
    clearColor = MathUtil.clamp(value, 0.0f, 1.0f);
    setClearColor();
  }

  public static void setClearColor(final Vector3f value) {
    clearColor = new Vector4f(MathUtil.clamp(value, 0.0f, 1.0f), 1.0f);
    setClearColor();
  }

  private static void setClearColor() {
    glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
  }
}
