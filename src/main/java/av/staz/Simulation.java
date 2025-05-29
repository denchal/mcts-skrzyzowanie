package av.staz;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Simulation {
    private final Intersection intersection;
    public double averageWaitTime;
    private int MAX_COLOR_CHANGE_FREQUENCY;
    private int MAX_NEW_CARS;
    private int carCounter = 0;
    public int tick = 0;
    private int lastColorChange;
    Random random = new Random();

    public Simulation(Intersection intersection, int MAX_COLOR_CHANGE_FREQUENCY, int MAX_NEW_CARS) {
        this.intersection = intersection;
        this.MAX_COLOR_CHANGE_FREQUENCY = MAX_COLOR_CHANGE_FREQUENCY;
        this.MAX_NEW_CARS = MAX_NEW_CARS;
        this.lastColorChange = -(MAX_COLOR_CHANGE_FREQUENCY + 1);
    }

    public Intersection getIntersection() {
        return intersection;
    }

    public void step() {
        String id = String.valueOf(carCounter++);
        Direction start = getRandomDirection(random);
        Direction end = getRandomDirection(random);
        while (end == start) {
            end = getRandomDirection(random);
        }


        int times = random.nextInt(MAX_NEW_CARS) + 1;
        for (int i = 0; i < times; i++) {
            intersection.addCar(id, start, end);
        }

        if (intersection.step(tick - lastColorChange >= MAX_COLOR_CHANGE_FREQUENCY)) {
            lastColorChange = tick;
        }

        averageWaitTime = intersection.getAvgLifeTime();
        tick++;
    }

    public void addCarJson(String id, Direction start, Direction end) {
        intersection.addCar(id, start, end);
    }

    public void stepJson() {
        if (intersection.step(tick - lastColorChange >= MAX_COLOR_CHANGE_FREQUENCY)) {
            lastColorChange = tick;
        }

        averageWaitTime = intersection.getAvgLifeTime();
        tick++;
    }

    public void saveToJson(String path) throws IOException {
        System.out.println("Zapisuję symulację do: " + path);
        List<List<String>> rawData = intersection.getCarsOut();
        List<Map<String, Object>> stepStatuses = new ArrayList<>();
        for (List<String> vehicles : rawData) {
            Map<String, Object> step = new HashMap<>();
            step.put("leftVehicles", vehicles);
            stepStatuses.add(step);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("stepStatuses", stepStatuses);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("output.json"), result);

    }

    private static Direction getRandomDirection(Random random) {
        Direction[] values = Direction.values();
        return values[random.nextInt(values.length)];
    }
}

