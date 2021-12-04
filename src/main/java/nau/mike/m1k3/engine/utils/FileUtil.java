package nau.mike.m1k3.engine.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nau.mike.m1k3.engine.M1k3Exception;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class FileUtil {

  public static BufferedImage getImage(final String fileName, final String ext) throws IOException {
    final String path = String.format("/textures/%s.%s", fileName, ext);
    final URL url = getUrl(path);
    return ImageIO.read(url);
  }

  public static String getShaderSource(final String shaderFileName) throws IOException {
    return getFileSource("shaders", shaderFileName, "glsl");
  }

  public static String getTtfFontFile(final String fontFileName) {
    return getFile("fonts", fontFileName, "ttf");
  }

  public static String getTexturePath(final String fileName, final String ext) {
    return getPath("textures", fileName, ext);
  }

  public static URL getUrl(final String path) {
    log.debug("Getting URL for file {}", path);
    return Optional.ofNullable(FileUtil.class.getResource(path))
        .orElseThrow(() -> new M1k3Exception(String.format("File %s was not found", path)));
  }

  public static String getFile(final String directory, final String fileName, final String ext) {
    final String path = buildFilePath(directory, fileName, ext);
    final URL url = getUrl(path);
    return url.getFile();
  }

  public static String getFileSource(
      final String directory, final String fileName, final String ext) throws IOException {
    final String path = buildFilePath(directory, fileName, ext);
    log.debug("Loading file {}", path);
    final URL sourceUrl = getUrl(path);
    final StringBuilder stringBuilder = new StringBuilder();
    try (final BufferedReader sourceReader =
        new BufferedReader(new FileReader(sourceUrl.getFile()))) {
      String line;
      while ((line = sourceReader.readLine()) != null) {
        if (line.startsWith("#") && !line.toLowerCase(Locale.ROOT).equals("#version 400 core")) {
          final String file = line.replace("#", "");
          getSharedShader(stringBuilder, file);
        } else {
          stringBuilder.append(line).append("\n");
        }
      }
    }
    return stringBuilder.toString();
  }

  private static void getSharedShader(final StringBuilder stringBuilder, final String fileName)
      throws IOException {
    final String path = buildFilePath("shaders", fileName, "glsl");
    final URL url = getUrl(path);
    try (final BufferedReader reader = new BufferedReader(new FileReader(url.getFile()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line).append("\n");
      }
    }
  }

  public static String getPath(final String directory, final String fileName, final String ext) {
    final String path = buildFilePath(directory, fileName, ext);
    final URL url = getUrl(path);
    return url.getPath();
  }

  public static URL getUrl(final String directory, final String fileName, final String ext) {
    final String path = getPath(directory, fileName, ext);
    return getUrl(path);
  }

  private static String buildFilePath(
      final String directory, final String fileName, final String ext) {
    return String.format("/%s/%s.%s", directory, fileName, ext);
  }
}
