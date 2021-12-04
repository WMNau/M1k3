package nau.mike.m1k3.engine.cameras;

import lombok.Getter;
import nau.mike.m1k3.engine.utils.MathUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@SuppressWarnings("unused")
@Getter
public class Camera {

  protected final Vector3f position;
  protected final Vector3f rotation;

  private Matrix4f viewMatrix;

  public Camera() {
    this(new Vector3f(0.0f), new Vector3f(0.0f));
  }

  public Camera(final Vector3f position, final Vector3f rotation) {
    this.position = position;
    this.rotation = rotation;
    setViewMatrix();
  }

  public void movePosition(final float x, final float y, final float z) {
    if (0 != z) {
      position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * z;
      position.z += (float) Math.cos(Math.toRadians(rotation.y)) * z;
    }
    if (0 != x) {
      position.x += (float) Math.sin(Math.toRadians(rotation.y - 90.0f)) * -1.0f * x;
      position.z += (float) Math.cos(Math.toRadians(rotation.y - 90.0f)) * x;
    }
    position.y += y;
    setViewMatrix();
  }

  public void movePosition(final Vector3f position) {
    movePosition(position.x, position.y, position.z);
  }

  public void setPosition(final float x, final float y, final float z) {
    position.set(x, y, z);
    setViewMatrix();
  }

  public void setPosition(final Vector3f position) {
    this.position.set(position);
    setViewMatrix();
  }

  public void moveRotation(final float x, final float y, final float z) {
    rotation.x += x;
    rotation.y += y;
    rotation.z += z;
    setViewMatrix();
  }

  public void moveRotation(final Vector3f rotation) {
    moveRotation(rotation.x, rotation.y, rotation.z);
    setViewMatrix();
  }

  public void setRotation(final float x, final float y, final float z) {
    rotation.set(x, y, z);
    setViewMatrix();
  }

  public void setRotation(final Vector3f rotation) {
    this.rotation.set(rotation);
    setViewMatrix();
  }

  private void setViewMatrix() {
    viewMatrix = MathUtil.createViewMatrix(this);
  }
}
