package av.staz;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class SkrzyzowanieApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parameters params = getParameters();
        Map<String, String> args = params.getNamed();

        int MAX_NEW_CARS = 2;
        int MAX_COLOR_CHANGE_FREQUENCY = 10;

        if (args.containsKey("MAX_NEW_CARS")) {
            try {
                MAX_NEW_CARS = Integer.parseInt(args.getOrDefault("MAX_NEW_CARS", "2"));
            } catch (NumberFormatException e) {
                System.err.println("Błąd: wartość 'MAX_NEW_CARS' musi być dodatnią liczbą całkowitą. Otrzymano: " + args.get("MAX_NEW_CARS") + "\n" + "Kontynuuję z domyślną wartością (2)");
            }
        }

        if (args.containsKey("MAX_COLOR_CHANGE_FREQUENCY")) {
            try {
                MAX_COLOR_CHANGE_FREQUENCY = Integer.parseInt(args.getOrDefault("MAX_COLOR_CHANGE_FREQUENCY", "10"));
            } catch (NumberFormatException e) {
                System.err.println("Błąd: wartość 'MAX_COLOR_CHANGE_FREQUENCY' musi być dodatnią liczbą całkowitą. Otrzymano: " + args.get("MAX_COLOR_CHANGE_FREQUENCY") + "\n" + "Kontynuuję z domyślną wartością (10)");
            }
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
        Simulation simulation = new Simulation(intersection, MAX_COLOR_CHANGE_FREQUENCY, MAX_NEW_CARS);
        IntersectionView view = new IntersectionView(canvas, stats, slider);
        IntersectionController controller = new IntersectionController(simulation, view);

        if (args.containsKey("in")) {
            controller.runJsonCommands(args);
        } else {
            controller.startSimulationLoop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
