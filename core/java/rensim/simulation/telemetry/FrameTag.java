package rensim.simulation.telemetry;

/** Coordinate frame tag used in telemetry payloads. */
public enum FrameTag {
  WORLD("w"),
  BODY("b");

  private final String token;

  FrameTag(String token) {
    this.token = token;
  }

  public String token() {
    return token;
  }

  public static FrameTag fromToken(String token) {
    return switch (token) {
      case "w" -> WORLD;
      case "b" -> BODY;
      default -> throw new IllegalArgumentException("frame tag must be 'w' or 'b' but was: " + token);
    };
  }
}
