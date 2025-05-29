package av.staz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.AnimationTimer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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

    public void runJsonCommands(Map<String, String> args) {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(args.get("in"));
        if (!jsonFile.exists()) {
            System.err.println("Błąd: plik wejściowy '" + args.get("in") + "' nie istnieje.");
            System.exit(1);
        }
        JsonNode jsonRoot;
        try {
            jsonRoot = mapper.readTree(jsonFile);
        } catch (IOException e) {
            System.err.println("Błąd: nie udało się wczytać pliku JSON: " + e.getMessage());
            System.exit(1);
            return;
        }
        JsonNode commands;
        try {
            commands = jsonRoot.get("commands");
        } catch (Exception e) {
            System.err.println("Błąd: nie znaleziono 'commands' wewnątrz pliku JSON: " + e.getMessage());
            System.exit(1);
            return;
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
                    JsonNode command = commands.get(idx++);
                    String type = "";
                    try {
                        type = command.get("type").asText();
                    } catch (Exception e) {
                        System.err.println("Błąd: brak typu operacji: " + e.getMessage());
                    }
                    switch (type) {
                        case "addVehicle" -> {
                            simulation.addCarJson(
                                    command.get("vehicleId").asText(),
                                    Direction.fromString(command.get("startRoad").asText()),
                                    Direction.fromString(command.get("endRoad").asText()));
                        }
                        case "step" -> simulation.stepJson();
                        default -> System.err.println("Błąd: błędny typ operacji!");
                    }

                    view.draw(simulation);
                    view.updateStats(simulation);
                    lastUpdate = now;
                } else {
                    view.updateStats(simulation);
                }
            }
        }.start();
    }
}
