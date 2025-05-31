package av.staz;

import java.util.Locale;

public enum Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    public static Direction fromString(String value) {
        return Direction.valueOf(value.toUpperCase(Locale.ROOT));
    }
}
