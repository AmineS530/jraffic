package components;
import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Jraffic extends JPanel implements KeyListener {
    private List<Car> cars = new ArrayList<>();
    private TrafficLight trafficLight = new TrafficLight();
    private Random rand = new Random();

    public Jraffic() {
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

        // cars li jayin mn South
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            double x = w / 2.0 + 15.0;
            double y = h - 35.0;
            if (canSpawn("up", x, y))
                cars.add(new Car("up", 30, 30, x, y, rand.nextInt(3) + 1));
        }

        // cars li jayin mn East
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            double x = 10.0;
            double y = h / 2.0 + 15.0;
            if (canSpawn("right", x, y))
                cars.add(new Car("right", 30, 30, x, y, rand.nextInt(3) + 1));
        }

        // cars li jayin mn North
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            double x = w / 2.0 - 45.0;
            double y = 10.0;
            if (canSpawn("down", x, y))
                cars.add(new Car("down", 30, 30, x, y, rand.nextInt(3) + 1));
        }

        // cars li jayin mn West
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            double x = w - 35.0;
            double y = h / 2.0 - 45.0;
            if (canSpawn("left", x, y))
                cars.add(new Car("left", 30, 30, x, y, rand.nextInt(3) + 1));
        }

        // random direction
        if (e.getKeyCode() == KeyEvent.VK_R) {
            int randomDir = rand.nextInt(4);
            String dir = "";
            double x = 0, y = 0;
            switch (randomDir) {
                case 0:
                    dir = "up";
                    x = w / 2.0 + 15.0;
                    y = h - 35.0;
                    break;
                case 1:
                    dir = "down";
                    x = w / 2.0 - 45.0;
                    y = 10.0;
                    break;
                case 2:
                    dir = "left";
                    x = w - 35.0;
                    y = h / 2.0 - 45.0;
                    break;
                case 3:
                    dir = "right";
                    x = 10.0;
                    y = h / 2.0 + 15.0;
                    break;
            }
            if (canSpawn(dir, x, y))
                cars.add(new Car(dir, 30, 30, x, y, rand.nextInt(3) + 1));
        }
    }

    // count kola direction xhal fih mn car
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

    // capacity
    private int calculateLaneCapacity() {
        double laneLength = 400.0;
        double vehicleLength = 30.0;
        double safetyGap = 50.0;
        return (int) Math.floor(laneLength / (vehicleLength + safetyGap));
    }

    private boolean canSpawn(String direction, double x, double y) {
        double safeDist = 60.0;
        for (Car car : cars) {
            if (car.direction.equals(direction)) {
                double dist = (double) Math.sqrt(Math.pow(car.x - x, 2) + Math.pow(car.y - y, 2));
                if (dist < safeDist) {
                    return false;
                }
            }
        }
        return true;
    }

    // Car Updates
    public void updateSimulation(double dt) {
        int w = getWidth();
        int h = getHeight();
        int[] counts = countCarsPerLane();
        int laneCapacity = calculateLaneCapacity();

        trafficLight.updateWithCongestion(dt, counts[0], counts[1], counts[2], counts[3], laneCapacity);

        double safetyGap = 50.0;
        for (int i = 0; i < cars.size(); i++) {
            boolean blocked = false;
            Car myCar = cars.get(i);

            for (int j = 0; j < cars.size(); j++) {
                if (i == j)
                    continue;
                Car other = cars.get(j);

                if (myCar.direction.equals(other.direction)) {
                    double dist = (double) Math.sqrt(Math.pow(myCar.x - other.x, 2) + Math.pow(myCar.y - other.y, 2));
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

        // cars li passed get deleted
        cars.removeIf(car -> car.x < -30.0 || car.x > w + 30.0 || car.y < -30.0 || car.y > h + 30.0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(4, 96, 85));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        RoadDrawer.drawRoad(g2d, getWidth(), getHeight());
        trafficLight.drawLights(g2d, getWidth(), getHeight());

        for (Car car : cars) {
            g2d.setColor(car.color);
            g2d.fillRect((int) car.x, (int) car.y, car.width, car.height);
        }
    }
}