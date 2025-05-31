package av.staz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.AnimationTimer;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Map;
import java.util.Objects;

public class IntersectionController {
    private final Simulation simulation;
    private final IntersectionView view;

    public IntersectionController(Simulation simulation, IntersectionView view) {
        this.simulation = simulation;
        this.view = view;
    }

    public void startSimulationLoop() {
        new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000 / (view.getSimulationSpeed() + 1e-6)) {
                    simulation.step();
                    view.draw(simulation);
                    view.updateStats(simulation);
                    lastUpdate = now;
                } else {
                    view.updateStats(simulation);
                }
            }
        }.start();
    }

    public void runJsonCommands(Map<String, String> args) throws NoSuchFieldException, IOException, InvalidKeyException {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(args.get("in"));
        if (!jsonFile.exists()) {
            throw new NoSuchFieldException("Błąd: plik wejściowy '" + args.get("in") + "' nie istnieje.");
        }
        JsonNode jsonRoot;
        try {
            jsonRoot = mapper.readTree(jsonFile);
        } catch (IOException e) {
            throw new IOException("Błąd: nie udało się wczytać pliku JSON: " + e.getMessage());
        }
        JsonNode commands = jsonRoot.get("commands");
        if (commands == null) {
            throw new InvalidKeyException("Błąd: brak 'commands' wewnątrz pliku json!");
        }

        new AnimationTimer() {
            private long lastUpdate = 0;
            int idx = 0;

            @Override
            public void handle(long now) {
                if (idx >= commands.size()) {
                    if (args.containsKey("out")) {
                        try {
                            simulation.saveToJson(args.get("out"));
                        } catch (IOException e) {
                            System.err.println("Błąd zapisu JSON: " + e.getMessage());
                        }
                    }
                    stop();
                    return;
                }
                if (now - lastUpdate >= 1_000_000_000 / (view.getSimulationSpeed() + 1e-6)) {
                    runJsonCommand(commands.get(idx++), idx);
                    lastUpdate = now;
                } else {
                    view.updateStats(simulation);
                }
            }
        }.start();
    }

    public void runJsonCommand(JsonNode command, int idx) {
        JsonNode commandName = command.get("type");
        if (commandName == null) {
            System.err.println("Błąd: błędny typ operacji w komendzie: " + idx);
            return;
        }
        String type = command.get("type").asText();
        if (Objects.equals(type, "")) {
            System.err.println("Błąd: brak typu operacji w komendzie: " + idx);
            return;
        }
        switch (type) {
            case "addVehicle" -> {
                JsonNode vehicleName = command.get("vehicleId");
                if (vehicleName == null) {
                    System.err.println("Błąd: brak vehicleId w komendzie: " + idx);
                    return;
                }
                JsonNode startName = command.get("startRoad");
                if (startName == null) {
                    System.err.println("Błąd: brak startRoad w komendzie: " + idx);
                    return;
                }
                JsonNode endName = command.get("endRoad");
                if (endName == null) {
                    System.err.println("Błąd: brak endRoad w komendzie: " + idx);
                    return;
                }
                simulation.addCarJson(
                        vehicleName.asText(),
                        Direction.fromString(startName.asText()),
                        Direction.fromString(endName.asText()));
            }
            case "step" -> simulation.stepJson();
            default -> System.err.println("Błąd: błędny typ operacji w komendzie: " + idx);
        }
        view.draw(simulation);
        view.updateStats(simulation);
    }

}
