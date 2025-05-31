package av.staz;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Map;

public class SkrzyzowanieApp extends Application {
    static int NEW_CARS_DEFAULT = 2;
    static int FREQUENCY_DEFAULT = 10;

    @Override
    public void start(Stage primaryStage) throws IOException, NoSuchFieldException, InvalidKeyException {
        Parameters params = getParameters();
        Map<String, String> args = params.getNamed();

        int newCars = NEW_CARS_DEFAULT;
        int frequency = FREQUENCY_DEFAULT;

        if (args.containsKey("NEW_CARS")) {
            newCars = maxCarNumCheck(args);
        }

        if (args.containsKey("FREQUENCY")) {
            frequency = maxLightFrequencyCheck(args);
        }


        HBox root = new HBox();
        Canvas canvas = new Canvas(600, 600);
        Label stats = new Label("Statystyki:");
        Slider slider = new Slider(0, 100, 1);
        VBox sidebar = new VBox(stats, slider);
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: white; -fx-padding: 10;");
        root.getChildren().addAll(new Pane(canvas), sidebar);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Symulacja Skrzyżowania");
        primaryStage.setScene(scene);
        primaryStage.show();

        Intersection intersection = new Intersection();
        Simulation simulation = new Simulation(intersection, frequency, newCars);
        IntersectionView view = new IntersectionView(canvas, stats, slider);
        IntersectionController controller = new IntersectionController(simulation, view);

        if (args.containsKey("in")) {
            controller.runJsonCommands(args);
        } else {
            controller.startSimulationLoop();
        }
    }

    public static int maxCarNumCheck(Map<String, String> args) {
        int NEW_CARS = NEW_CARS_DEFAULT;
        try {
            NEW_CARS = Integer.parseInt(args.get("NEW_CARS"));
        } catch (NumberFormatException e) {
            System.err.println("Błąd: wartość 'NEW_CARS' musi być dodatnią liczbą całkowitą. Otrzymano: " + args.get("NEW_CARS") + "\n" + "Kontynuuję z domyślną wartością (" + NEW_CARS_DEFAULT + ")");
        }

        if (NEW_CARS <= 0) {
            System.err.println("Błąd: wartość 'NEW_CARS' musi być dodatnią liczbą całkowitą. Otrzymano: " + args.get("NEW_CARS") + "\n" + "Kontynuuję z domyślną wartością (" + NEW_CARS_DEFAULT + ")");
            NEW_CARS = NEW_CARS_DEFAULT;
        }

        return NEW_CARS;
    }

    public static int maxLightFrequencyCheck(Map<String, String> args) {
        int FREQUENCY = FREQUENCY_DEFAULT;
        try {
            FREQUENCY = Integer.parseInt(args.get("FREQUENCY"));
        } catch (NumberFormatException e) {
            System.err.println("Błąd: wartość 'FREQUENCY' musi być dodatnią liczbą całkowitą. Otrzymano: " + args.get("FREQUENCY") + "\n" + "Kontynuuję z domyślną wartością (" + FREQUENCY_DEFAULT + ")");
        }

        if (FREQUENCY <= 0) {
            System.err.println("Błąd: wartość 'FREQUENCY' musi być dodatnią liczbą całkowitą. Otrzymano: " + args.get("FREQUENCY") + "\n" + "Kontynuuję z domyślną wartością (" + FREQUENCY_DEFAULT + ")");
            FREQUENCY = FREQUENCY_DEFAULT;
        }

        return FREQUENCY;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
