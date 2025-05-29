package av.staz;

import java.util.ArrayList;
import java.util.List;

public class Lane {
    private final TrafficLight light;
    private final List<Car> cars = new ArrayList<>();
    private final Direction position;

    public Lane(TrafficLight light, Direction position) {
        this.light = light;
        this.position = position;
    }

    public int getNumCars() {
        return cars.size();
    }

    public List<Car> getCars() {
        return cars;
    }

    public Direction getPosition() {
        return position;
    }

    public LightColor getLightColor() {
        return light.getColor();
    }

    public void setLightColor(LightColor color) {
        light.setColor(color);
    }
    
    public Car getTopCar() {
        if (!cars.isEmpty()) {
            return cars.get(0);
        }
        return null;
    }

    public void removeGone() {
        if (cars.isEmpty()) {
            return;
        }
        if (cars.get(0).isDone()) {
            cars.remove(0);
        }
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public double getTotalLifeTime() {
        double sum = 0;
        for (Car car : cars) {
            sum += car.getLifeTime();
        }
        return sum;
    }

    public void updateLifeTimes() {
        for (Car car : cars) {
            car.addLifeTime();
        }
    }

    public String toString() {
        return "Lane [light=" + light.getColor().toString() + ", cars=" + cars + "]";
    }
}
