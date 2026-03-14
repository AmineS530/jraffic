package Jraffic.models;

import Jraffic.helpers.Constants;
import Jraffic.helpers.Direction;
import Jraffic.helpers.Route;

public class Vehicle {

    private double x, y;
    private final double speed;
    private Direction direction;
    private final Route route;
    private boolean stopped;

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

    public void update(long now) {
        if (stopped)
            return;

        // Move
        switch (direction) {
            case NORTH -> y -= speed;
            case SOUTH -> y += speed;
            case EAST -> x += speed;
            case WEST -> x -= speed;
        }

        if (now - lastFrameTime >= Constants.SPRITE_FRAME_NS) {
            lastFrameTime = now;
            animFrame++;
            if (animFrame > 10_000)
                animFrame = 0;
        }
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
