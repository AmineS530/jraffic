package Jraffic.models;

import Jraffic.helpers.Constants;
import Jraffic.helpers.Direction;
import Jraffic.helpers.Route;

public class Vehicle {

    private double x, y;
    private final double speed;
    private Direction direction;
    private final Route route;
    private boolean stopped = false;
    private boolean turned = false;

    // Animation state
    private long lastFrameTime = 0;
    private int animFrame = 0;

    public Vehicle(double x, double y, double speed, Direction direction, Route route) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.direction = direction;
        this.route = route;
    }

    public void update(long now, double cx, double cy, double laneWidth) {
        if (!stopped) {
            move();
        }
        if (!turned)
            checkAndTurn(cx, cy, laneWidth / 2);

        if (now - lastFrameTime >= Constants.SPRITE_FRAME_NS) {
            lastFrameTime = now;
            if (++animFrame > 10_000)
                animFrame = 0;
        }
    }

    private void move() {
        switch (direction) {
            case NORTH -> y -= speed;
            case SOUTH -> y += speed;
            case EAST -> x += speed;
            case WEST -> x -= speed;
        }
    }

    private void checkAndTurn(double cx, double cy, double half) {
        if (route == Route.STRAIGHT)
            return;

        boolean atCenter = switch (direction) {
            case NORTH -> y <= cy;
            case SOUTH -> y >= cy;
            case EAST -> x >= cx;
            case WEST -> x <= cx;
        };

        if (!atCenter)
            return;

        direction = turnDirection();
        turned = true;

        switch (direction) {
            case NORTH, SOUTH -> x = (direction == Direction.NORTH) ? cx + half : cx - half;
            case EAST, WEST -> y = (direction == Direction.EAST) ? cy + half : cy - half;
        }
    }

    private Direction turnDirection() {
        return switch (route) {
            case LEFT -> switch (direction) {
                case NORTH -> Direction.WEST;
                case SOUTH -> Direction.EAST;
                case EAST -> Direction.NORTH;
                case WEST -> Direction.SOUTH;
            };
            case RIGHT -> switch (direction) {
                case NORTH -> Direction.EAST;
                case SOUTH -> Direction.WEST;
                case EAST -> Direction.SOUTH;
                case WEST -> Direction.NORTH;
            };
            case STRAIGHT -> direction;
        };
    }

    public void setDirection(Direction d) {
        this.direction = d;
    }

    public void setStopped(boolean s) {
        this.stopped = s;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Route getRoute() {
        return route;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isStopped() {
        return stopped;
    }

    public int getAnimFrame() {
        return animFrame;
    }
}
