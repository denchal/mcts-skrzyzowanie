package av.staz;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

import java.util.List;

public class IntersectionView {
    private final GraphicsContext gc;
    private final Label statLabel;
    private final Slider speedSlider;

    public IntersectionView(Canvas canvas, Label statLabel, Slider speedSlider) {
        this.gc = canvas.getGraphicsContext2D();
        this.statLabel = statLabel;
        this.speedSlider = speedSlider;
    }

    public void draw(Simulation simulation) {
        gc.clearRect(0, 0, 600, 600);

        double laneWidth = 50;
        double carHeight = 30;
        double carWidth = 50;
        double spacing = 5;

        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, 600, 600);

        gc.setFill(Color.DARKGRAY);
        gc.fillRect(275, 0, laneWidth, 300);
        gc.fillRect(275, 300, laneWidth, 300);
        gc.fillRect(300, 275, 300, laneWidth);
        gc.fillRect(0, 275, 300, laneWidth);

        List<Lane> lanes = simulation.getIntersection().getLanes();
        for (Lane lane : lanes) {
            Direction dir = lane.getPosition();
            List<Car> cars = lane.getCars();

            for (int i = 0; i < cars.size(); i++) {
                Car car = cars.get(i);
                double x = 0, y = 0;

                switch (dir) {
                    case NORTH -> {
                        x = 250;
                        y = 250 - (i + 1) * (carHeight + spacing);
                    }
                    case SOUTH -> {
                        x = 300;
                        y = 350 + i * (carHeight + spacing);
                    }
                    case EAST -> {
                        x = 350 + i * (carWidth + spacing);
                        y = 250;
                    }
                    case WEST -> {
                        x = 250 - (i + 1) * (carWidth + spacing);
                        y = 300;
                    }
                }

                gc.setFill(Color.WHITE);
                gc.fillRect(x, y, carWidth, carHeight);
                gc.setFill(Color.BLACK);
                gc.fillText(car.getEnd().toString(), x + 5, y + 20);
            }
        }

        for (Lane lane : lanes) {
            Direction position = lane.getPosition();
            LightColor color = lane.getLightColor();
            gc.setFill(color == LightColor.RED ? Color.RED : Color.GREEN);
            switch (position) {
                case NORTH -> gc.fillOval(260, 260, 15, 15);
                case SOUTH -> gc.fillOval(325, 325, 15, 15);
                case EAST -> gc.fillOval(325, 260, 15, 15);
                case WEST -> gc.fillOval(260, 325, 15, 15);
            }
        }
    }

    public void updateStats(Simulation simulation) {
        statLabel.setText("Statystyki: \n"
                + "Średni czas oczekiwania: \n"
                + simulation.averageWaitTime + "\n"
                + "Liczby aut: \n"
                + "Północ - " + simulation.getIntersection().getLanes().get(0).getNumCars() + "\n"
                + "Południe - " + simulation.getIntersection().getLanes().get(1).getNumCars() + "\n"
                + "Wschód - " + simulation.getIntersection().getLanes().get(2).getNumCars() + "\n"
                + "Zachód - " + simulation.getIntersection().getLanes().get(3).getNumCars() + "\n"
                + "Łącznie przejechało aut - " + simulation.getIntersection().getPassedCars() + "\n"
                + "\n"
                + "Prędkość symulacji: " + String.format("%.2f", speedSlider.getValue()) + "\n");
    }

    public double getSimulationSpeed() {
        return speedSlider.getValue();
    }
}
