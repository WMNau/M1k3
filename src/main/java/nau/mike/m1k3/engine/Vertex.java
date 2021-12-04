package nau.mike.m1k3.engine;

import lombok.RequiredArgsConstructor;
import nau.mike.m1k3.engine.utils.MathUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@RequiredArgsConstructor
public class Vertex {

  private final float[] vertices;
  private final float[] normals;
  private final float[] uvs;
  private final int[] indices;

  public FloatBuffer getVertices() {
    return getBuffer(vertices);
  }

  public FloatBuffer getNormals() {
    return getBuffer(normals);
  }

  public FloatBuffer getUvs() {
    return getBuffer(uvs);
  }

  public IntBuffer getIndices() {
    return getBuffer(indices);
  }

  public FloatBuffer getBuffer(final float[] data) {
    return MathUtil.buffer(data);
  }

  public IntBuffer getBuffer(final int[] data) {
    return MathUtil.buffer(data);
  }

  public int getIndicesLength() {
    return indices.length;
  }
}
