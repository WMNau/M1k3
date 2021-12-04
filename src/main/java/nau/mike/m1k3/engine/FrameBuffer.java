package nau.mike.m1k3.engine;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static org.lwjgl.opengl.GL30C.*;

@Slf4j
public class FrameBuffer {

  @Getter private final int fbo;

  private final Texture texture;

  public FrameBuffer(int width, int height) {
    this.fbo = glGenFramebuffers();
    bind();
    this.texture = new Texture(width, height);
    createFramebuffer(width, height);
    unbind();
  }

  public void bind() {
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
  }

  public void unbind() {
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
  }

  private void createFramebuffer(final int width, final int height) {
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getId(), 0);
    final int rbo = glGenRenderbuffers();
    glBindRenderbuffer(GL_RENDERBUFFER, rbo);
    glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
    glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo);
    final int success = glCheckFramebufferStatus(GL_FRAMEBUFFER);
    if (success != GL_FRAMEBUFFER_COMPLETE) {
      final String errorMessage = "Framebuffer did not succeed";
      log.error(errorMessage);
      throw new M1k3Exception(errorMessage);
    }
  }

  public int getTextureId() {
    return texture.getId();
  }
}
