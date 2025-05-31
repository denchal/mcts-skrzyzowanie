

import av.staz.Direction;
import av.staz.Trajectory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrajectoryTest {
    @Test
    public void testLeftRight() {
        Trajectory t1 = new Trajectory(Direction.SOUTH, Direction.WEST);
        Trajectory t2 = new Trajectory(Direction.NORTH, Direction.WEST);
        assertFalse(t1.conflictsWith(t2));
    }

    @Test
    public void testRightLeft() {
        Trajectory t1 = new Trajectory(Direction.SOUTH, Direction.EAST);
        Trajectory t2 = new Trajectory(Direction.NORTH, Direction.EAST);
        assertFalse(t1.conflictsWith(t2));
    }

    @Test
    public void testRightRight() {
        Trajectory t1 = new Trajectory(Direction.SOUTH, Direction.EAST);
        Trajectory t2 = new Trajectory(Direction.NORTH, Direction.WEST);
        assertFalse(t1.conflictsWith(t2));
    }

    @Test
    public void testLeftLeft() {
        Trajectory t1 = new Trajectory(Direction.SOUTH, Direction.WEST);
        Trajectory t2 = new Trajectory(Direction.NORTH, Direction.EAST);
        assertTrue(t1.conflictsWith(t2));
    }

    @Test
    public void testLeftStraight() {
        Trajectory t1 = new Trajectory(Direction.SOUTH, Direction.WEST);
        Trajectory t2 = new Trajectory(Direction.NORTH, Direction.SOUTH);
        assertTrue(t1.conflictsWith(t2));
    }

    @Test
    public void testRightStraight() {
        Trajectory t1 = new Trajectory(Direction.SOUTH, Direction.EAST);
        Trajectory t2 = new Trajectory(Direction.NORTH, Direction.SOUTH);
        assertFalse(t1.conflictsWith(t2));
    }
}
