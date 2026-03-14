package Jraffic.graphics;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import Jraffic.helpers.Constants;
import Jraffic.helpers.Direction;
import Jraffic.helpers.Route;
import Jraffic.models.Sprites;
import Jraffic.models.TrafficLight;
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
    private final TrafficLight trafficLight = new TrafficLight();

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
        trafficLight.draw(gc, centerX, centerY, ROAD_WIDTH);
        drawFps(now);
    }

    public void updateVehicles(long now) {
        int cap = Math.max(1, (int) (canvas.getHeight() / (LANE_WIDTH + 50)));
        trafficLight.update(
                Constants.FRAME_NS,
                countDir(Direction.NORTH), countDir(Direction.SOUTH),
                countDir(Direction.EAST), countDir(Direction.WEST),
                cap);

        vehicles.forEach(v -> v.update(now, centerX, centerY, LANE_WIDTH));
        vehicles.removeIf(v -> v.getX() < -200 || v.getX() > canvas.getWidth() + 200 ||
                v.getY() < -200 || v.getY() > canvas.getHeight() + 200);
    }

    public void spawnVehicle(Direction direction) {
        double x = 0, y = 0;

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
        if (isSafeToSpawn(direction))
            vehicles.add(new Vehicle(x, y, 2.0, direction, route));
    }

    private boolean isSafeToSpawn(Direction direction) {
        double minGap = Sprites.FRAME_H + 22;
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

    private void drawRoad() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.drawImage(background, 0, 0, w, h);

        gc.setFill(Color.web("#1a1a1a"));
        gc.fillRect(0, centerY - LANE_WIDTH, w, ROAD_WIDTH);
        gc.fillRect(centerX - LANE_WIDTH, 0, ROAD_WIDTH, h);

        gc.setFill(Color.web("#2a2a2a"));
        gc.fillRect(centerX - LANE_WIDTH, centerY - LANE_WIDTH, ROAD_WIDTH, ROAD_WIDTH);

        gc.setStroke(Color.web("#e8e800"));
        gc.setLineWidth(1.5);
        gc.setLineDashes(20, 15);
        gc.strokeLine(0, centerY, centerX - LANE_WIDTH, centerY);
        gc.strokeLine(centerX + LANE_WIDTH, centerY, w, centerY);
        gc.strokeLine(centerX, 0, centerX, centerY - LANE_WIDTH);
        gc.strokeLine(centerX, centerY + LANE_WIDTH, centerX, h);
        gc.setLineDashes(null);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);

        // south
        gc.strokeLine(centerX - LANE_WIDTH, centerY + LANE_WIDTH, centerX + LANE_WIDTH, centerY + LANE_WIDTH);
        // north
        gc.strokeLine(centerX - LANE_WIDTH, centerY - LANE_WIDTH, centerX + LANE_WIDTH, centerY - LANE_WIDTH);
        // west
        gc.strokeLine(centerX - LANE_WIDTH, centerY + LANE_WIDTH, centerX - LANE_WIDTH, centerY - LANE_WIDTH);
        // east
        gc.strokeLine(centerX + LANE_WIDTH, centerY - LANE_WIDTH, centerX + LANE_WIDTH, centerY + LANE_WIDTH);

    }

    private void drawVehicle(Vehicle v) {
        Sprites sheet = spriteMap.get(v.getRoute());
        var frame = sheet.getFrame(v.getDirection(), v.getAnimFrame());
        gc.drawImage(frame,
                v.getX() - Sprites.FRAME_W / 2.0,
                v.getY() - Sprites.FRAME_H / 2.0,
                Sprites.FRAME_W, Sprites.FRAME_H);
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

    boolean yes = false;

    public void stopVehicles() {
        yes = !yes;
        vehicles.forEach(v -> v.setStopped(yes));
    }

    private int countDir(Direction d) {
        int n = 0;
        for (Vehicle v : vehicles)
            if (v.getDirection() == d)
                n++;
        return n;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}