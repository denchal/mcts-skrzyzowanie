import av.staz.Car;
import av.staz.Direction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MinorTests {
    // testy pominiejszych rzeczy typu jazda samochodu...

    @Test
    public void testDrive() {
        Car car = new Car(Direction.NORTH, Direction.SOUTH, "1");
        car.drive();
        assertEquals(car.getStart(), car.getEnd());
        assertEquals(true, car.isDone());
        assertEquals(Direction.SOUTH, car.getStart());
    }

}
