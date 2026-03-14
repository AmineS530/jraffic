package Jraffic.models;

import Jraffic.helpers.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TrafficLight {

    public Direction state;
    public Direction nextState;
    public long timerNs;
    public boolean clearing;

    private static final long SEC  = 1_000_000_000L;
    private static final long HALF = 500_000_000L;

    private static final Direction[] CYCLE = {
        Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST
    };

    public TrafficLight() {
        this.state     = Direction.SOUTH;
        this.nextState = Direction.WEST;
        this.timerNs   = SEC;
        this.clearing  = false;
    }

    public void update(long frameNs, int northCount, int southCount, int eastCount, int westCount, int capacity) {
        if (timerNs > 0) timerNs -= frameNs;

        if (clearing) {
            if (timerNs <= 0) {
                state    = nextState;
                clearing = false;

                int count = switch (state) {
                    case NORTH -> northCount;
                    case SOUTH -> southCount;
                    case EAST  -> eastCount;
                    case WEST  -> westCount;
                };

                double ratio = (double) count / capacity;
                if      (ratio > 0.4) timerNs = 2 * SEC;
                else if (count > 0)   timerNs = SEC;
                else                  timerNs = HALF;
            }
        } else {
            if (timerNs <= 0) {
                nextState = calcPriority(northCount, southCount, eastCount, westCount);
                state     = null;  // ALL_RED
                clearing  = true;
                timerNs   = HALF;
            }
        }
    }

    public boolean isGreen(Direction dir) {
        return state == dir;
    }

    public boolean isAllRed() {
        return state == null;
    }

    private Direction calcPriority(int n, int s, int e, int w) {
        int[] counts = { s, w, n, e };
        int currentIdx = indexOfCycle(state);
        if (currentIdx == -1) currentIdx = 0;

        int bestIdx = -1, bestCount = -1;
        for (int i = 0; i < counts.length; i++) {
            if (i == currentIdx) continue;
            if (counts[i] > bestCount) {
                bestCount = counts[i];
                bestIdx   = i;
            }
        }
        return CYCLE[bestIdx == -1 ? (currentIdx + 1) % 4 : bestIdx];
    }

    private int indexOfCycle(Direction d) {
        for (int i = 0; i < CYCLE.length; i++)
            if (CYCLE[i] == d) return i;
        return -1;
    }

    public void draw(GraphicsContext gc, double centerX, double centerY, double roadWidth) {
        double gap = roadWidth / 2 + 12;
        double r   = 10;

        drawLight(gc, centerX - gap, centerY - gap, r, state == Direction.SOUTH);
        drawLight(gc, centerX + gap, centerY - gap, r, state == Direction.WEST);
        drawLight(gc, centerX - gap, centerY + gap, r, state == Direction.EAST);
        drawLight(gc, centerX + gap, centerY + gap, r, state == Direction.NORTH);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        String label = isAllRed() ? "ALL RED" : state.toString();
        gc.fillText(String.format("Light: %-8s  %.1fs", label, timerNs / 1_000_000_000.0), 10, 36);
    }

    private void drawLight(GraphicsContext gc, double x, double y, double r, boolean green) {
        gc.setFill(Color.web("#222222"));
        gc.fillOval(x - r - 3, y - r - 3, (r + 3) * 2, (r + 3) * 2);
        gc.setFill(green ? Color.LIMEGREEN : Color.web("#8b0000"));
        gc.fillOval(x - r, y - r, r * 2, r * 2);
    }
}