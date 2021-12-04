package nau.mike.m1k3.engine;

@SuppressWarnings("unused")
public class M1k3Exception extends IllegalStateException {

  public M1k3Exception(final String message) {
    super(message);
  }

  public M1k3Exception(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
