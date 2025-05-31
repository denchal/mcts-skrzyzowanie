package av.staz;

public record Trajectory(Direction start, Direction end) {

    public TurnType getTurnType() {
        return switch (start) {
            case NORTH -> end == Direction.WEST ? TurnType.RIGHT :
                          end == Direction.SOUTH ? TurnType.STRAIGHT :
                            TurnType.LEFT;
            case SOUTH -> end == Direction.EAST ? TurnType.RIGHT :
                          end == Direction.NORTH ? TurnType.STRAIGHT :
                            TurnType.LEFT;
            case EAST -> end == Direction.NORTH ? TurnType.RIGHT :
                         end == Direction.WEST ? TurnType.STRAIGHT :
                            TurnType.LEFT;
            case WEST -> end == Direction.SOUTH ? TurnType.RIGHT :
                         end == Direction.EAST ? TurnType.STRAIGHT :
                            TurnType.LEFT;
        };
    }

    public boolean conflictsWith(Trajectory other) {
        if (this.equals(other)) return false;

        TurnType thisTurn = this.getTurnType();
        TurnType otherTurn = other.getTurnType();

        // Zakładam, że pojazdy z naprzeciwka skręcające w tą samą stronę się nie blokują - ten który ma bliżej przejedzie pierwszy, bez kolizji

        // Lewoskręt koliduje z przeciwległym wprost i prawoskrętem
        if (thisTurn == TurnType.LEFT && other.start == opposite(this.start) && (otherTurn == TurnType.STRAIGHT || otherTurn == TurnType.LEFT)) {
            return true;
        }

        // Lewoskręt koliduje z prawym wprost i prawym lewoskrętem
        if (thisTurn == TurnType.LEFT && other.start == rightOf(this.start)
                && (otherTurn == TurnType.STRAIGHT || otherTurn == TurnType.LEFT)) {
            return true;
        }

        // Jazda na wprost koliduje z jakąkolwiek jazdą z prawej
        if (thisTurn == TurnType.STRAIGHT &&
                other.start == rightOf(this.start)) {
            return true;
        }

        return false;
    }


    private Direction opposite(Direction dir) {
        return switch (dir) {
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case EAST -> Direction.WEST;
            case WEST -> Direction.EAST;
        };
    }

    private Direction rightOf(Direction dir) {
        return switch (dir) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.NORTH;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trajectory)) return false;
        Trajectory other = (Trajectory) o;
        return this.start == other.start && this.end == other.end;
    }

}

