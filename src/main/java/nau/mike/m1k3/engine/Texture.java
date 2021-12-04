package nau.mike.m1k3.engine;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nau.mike.m1k3.engine.utils.FileUtil;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

@SuppressWarnings("unused")
@Slf4j
@Getter
public class Texture {

  private final int id;
  private final int width;
  private final int height;
  private final ByteBuffer bufferedImage;

  public Texture(final Texture texture) {
    this.id = texture.id;
    this.width = texture.width;
    this.height = texture.height;
    this.bufferedImage = texture.bufferedImage;
  }

  public Texture(final String fileName, final String ext) {
    this.id = bindTexture();

    generateTextureParameters();
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

    try (final MemoryStack stack = stackPush()) {
      final IntBuffer w = stack.mallocInt(1);
      final IntBuffer h = stack.mallocInt(1);
      final IntBuffer c = stack.mallocInt(1);

      final String path = FileUtil.getTexturePath(fileName, ext);

      log.debug("Creating Texture: {}", path);
      this.bufferedImage =
          Optional.ofNullable(stbi_load(path, w, h, c, 4))
              .orElseThrow(
                  () -> {
                    final String errorMessage =
                        String.format("Image /textures/%s could not be found", fileName);
                    log.debug(errorMessage);
                    throw new M1k3Exception(errorMessage);
                  });

      final int nrChannel = c.get();
      final int internalFormat = nrChannel == 4 || nrChannel == 3 ? GL_RGBA : GL_RGB;
      this.width = w.get();
      this.height = h.get();

      storeTextureImage(internalFormat);
      glGenerateMipmap(GL_TEXTURE_2D);
      stbi_image_free(bufferedImage);
    }
    unbind();
    log.debug("Texture created");
  }

  public Texture(final int width, final int height) {
    log.debug("Creating Texture: Generated");
    this.width = width;
    this.height = height;
    this.bufferedImage = null;
    this.id = bindTexture();
    generateTextureParameters();
    storeTextureImage(GL_RGB);
    unbind();
    log.debug("Texture created");
  }

  public void bind(int activeIndex) {
    glActiveTexture(GL_TEXTURE0 + activeIndex);
    glBindTexture(GL_TEXTURE_2D, id);
  }

  public void bind() {
    bind(0);
  }

  public void bindCubeMap(final List<Texture> textureList) {
    final AtomicInteger i = new AtomicInteger(0);
    textureList.forEach(
        texture -> {
          glActiveTexture(GL_TEXTURE0 + i.getAndIncrement());
          glBindTexture(GL_TEXTURE_CUBE_MAP, texture.id);
        });
  }

  public void unbind() {
    glBindTexture(GL_TEXTURE_2D, 0);
  }

  private int bindTexture() {
    final int texture = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, texture);
    return texture;
  }

  private void generateTextureParameters() {
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
  }

  private void storeTextureImage(final int internalFormat) {
    if (bufferedImage == null) {
      glTexImage2D(
          GL_TEXTURE_2D,
          0,
          internalFormat,
          width,
          height,
          0,
          internalFormat,
          GL_UNSIGNED_BYTE,
          NULL);
    } else {
      glTexImage2D(
          GL_TEXTURE_2D,
          0,
          internalFormat,
          width,
          height,
          0,
          internalFormat,
          GL_UNSIGNED_BYTE,
          bufferedImage);
    }
  }
}
