package Jraffic.graphics;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import Jraffic.helpers.Direction;
import Jraffic.helpers.Route;
import Jraffic.models.Sprites;
import Jraffic.models.Vehicle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CanvasPanel {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final double centerX;
    private final double centerY;

    private static final double ROAD_WIDTH = Sprites.FRAME_W * 1.5;
    private static final double LANE_WIDTH = ROAD_WIDTH / 2;
    private static final double SPAWN_MARGIN = LANE_WIDTH / 2;

    private final Image background;

    private int fps = 0;
    private int frameCount = 0;
    private long lastFpsTime = 0;
    private static final Font FPS_FONT = Font.font("Monospaced", 14);

    private final Map<Route, Sprites> spriteMap = new EnumMap<>(Route.class);
    private final List<Vehicle> vehicles = new ArrayList<>();

    public CanvasPanel(double width, double height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        centerX = width / 2;
        centerY = height / 2;

        background = new Image(getClass().getResourceAsStream("/assets/background.png"));

        spriteMap.put(Route.STRAIGHT, new Sprites("/assets/sprites/car_00.png"));
        spriteMap.put(Route.LEFT, new Sprites("/assets/sprites/car_01.png"));
        spriteMap.put(Route.RIGHT, new Sprites("/assets/sprites/car_02.png"));
    }

    public void render(long now) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawRoad();
        for (Vehicle v : vehicles)
            drawVehicle(v);
        drawFps(now);
    }

    private void drawRoad() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.drawImage(background, 0, 0, w, h);

        // Horizontal + vertical roads
        gc.setFill(Color.web("#1a1a1a"));
        gc.fillRect(0, centerY - ROAD_WIDTH / 2, w, ROAD_WIDTH);
        gc.fillRect(centerX - ROAD_WIDTH / 2, 0, ROAD_WIDTH, h);

        // Intersection box slightly lighter
        gc.setFill(Color.web("#2a2a2a"));
        gc.fillRect(centerX - ROAD_WIDTH / 2, centerY - ROAD_WIDTH / 2, ROAD_WIDTH, ROAD_WIDTH);

        // Dashed center lane dividers
        gc.setStroke(Color.web("#e8e800"));
        gc.setLineWidth(1.5);
        gc.setLineDashes(20, 15);
        gc.strokeLine(0, centerY, centerX - ROAD_WIDTH / 2, centerY);
        gc.strokeLine(centerX + ROAD_WIDTH / 2, centerY, w, centerY);
        gc.strokeLine(centerX, 0, centerX, centerY - ROAD_WIDTH / 2);
        gc.strokeLine(centerX, centerY + ROAD_WIDTH / 2, centerX, h);
        gc.setLineDashes(null);

        // Stop lines
        // gc.setStroke(Color.WHITE);
        // gc.setLineWidth(3);
        // gc.strokeLine(centerX, centerY + ROAD_WIDTH / 2, centerX + ROAD_WIDTH / 2,
        // centerY + ROAD_WIDTH / 2);
        // gc.strokeLine(centerX - ROAD_WIDTH / 2, centerY - ROAD_WIDTH / 2, centerX,
        // centerY - ROAD_WIDTH / 2);
        // gc.strokeLine(centerX - ROAD_WIDTH / 2, centerY, centerX - ROAD_WIDTH / 2,
        // centerY + ROAD_WIDTH / 2);
        // gc.strokeLine(centerX + ROAD_WIDTH / 2, centerY - ROAD_WIDTH / 2, centerX +
        // ROAD_WIDTH / 2, centerY);
    }

    private void drawVehicle(Vehicle v) {
        Sprites sheet = spriteMap.get(v.getRoute());
        var frame = sheet.getFrame(v.getDirection(), v.getAnimFrame());
        gc.drawImage(
                frame,
                v.getX() - Sprites.FRAME_W / 2.0,
                v.getY() - Sprites.FRAME_H / 2.0,
                Sprites.FRAME_W,
                Sprites.FRAME_H);
    }

    public void spawnVehicle(Direction direction) {
        double x = 0, y = 0;
        double speed = 2.0;

        switch (direction) {
            case NORTH -> {
                x = centerX + LANE_WIDTH / 2;
                y = canvas.getHeight() + SPAWN_MARGIN;
            }
            case SOUTH -> {
                x = centerX - LANE_WIDTH / 2;
                y = -SPAWN_MARGIN;
            }
            case EAST -> {
                x = -SPAWN_MARGIN;
                y = centerY + LANE_WIDTH / 2;
            }
            case WEST -> {
                x = canvas.getWidth() + SPAWN_MARGIN;
                y = centerY - LANE_WIDTH / 2;
            }
        }

        Route route = Route.values()[(int) (Math.random() * Route.values().length)];
        if (isSafeToSpawn(direction)) {
            vehicles.add(new Vehicle(x, y, speed, direction, route));
        }
    }

    private boolean isSafeToSpawn(Direction direction) {
        double minGap = Sprites.FRAME_H + ROAD_WIDTH;

        for (Vehicle v : vehicles) {
            if (v.getDirection() != direction)
                continue;
            double dist = switch (direction) {
                case NORTH -> Math.abs(v.getY() - (canvas.getHeight() + SPAWN_MARGIN));
                case SOUTH -> Math.abs(v.getY() - (-SPAWN_MARGIN));
                case EAST -> Math.abs(v.getX() - (-SPAWN_MARGIN));
                case WEST -> Math.abs(v.getX() - (canvas.getWidth() + SPAWN_MARGIN));
            };
            if (dist < minGap)
                return false;
        }
        return true;
    }

    public void updateVehicles(long now) {
        vehicles.forEach(v -> v.update(now));
        vehicles.removeIf(v -> v.getX() < -200 || v.getX() > canvas.getWidth() + 200 ||
                v.getY() < -200 || v.getY() > canvas.getHeight() + 200);
    }

    private void drawFps(long now) {
        frameCount++;
        if (now - lastFpsTime >= 1_000_000_000L) {
            fps = frameCount;
            frameCount = 0;
            lastFpsTime = now;
        }
        gc.setFill(Color.WHITE);
        gc.setFont(FPS_FONT);
        gc.fillText("FPS: " + fps, 10, 20);
    }

    public Canvas getCanvas() {
        return canvas;
    }
}