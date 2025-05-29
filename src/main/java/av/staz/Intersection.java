package av.staz;

import java.util.ArrayList;
import java.util.List;

public class Intersection {
    private final List<Lane> lanes;
    private double avgLifeTime;
    private int totalCars = 0;
    private int passedCars = 0;
    private List<List<String>> carsOut = new ArrayList<>();
    private double totalLifetime = 0;
    private double passedLifetime = 0;

    public Intersection() {
        lanes = new ArrayList<>();
        lanes.add(
                new Lane(
                        new TrafficLight(),
                        Direction.NORTH)
        );

        lanes.add(
                new Lane(new TrafficLight(),
                        Direction.SOUTH)
        );

        lanes.add(
                new Lane(
                        new TrafficLight(),
                        Direction.EAST)
        );

        lanes.add(
                new Lane(
                        new TrafficLight(),
                        Direction.WEST)
        );
    }

    public List<Lane> getLanes() {
        return lanes;
    }

    public int getPassedCars() {
        return passedCars;
    }

    public void addCar(String id, Direction start, Direction end) {
        if (id == null || id.isEmpty() || start == null || end == null) {
            System.out.println("Brak jednego lub więcej parametrów!");
            return;
        }
        if (start.equals(end)) {
            System.out.println("Samochód musi mieć różny start i koniec!");
            return;
        }
        lanes.get(start.ordinal()).addCar(
                new Car(start, end, id)
        );
    }

    public boolean step(boolean canChangeLights) {
        List<String> outThisStep = new ArrayList<>();
        boolean result = false;
        if (canChangeLights) {
            State next = MCTS.findNextMove(asState());
            result = updateLights(next);
        }

        List<Car> contenders = new ArrayList<>();

        totalLifetime = 0;
        totalCars = 0;

        for (Lane lane : lanes) {
            totalLifetime += lane.getTotalLifeTime();
            totalCars += lane.getCars().size();
            if (lane.getLightColor() == LightColor.RED) {
                continue;
            }
            Car car = lane.getTopCar();
            if (car != null) {
                contenders.add(car);
            }
        }


        List<Car> allowedToDrive = new ArrayList<>();
        List<Trajectory> usedPaths = new ArrayList<>();

        for (Car car : contenders) {
            Trajectory trajectory = new Trajectory(car.getStart(), car.getEnd());

            boolean conflict = false;
            for (Trajectory used : usedPaths) {
                if (trajectory.conflictsWith(used)) {
                    conflict = true;
                    break;
                }
            }

            if (!conflict) {
                allowedToDrive.add(car);
                usedPaths.add(trajectory);
            }
        }

        avgLifeTime = (totalLifetime + passedLifetime) / (totalCars + passedCars);

        for (Car car : allowedToDrive) {
            car.drive();
            outThisStep.add(car.getId());
            passedCars++;
            passedLifetime += car.getLifeTime();
        }

        for (Lane lane : lanes) {
            lane.removeGone();
            lane.updateLifeTimes();
        }

        carsOut.add(outThisStep);

        return result;
    }

    public double getAvgLifeTime() {
        return avgLifeTime;
    }

    public List<List<String>> getCarsOut() {
        return carsOut;
    }

    public State asState() {
        List<Direction> greenLights = new ArrayList<>();
        for (Lane lane : lanes) {
            if (lane.getLightColor() == LightColor.GREEN) {
                greenLights.add(lane.getPosition());
            }
        }
        return new State(lanes.get(0).getNumCars(), lanes.get(1).getNumCars(), lanes.get(2).getNumCars(), lanes.get(3).getNumCars(), greenLights);
    }

    public boolean updateLights(State state) {
        boolean result = false;
        for (Lane lane : lanes) {
            if (lane.getLightColor() == LightColor.GREEN && !state.greenLights().contains(lane.getPosition())) {
                result = true;
            }
            if (state.greenLights().contains(lane.getPosition())) {
                lane.setLightColor(LightColor.GREEN);
            }
            else {
                lane.setLightColor(LightColor.RED);
            }
        }
        return result;
    }
}
