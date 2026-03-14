import java.awt.Color;

public class Car {
    public String direction;
    public int width;
    public int height;
    public double x;
    public double y;
    public Color color;
    public double speed;

    public Car(String direction, int width, int height, double x, double y, int colorType) {
        this.direction = direction;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.speed = 400.0;

        if (colorType == 1) {
            this.color = Color.YELLOW;
        } else if (colorType == 2) {
            this.color = Color.BLUE;
        } else {
            this.color = Color.RED;
        }
    }

    public void update(double dt, String state, boolean blocked, int screenWidth, int screenHeight) {
        if (blocked) {
            return;
        }

        switch (direction) {
            case "up":
                if (y >= screenHeight / 2.0 + 12.0 && y <= screenHeight / 2.0 + 18.0 && color.equals(Color.RED)) {
                    y = screenHeight / 2.0 + 15.0;
                    x += speed * dt;
                } else if (y >= screenHeight / 2.0 - 48.0 && y <= screenHeight / 2.0 - 43.0
                        && color.equals(Color.YELLOW)) {
                    y = screenHeight / 2.0 - 45.0;
                    x -= speed * dt;
                } else {
                    double stopLine = screenHeight / 2.0 + 65.0;
                    double clearLine = screenHeight / 2.0 + 60.0;

                    if (state.equals(direction)) {
                        y -= speed * dt;
                    } else if (y >= stopLine) {
                        y = Math.max(y - speed * dt, stopLine);
                    } else if (y < clearLine) {
                        y -= speed * dt;
                    }
                }
                break;
            case "down":
                if (y >= screenHeight / 2.0 - 48.0 && y <= screenHeight / 2.0 - 43.0 && color.equals(Color.RED)) {
                    y = screenHeight / 2.0 - 45.0;
                    x -= speed * dt;
                } else if (y >= screenHeight / 2.0 + 12.0 && y <= screenHeight / 2.0 + 18.0
                        && color.equals(Color.YELLOW)) {
                    y = screenHeight / 2.0 + 15.0;
                    x += speed * dt;
                } else {
                    double stopLine = screenHeight / 2.0 - 95.0;
                    double clearLine = screenHeight / 2.0 - 90.0;

                    if (state.equals(direction)) {
                        y += speed * dt;
                    } else if (y <= stopLine) {
                        y = Math.min(y + speed * dt, stopLine);
                    } else if (y > clearLine) {
                        y += speed * dt;
                    }
                }
                break;
            case "left":
                if (x >= screenWidth / 2.0 + 12.0 && x <= screenWidth / 2.0 + 18.0 && color.equals(Color.RED)) {
                    x = screenWidth / 2.0 + 15.0;
                    y -= speed * dt;
                } else if (x >= screenWidth / 2.0 - 48.0 && x <= screenWidth / 2.0 - 42.0
                        && color.equals(Color.YELLOW)) {
                    x = screenWidth / 2.0 - 45.0;
                    y += speed * dt;
                } else {
                    double stopLine = screenWidth / 2.0 + 65.0;
                    double clearLine = screenWidth / 2.0 + 60.0;

                    if (state.equals(direction)) {
                        x -= speed * dt;
                    } else if (x >= stopLine) {
                        x = Math.max(x - speed * dt, stopLine);
                    } else if (x < clearLine) {
                        x -= speed * dt;
                    }
                }
                break;
            case "right":
                if (x >= screenWidth / 2.0 - 48.0 && x <= screenWidth / 2.0 - 42.0 && color.equals(Color.RED)) {
                    x = screenWidth / 2.0 - 45.0;
                    y += speed * dt;
                } else if (x >= screenWidth / 2.0 + 12.0 && x <= screenWidth / 2.0 + 18.0
                        && color.equals(Color.YELLOW)) {
                    x = screenWidth / 2.0 + 15.0;
                    y -= speed * dt;
                } else {
                    double stopLine = screenWidth / 2.0 - 95.0;
                    double clearLine = screenWidth / 2.0 - 90.0;

                    if (state.equals(direction)) {
                        x += speed * dt;
                    } else if (x <= stopLine) {
                        x = Math.min(x + speed * dt, stopLine);
                    } else if (x > clearLine) {
                        x += speed * dt;
                    }
                }
                break;
        }
    }
}