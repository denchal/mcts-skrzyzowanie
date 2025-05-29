package av.staz;

public class Car {
    private Direction start;
    private final Direction end;
    private final String id;
    private int lifeTime;

    public Car(Direction start, Direction end, String id) {
        this.start = start;
        this.end = end;
        this.id = id;
        lifeTime = 0;
    }

    public Direction getStart() {
        return start;
    }

    public Direction getEnd() {
        return end;
    }

    public String getId() {
        return id;
    }

    public void drive() {
        this.start = this.end;
    }

    public boolean isDone() {
        return this.start.equals(this.end);
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public void addLifeTime() {
        this.lifeTime++;
    }

    public String toString() {
        return id + " " + start + " " + end + " " + lifeTime;
    }
}
