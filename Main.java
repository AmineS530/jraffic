import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JPanel implements KeyListener {

    private List<Car> cars = new ArrayList<>();
    private TrafficLight trafficLight = new TrafficLight();
    private Random rand = new Random();

    public Main() {
        setPreferredSize(new Dimension(900, 800));
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int w = getWidth();
        int h = getHeight();
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
        if (e.getKeyCode() == KeyEvent.VK_C || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            cars.clear();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            float x = w / 2.0f + 15.0f;
            float y = h - 35.0f;
            if (canSpawn("up", x, y))
                cars.add(new Car("up", 30, 30, x, y, rand.nextInt(3) + 1));
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            float x = 10.0f;
            float y = h / 2.0f + 15.0f;
            if (canSpawn("right", x, y))
                cars.add(new Car("right", 30, 30, x, y, rand.nextInt(3) + 1));
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            float x = w / 2.0f - 45.0f;
            float y = 10.0f;
            if (canSpawn("down", x, y))
                cars.add(new Car("down", 30, 30, x, y, rand.nextInt(3) + 1));
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            float x = w - 35.0f;
            float y = h / 2.0f - 45.0f;
            if (canSpawn("left", x, y))
                cars.add(new Car("left", 30, 30, x, y, rand.nextInt(3) + 1));
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            int randomDir = rand.nextInt(4);
            String dir = "";
            float x = 0, y = 0;
            switch (randomDir) {
                case 0:
                    dir = "up";
                    x = w / 2.0f + 15.0f;
                    y = h - 35.0f;
                    break;
                case 1:
                    dir = "down";
                    x = w / 2.0f - 45.0f;
                    y = 10.0f;
                    break;
                case 2:
                    dir = "left";
                    x = w - 35.0f;
                    y = h / 2.0f - 45.0f;
                    break;
                case 3:
                    dir = "right";
                    x = 10.0f;
                    y = h / 2.0f + 15.0f;
                    break;
            }
            if (canSpawn(dir, x, y))
                cars.add(new Car(dir, 30, 30, x, y, rand.nextInt(3) + 1));
        }
    }

    private int[] countCarsPerLane() {
        int up = 0, down = 0, left = 0, right = 0;
        for (Car car : cars) {
            switch (car.direction) {
                case "up":
                    up++;
                    break;
                case "down":
                    down++;
                    break;
                case "left":
                    left++;
                    break;
                case "right":
                    right++;
                    break;
            }
        }
        return new int[] { up, down, left, right };
    }

    private int calculateLaneCapacity() {
        float laneLength = 400.0f;
        float vehicleLength = 30.0f;
        float safetyGap = 50.0f;
        return (int) Math.floor(laneLength / (vehicleLength + safetyGap));
    }

    private boolean canSpawn(String direction, float x, float y) {
        float safeDist = 60.0f;
        for (Car car : cars) {
            if (car.direction.equals(direction)) {
                float dist = (float) Math.sqrt(Math.pow(car.x - x, 2) + Math.pow(car.y - y, 2));
                if (dist < safeDist) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updateSimulation(float dt) {
        int w = getWidth();
        int h = getHeight();
        int[] counts = countCarsPerLane();
        int laneCapacity = calculateLaneCapacity();

        trafficLight.updateWithCongestion(dt, counts[0], counts[1], counts[2], counts[3], laneCapacity);

        // Car Updates
        float safetyGap = 50.0f;
        for (int i = 0; i < cars.size(); i++) {
            boolean blocked = false;
            Car myCar = cars.get(i);

            for (int j = 0; j < cars.size(); j++) {
                if (i == j)
                    continue;
                Car other = cars.get(j);

                if (myCar.direction.equals(other.direction)) {
                    float dist = (float) Math.sqrt(Math.pow(myCar.x - other.x, 2) + Math.pow(myCar.y - other.y, 2));
                    if (dist < safetyGap) {
                        boolean isAhead = false;
                        switch (myCar.direction) {
                            case "up":
                                isAhead = other.y < myCar.y;
                                break;
                            case "down":
                                isAhead = other.y > myCar.y;
                                break;
                            case "left":
                                isAhead = other.x < myCar.x;
                                break;
                            case "right":
                                isAhead = other.x > myCar.x;
                                break;
                        }
                        if (isAhead) {
                            blocked = true;
                            break;
                        }
                    }
                }
            }
            myCar.update(dt, trafficLight.state, blocked, w, h);
        }

        // Filter out-of-bounds cars
        cars.removeIf(car -> car.x < -30.0f || car.x > w + 30.0f || car.y < -30.0f || car.y > h + 30.0f);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Anti-aliasing for smoother lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background color
        g2d.setColor(new Color(4, 96, 85));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        RoadDrawer.drawRoad(g2d, getWidth(), getHeight());
        trafficLight.drawLights(g2d, getWidth(), getHeight());

        for (Car car : cars) {
            g2d.setColor(car.color);
            g2d.fillRect((int) car.x, (int) car.y, car.width, car.height);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Jraffic");
        Main gamePanel = new Main();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        // Game loop directly in the main method
        long lastTime = System.nanoTime();

        while (true) {
            long now = System.nanoTime();
            float dt = (float) ((now - lastTime) / 1000000000.0);
            lastTime = now;

            gamePanel.updateSimulation(dt);
            gamePanel.repaint();

            // Cap frame rate slightly to avoid 100% CPU usage
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}