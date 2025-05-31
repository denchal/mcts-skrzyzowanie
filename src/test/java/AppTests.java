import av.staz.SkrzyzowanieApp;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTests {
    @Test
    public void testMaxCars() {
        Map<String, String> args = new HashMap<>();
        args.put("NEW_CARS", "-2");
        int x = SkrzyzowanieApp.maxCarNumCheck(args);
        assertEquals(2, x);

        args = new HashMap<>();
        args.put("NEW_CARS", "5");
        x = SkrzyzowanieApp.maxCarNumCheck(args);
        assertEquals(5, x);
    }

    @Test
    public void testMaxFrequency() {
        Map<String, String> args = new HashMap<>();
        args.put("FREQUENCY", "-2");
        int x = SkrzyzowanieApp.maxLightFrequencyCheck(args);
        assertEquals(10, x);

        args = new HashMap<>();
        args.put("FREQUENCY", "5");
        x = SkrzyzowanieApp.maxLightFrequencyCheck(args);
        assertEquals(5, x);
    }
}
