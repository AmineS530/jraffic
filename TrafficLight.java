import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Arrays;

public class TrafficLight {
    public String state;
    public String nextState;
    public double timer;
    public boolean clearing;

    private static final String[] LIGHTS = { "down", "left", "up", "right" };

    public TrafficLight() {
        this.state = "down";
        this.nextState = "left";
        this.timer = 1.0f;
        this.clearing = false;
    }

    public void updateWithCongestion(double dt, int upCount, int downCount, int leftCount, int rightCount,
            int capacity) {
        if (this.timer > 0.0f) {
            this.timer -= dt;
        }

        if (this.clearing) {
            if (this.timer <= 0.0f) {
                this.state = this.nextState;
                this.clearing = false;

                int count = 0;
                switch (this.state) {
                    case "up":
                        count = upCount;
                        break;
                    case "down":
                        count = downCount;
                        break;
                    case "left":
                        count = leftCount;
                        break;
                    case "right":
                        count = rightCount;
                        break;
                }

                double ratio = (double) count / capacity;

                if (ratio > 0.4f) {
                    this.timer = 2.0f;
                } else if (count > 0) {
                    this.timer = 1.0f;
                } else {
                    this.timer = 0.5f;
                }
            }
        } else {
            if (this.timer <= 0.0f) {
                this.nextState = calculatePriority(upCount, downCount, leftCount, rightCount);
                this.state = "ALL_RED";
                this.clearing = true;
                this.timer = 0.5f;
            }
        }
    }

    private String calculatePriority(int up, int down, int left, int right) {
        int[] counts = { down, left, up, right };
        int currentIdx = Arrays.asList(LIGHTS).indexOf(this.state);
        if (currentIdx == -1)
            currentIdx = 0;

        int[] filteredCounts = new int[3];
        int idx = 0;
        for (int i = 0; i < counts.length; i++) {
            if (i != currentIdx) {
                filteredCounts[idx++] = counts[i];
            }
        }

        Arrays.sort(filteredCounts);
        int maxCount = filteredCounts[2];

        int nextIdx = 0;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] == maxCount && i != currentIdx) {
                nextIdx = i;
                break;
            }
        }
        return LIGHTS[nextIdx];
    }

    public void drawLights(Graphics2D g, int screenWidth, int screenHeight) {
        double cx = screenWidth / 2.0f;
        double cy = screenHeight / 2.0f;
        double gap = 60.0f;
        int r = 16; // diameter in java rather than radius

        Color topLeftColor = state.equals("down") ? Color.GREEN : Color.RED;
        Color topRightColor = state.equals("left") ? Color.GREEN : Color.RED;
        Color bottomLeftColor = state.equals("right") ? Color.GREEN : Color.RED;
        Color bottomRightColor = state.equals("up") ? Color.GREEN : Color.RED;

        g.setColor(topLeftColor);
        g.fillOval((int) (cx - gap - 15 - r / 2), (int) (cy - gap - 15 - r / 2), r, r);

        g.setColor(topRightColor);
        g.fillOval((int) (cx + gap + 15 - r / 2), (int) (cy - gap - 15 - r / 2), r, r);

        g.setColor(bottomLeftColor);
        g.fillOval((int) (cx - gap - 15 - r / 2), (int) (cy + gap + 15 - r / 2), r, r);

        g.setColor(bottomRightColor);
        g.fillOval((int) (cx + gap + 15 - r / 2), (int) (cy + gap + 15 - r / 2), r, r);

        // Debug Text
        int marginX = 10;
        int marginY = 10;
        String statusText = clearing ? "SWITCHING..." : state;
        Color statusColor = clearing ? Color.YELLOW : Color.GREEN;

        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        g.drawString(String.format("Timer: %.1fs", timer), marginX, marginY + 20);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(statusColor);
        g.drawString("Active: " + statusText.toUpperCase(), marginX, marginY + 50);
    }
}