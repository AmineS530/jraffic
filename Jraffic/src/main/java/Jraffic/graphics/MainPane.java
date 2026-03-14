package Jraffic.graphics;

import java.util.HashSet;
import java.util.Set;

import Jraffic.helpers.Constants;
import Jraffic.helpers.Direction;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public class MainPane extends Pane {

    private static final double WIDTH = 1440.0;
    private static final double HEIGHT = 900.0;

    private final CanvasPanel canvasPanel;
    private final AnimationTimer gameLoop;
    private final Set<KeyCode> heldKeys = new HashSet<>();

    public MainPane() {
        canvasPanel = new CanvasPanel(WIDTH, HEIGHT);
        getChildren().add(canvasPanel.getCanvas());

        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate < Constants.FRAME_NS)
                    return;
                lastUpdate = now;

                canvasPanel.updateVehicles(now);
                canvasPanel.render(now);
            }
        };
        gameLoop.start();

        // Key input — must request focus so key events are received
        setOnKeyPressed(this::handleKey);
        setOnKeyReleased(e -> heldKeys.remove(e.getCode()));

        // Grab focus once the scene is attached
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null)
                requestFocus();
        });
    }

    private void handleKey(KeyEvent e) {
        if (heldKeys.contains(e.getCode()))
            return;
        heldKeys.add(e.getCode());
        switch (e.getCode()) {
            case UP -> canvasPanel.spawnVehicle(Direction.NORTH);
            case DOWN -> canvasPanel.spawnVehicle(Direction.SOUTH);
            case LEFT -> canvasPanel.spawnVehicle(Direction.WEST);
            case RIGHT -> canvasPanel.spawnVehicle(Direction.EAST);
            case R -> canvasPanel.spawnVehicle(randomDirection());
            case ESCAPE -> System.exit(0);
            default -> {
            }
        }
    }

    private Direction randomDirection() {
        Direction[] dirs = Direction.values();
        return dirs[(int) (Math.random() * dirs.length)];
    }
}