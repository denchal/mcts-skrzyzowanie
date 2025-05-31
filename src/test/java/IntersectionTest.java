import av.staz.Direction;
import av.staz.Intersection;
import av.staz.State;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IntersectionTest {
    @Test
    public void testIntersection() {
        Intersection intersection = new Intersection();
        intersection.addCar("1", Direction.SOUTH, Direction.NORTH);
        intersection.addCar("2", Direction.SOUTH, Direction.WEST);
        intersection.addCar("3", Direction.SOUTH, Direction.EAST);

        intersection.addCar("4", Direction.NORTH, Direction.SOUTH);
        intersection.addCar("5", Direction.NORTH, Direction.WEST);
        intersection.addCar("6", Direction.NORTH, Direction.EAST);

        intersection.updateLights(new State(0,0,0,0, List.of(Direction.SOUTH, Direction.NORTH)));

        intersection.step(false);
        intersection.step(false);
        intersection.step(false);

        assertEquals(0, intersection.getLanes().get(0).getNumCars());
        assertEquals(0, intersection.getLanes().get(1).getNumCars());

        assertEquals(6, intersection.getPassedCars());

        // Wszystkie auta powinny przejechać w tym ustawieniu w ciągu 3 kroków
    }

    @Test
    public void testLightSwitchByMCTS() {
        Intersection intersection = new Intersection();
        intersection.addCar("1", Direction.SOUTH, Direction.NORTH);
        intersection.addCar("2", Direction.SOUTH, Direction.WEST);
        intersection.addCar("3", Direction.SOUTH, Direction.EAST);

        intersection.addCar("4", Direction.NORTH, Direction.SOUTH);
        intersection.addCar("5", Direction.NORTH, Direction.WEST);
        intersection.addCar("6", Direction.NORTH, Direction.EAST);

        intersection.updateLights(new State(0,0,0,0, List.of(Direction.WEST, Direction.EAST)));

        intersection.step(true);

        assertEquals(intersection.asState().greenLights(), List.of(Direction.NORTH, Direction.SOUTH));

        // Algorytm powinien zmienić swiatła z west/east na north/south
    }

    @Test
    public void testManyCarsFromOneDirection() {
        Intersection intersection = new Intersection();
        for (int i = 0; i < 100; i++) {
            intersection.addCar(String.valueOf(i), Direction.SOUTH, Direction.NORTH);
        }

        intersection.updateLights(new State(0,0,0,0, List.of(Direction.SOUTH)));

        for (int i = 0; i < 50; i++) {
            intersection.step(false);
        }

        assertEquals(50, intersection.getPassedCars());
    }

    @Test
    public void testAddCarWithNullDirection() {
        Intersection intersection = new Intersection();
        assertDoesNotThrow(() -> {
            intersection.addCar("X", null, Direction.NORTH);
            intersection.addCar("Y", Direction.SOUTH, null);
        });
    }

    @Test
    public void testDuplicateCarIds() {
        Intersection intersection = new Intersection();
        intersection.addCar("DUP", Direction.NORTH, Direction.SOUTH);
        intersection.addCar("DUP", Direction.SOUTH, Direction.NORTH);

        int count = 0;
        for (var lane : intersection.getLanes()) {
            count += lane.getCars().stream().filter(c -> c.getId().equals("DUP")).count();
        }
        assertEquals(2, count);
    }

    @Test
    public void testEmptyGreenLightList() {
        Intersection intersection = new Intersection();
        assertDoesNotThrow(() -> {
            intersection.updateLights(new State(0, 0, 0, 0, List.of()));
            intersection.step(false);
        });
    }

    @Test
    public void testNullStateUpdate() {
        Intersection intersection = new Intersection();
        assertThrows(NullPointerException.class, () -> {
            intersection.updateLights(null);
        });
        assertEquals(new ArrayList<Direction>(), intersection.asState().greenLights());
    }

    @Test
    public void testStepWithoutLightState() {
        Intersection intersection = new Intersection();
        intersection.addCar("1", Direction.NORTH, Direction.SOUTH);
        assertDoesNotThrow(() -> {
            intersection.step(false);
        });
    }
}
