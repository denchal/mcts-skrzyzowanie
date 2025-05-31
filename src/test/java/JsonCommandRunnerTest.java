import av.staz.Intersection;
import av.staz.IntersectionController;
import av.staz.IntersectionView;
import av.staz.Simulation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

public class JsonCommandRunnerTest {

    @Test
    public void testMissingInputFile() {
        IntersectionController runner = new IntersectionController(
                new Simulation(
                        new Intersection(), 10, 2),
                new ViewMock()
        );
        Map<String, String> args = new HashMap<>();
        args.put("in", "nonexistent.json");
        Exception exception = assertThrows(NoSuchFieldException.class, () -> runner.runJsonCommands(args));
        assertTrue(exception.getMessage().contains("Błąd: plik wejściowy"));
    }

    @Test
    public void testMalformedJson() {
        IntersectionController runner = new IntersectionController(
                new Simulation(
                        new Intersection(), 10, 2),
                new ViewMock()
        );
        Map<String, String> args = new HashMap<>();
        args.put("in", getPath("malformed.json"));
        Exception exception = assertThrows(IOException.class, () -> runner.runJsonCommands(args));
        assertTrue(exception.getMessage().contains("nie udało się wczytać"));
    }

    @Test
    public void testMissingCommandsField() {
        IntersectionController runner = new IntersectionController(
                new Simulation(
                        new Intersection(), 10, 2),
                new ViewMock()
        );
        Map<String, String> args = new HashMap<>();
        args.put("in", getPath("no_commands.json"));
        Exception exception = assertThrows(InvalidKeyException.class, () -> runner.runJsonCommands(args));
        assertTrue(exception.getMessage().contains("brak 'commands'"));
    }

    @Test
    public void testMissingTypeField() throws IOException {
        IntersectionController runner = new IntersectionController(
                new Simulation(
                        new Intersection(), 10, 2),
                new ViewMock()
        );
        Map<String, String> args = new HashMap<>();
        args.put("in", getPath("missing_type.json"));
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(args.get("in"));
        JsonNode jsonRoot = mapper.readTree(jsonFile);
        JsonNode commands = jsonRoot.get("commands");

        JsonNode command = commands.get(0);
        assertDoesNotThrow(() -> runner.runJsonCommand(command, 0));
    }

    @Test
    public void testInvalidCommandType() throws IOException {
        IntersectionController runner = new IntersectionController(
                new Simulation(
                        new Intersection(), 10, 2),
                new ViewMock()
        );
        Map<String, String> args = new HashMap<>();
        args.put("in", getPath("invalid_type.json"));

        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(args.get("in"));
        JsonNode jsonRoot = mapper.readTree(jsonFile);
        JsonNode commands = jsonRoot.get("commands");

        JsonNode command = commands.get(0);
        assertDoesNotThrow(() -> runner.runJsonCommand(command, 0));
    }

    private String getPath(String filename) {
        return new File("src/test/resources/" + filename).getAbsolutePath();
    }

    class ViewMock extends IntersectionView {
        public ViewMock() {
            super(new Canvas(), null, null);
        }

        @Override
        public void draw(Simulation sim) {
        }

        @Override
        public void updateStats(Simulation sim) {
        }

        @Override
        public double getSimulationSpeed() {
            return 1.0;
        }
    }
}




