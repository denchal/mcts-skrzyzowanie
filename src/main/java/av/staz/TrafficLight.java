package av.staz;

public class TrafficLight {
    private LightColor color;

    public TrafficLight() {
        this.color = LightColor.RED;
    }

    public LightColor getColor() {
        return color;
    }

    public void setColor(LightColor color) {
        this.color = color;
    }
}
