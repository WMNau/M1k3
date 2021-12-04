package nau.mike.m1k3.engine.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nau.mike.m1k3.engine.cameras.Camera;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MathUtil {

  public static float clamp(final float value, final float min, final float max) {
    return min(max, max(min, value));
  }

  public static Vector2f clamp(final Vector2f value, final float min, final float max) {
    final float x = clamp(value.x, min, max);
    final float y = clamp(value.y, min, max);
    return new Vector2f(x, y);
  }

  public static Vector3f clamp(final Vector3f value, final float min, final float max) {
    final float x = clamp(value.x, min, max);
    final float y = clamp(value.y, min, max);
    final float z = clamp(value.z, min, max);
    return new Vector3f(x, y, z);
  }

  public static Vector4f clamp(final Vector4f value, final float min, final float max) {
    final float x = clamp(value.x, min, max);
    final float y = clamp(value.y, min, max);
    final float z = clamp(value.z, min, max);
    final float w = clamp(value.w, min, max);
    return new Vector4f(x, y, z, w);
  }

  public static int clamp(final int value, final int min, final int max) {
    return min(max, max(min, value));
  }

  public static Vector2i clamp(final Vector2i value, final int min, final int max) {
    final int x = clamp(value.x, min, max);
    final int y = clamp(value.y, min, max);
    return new Vector2i(x, y);
  }

  public static Vector3i clamp(final Vector3i value, final int min, final int max) {
    final int x = clamp(value.x, min, max);
    final int y = clamp(value.y, min, max);
    final int z = clamp(value.z, min, max);
    return new Vector3i(x, y, z);
  }

  public static Vector4i clamp(final Vector4i value, final int min, final int max) {
    final int x = clamp(value.x, min, max);
    final int y = clamp(value.y, min, max);
    final int z = clamp(value.z, min, max);
    final int w = clamp(value.w, min, max);
    return new Vector4i(x, y, z, w);
  }

  public static ByteBuffer buffer(final byte[] data) {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final ByteBuffer buffer = stack.malloc(data.length);
      buffer.put(data).flip();
      return buffer;
    }
  }

  public static IntBuffer buffer(final int[] data) {
    final IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
    buffer.put(data).flip();
    return buffer;
  }

  public static FloatBuffer buffer(final float[] data) {
    final FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
    buffer.put(data).flip();
    return buffer;
  }

  public static FloatBuffer buffer(final Vector2f vec) {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final float[] data = toFloatArray(vec);
      final FloatBuffer buffer = stack.mallocFloat(data.length);
      buffer.put(data).flip();
      return buffer;
    }
  }

  public static FloatBuffer buffer(final Vector3f vec) {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final float[] data = toFloatArray(vec);
      final FloatBuffer buffer = stack.mallocFloat(data.length);
      buffer.put(data).flip();
      return buffer;
    }
  }

  public static FloatBuffer buffer(final Vector4f vec) {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final float[] data = toFloatArray(vec);
      final FloatBuffer buffer = stack.mallocFloat(data.length);
      buffer.put(data).flip();
      return buffer;
    }
  }

  public static FloatBuffer buffer(final Matrix3f data) {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final FloatBuffer buffer = stack.mallocFloat(9);
      data.get(buffer);
      return buffer;
    }
  }

  public static FloatBuffer buffer(final Matrix4f data) {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final FloatBuffer buffer = stack.mallocFloat(16);
      data.get(buffer);
      return buffer;
    }
  }

  public static Matrix4f createModelMatrix(
      final Vector3f transformation, final Vector3f rotation, final float scale) {
    final Matrix4f matrix = new Matrix4f().identity();
    matrix.translation(transformation);
    matrix
        .rotateX((float) Math.toRadians(rotation.x))
        .rotateY((float) Math.toRadians(rotation.y))
        .rotateZ((float) Math.toRadians(rotation.z));
    matrix.scale(scale);
    return matrix;
  }

  public static Matrix4f createModelMatrix(final Vector2f transformation, final Vector2f scale) {
    final Matrix4f matrix = new Matrix4f().identity();
    matrix.translation(new Vector3f(transformation.x, transformation.y, 0.0f));
    matrix.scale(new Vector3f(scale.x, scale.y, 1.0f));
    return matrix;
  }

  public static Matrix4f createViewMatrix(final Camera camera) {
    final Matrix4f matrix = new Matrix4f().identity();
    matrix
        .rotateX((float) Math.toRadians(camera.getRotation().x))
        .rotateY((float) Math.toRadians(camera.getRotation().y))
        .rotateZ((float) Math.toRadians(camera.getRotation().z));
    final Vector3f position =
        new Vector3f(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
    matrix.translate(position);
    return matrix;
  }

  public static Matrix4f createProjectionMatrix(
      final float fov, final float aspectRatio, final float zNear, final float zFar) {
    return new Matrix4f().identity().perspective(fov, aspectRatio, zNear, zFar);
  }

  public static float[] toFloatArray(final Vector2f vector) {
    return new float[] {vector.x, vector.y};
  }

  public static float[] toFloatArray(final Vector3f vector) {
    return new float[] {vector.x, vector.y, vector.z};
  }

  public static float[] toFloatArray(final Vector4f vector) {
    return new float[] {vector.x, vector.y, vector.z, vector.w};
  }

  public static float[] toFloatArray(final List<Float> list) {
    final float[] result = new float[list.size()];
    for (int i = 0; i < result.length; i++) {
      result[i] = list.get(i);
    }
    return result;
  }

  public static int[] toIntArray(final List<Integer> list) {
    final int[] result = new int[list.size()];
    for (int i = 0; i < result.length; i++) {
      result[i] = list.get(i);
    }
    return result;
  }

  public static float barryCentric(
      final Vector3f p1, final Vector3f p2, final Vector3f p3, final Vector2f pos) {
    float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
    float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
    float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
    float l3 = 1.0f - l1 - l2;
    return l1 * p1.y + l2 * p2.y + l3 * p3.y;
  }
}
