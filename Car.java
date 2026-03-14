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
        this.speed = 400.0f;

        // 1 = YELLOW, 2 = BLUE, others = RED
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
                if (y >= screenHeight / 2.0f + 12.0f && y <= screenHeight / 2.0f + 18.0f && color.equals(Color.RED)) {
                    y = screenHeight / 2.0f + 15.0f;
                    x += speed * dt;
                } else if (y >= screenHeight / 2.0f - 48.0f && y <= screenHeight / 2.0f - 43.0f
                        && color.equals(Color.YELLOW)) {
                    y = screenHeight / 2.0f - 45.0f;
                    x -= speed * dt;
                } else {
                    double stopLine = screenHeight / 2.0f + 65.0f;
                    double clearLine = screenHeight / 2.0f + 60.0f;

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
                if (y >= screenHeight / 2.0f - 48.0f && y <= screenHeight / 2.0f - 43.0f && color.equals(Color.RED)) {
                    y = screenHeight / 2.0f - 45.0f;
                    x -= speed * dt;
                } else if (y >= screenHeight / 2.0f + 12.0f && y <= screenHeight / 2.0f + 18.0f
                        && color.equals(Color.YELLOW)) {
                    y = screenHeight / 2.0f + 15.0f;
                    x += speed * dt;
                } else {
                    double stopLine = screenHeight / 2.0f - 95.0f;
                    double clearLine = screenHeight / 2.0f - 90.0f;

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
                if (x >= screenWidth / 2.0f + 12.0f && x <= screenWidth / 2.0f + 18.0f && color.equals(Color.RED)) {
                    x = screenWidth / 2.0f + 15.0f;
                    y -= speed * dt;
                } else if (x >= screenWidth / 2.0f - 48.0f && x <= screenWidth / 2.0f - 42.0f
                        && color.equals(Color.YELLOW)) {
                    x = screenWidth / 2.0f - 45.0f;
                    y += speed * dt;
                } else {
                    double stopLine = screenWidth / 2.0f + 65.0f;
                    double clearLine = screenWidth / 2.0f + 60.0f;

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
                if (x >= screenWidth / 2.0f - 48.0f && x <= screenWidth / 2.0f - 42.0f && color.equals(Color.RED)) {
                    x = screenWidth / 2.0f - 45.0f;
                    y += speed * dt;
                } else if (x >= screenWidth / 2.0f + 12.0f && x <= screenWidth / 2.0f + 18.0f
                        && color.equals(Color.YELLOW)) {
                    x = screenWidth / 2.0f + 15.0f;
                    y -= speed * dt;
                } else {
                    double stopLine = screenWidth / 2.0f - 95.0f;
                    double clearLine = screenWidth / 2.0f - 90.0f;

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