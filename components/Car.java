package components;

import java.awt.Color;

public class Car {
    public String direction;
    public int width = 88;
    public int height = 88;
    public double x, y;
    public Color color;
    public double speed = 225.0;

    public int spriteIndex = 0;
    public int animFrame = 0;
    private double animTimer = 0.0;
    private boolean turned = false;

    private static final double ANIM_INTERVAL = 0.08;
    private static final double BAND_RADIUS = 3.0;

    public Car(String direction, double x, double y, int colorType) {
        this.direction = direction;
        this.x = x;
        this.y = y;
        this.spriteIndex = colorType % 3;
        this.color = colorType == 1 ? Color.YELLOW
                : colorType == 2 ? Color.BLUE
                        : Color.RED;
    }

    public void update(double dt, String state, boolean blocked, int sw, int sh) {
        if (blocked)
            return;
        advanceAnim(dt);

        double cx = sw / 2.0;
        double cy = sh / 2.0;

        switch (direction) {
            case "up" -> updateAxis(dt, state, y, cy, -1, true);
            case "down" -> updateAxis(dt, state, y, cy, +1, true);
            case "left" -> updateAxis(dt, state, x, cx, -1, false);
            case "right" -> updateAxis(dt, state, x, cx, +1, false);
        }
    }

    // ── Core movement ─────────────────────────────────────────────────────────

    private void updateAxis(double dt, String state,
            double pos, double center, int sign,
            boolean vertical) {
        // RED turns at the near band, YELLOW at the far band
        double nearBand = center + sign * (-40.0);
        double farBand = center + sign * (35.0);
        double stopLine = center + sign * (-132.0);
        double clearLine = center + sign * (-100.0);

        if (!turned && inBand(pos, nearBand) && color == Color.RED) {
            snapAndTurn(dt, sign, vertical, nearBand, true);
        } else if (!turned && inBand(pos, farBand) && color == Color.YELLOW) {
            snapAndTurn(dt, sign, vertical, farBand, false);
        } else {
            stopOnRed(dt, state, stopLine, clearLine, sign, vertical);
        }
    }

    private void snapAndTurn(double dt, int sign, boolean vertical,
            double snapPos, boolean towardsPositive) {
        int crossSign = towardsPositive ? sign * -1 : sign;

        if (vertical) {
            y = snapPos;
            x += crossSign * speed * dt;
            direction = towardsPositive ? (sign == -1 ? "right" : "left")
                    : (sign == -1 ? "left" : "right");
        } else {
            x = snapPos;
            y -= crossSign * speed * dt;
            direction = towardsPositive ? (sign == -1 ? "up" : "down")
                    : (sign == -1 ? "down" : "up");
        }
        turned = true;
    }

    /** Moves along main axis, clamping to stopLine when the light is red. */
    private void stopOnRed(double dt, String state,
            double stopLine, double clearLine,
            int sign, boolean vertical) {
        double pos = vertical ? y : x;
        double step = sign * speed * dt;
        boolean approaching = sign == -1 ? pos >= stopLine : pos <= stopLine;
        boolean past = sign == -1 ? pos < clearLine : pos > clearLine;

        double newPos;
        if (state.equals(direction)) {
            newPos = pos + step; // green
        } else if (approaching) {
            newPos = sign == -1 ? Math.max(pos + step, stopLine) // clamp
                    : Math.min(pos + step, stopLine);
        } else if (past) {
            newPos = pos + step; // past — keep going
        } else {
            return; // stopped at line
        }

        if (vertical)
            y = newPos;
        else
            x = newPos;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void advanceAnim(double dt) {
        animTimer += dt;
        if (animTimer >= ANIM_INTERVAL) {
            animTimer -= ANIM_INTERVAL;
            animFrame = (animFrame + 1) % 10_000;
        }
    }

    private boolean inBand(double value, double target) {
        return Math.abs(value - target) <= BAND_RADIUS;
    }
}