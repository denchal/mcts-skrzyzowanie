package av.staz;

import java.util.ArrayList;
import java.util.List;

public record State(int north, int south, int east, int west, List<Direction> greenLights) {

    public List<State> getAvailableMoves() {
        List<State> moves = new ArrayList<>();

        for (Direction d : Direction.values()) {
            moves.add(new State(north, south, east, west, List.of(d)));
        }

        Direction[] directions = Direction.values();
        for (int i = 0; i < directions.length; i++) {
            for (int j = i + 1; j < directions.length; j++) {
                moves.add(new State(north, south, east, west, List.of(directions[i], directions[j])));
            }
        }

        return moves;
    }

    public double reward() {
        return (double) -((north - 1) * north + (south - 1) * south + (east - 1) * east + (west - 1) * west) / 2;
    }
}
