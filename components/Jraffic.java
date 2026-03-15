package components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.JPanel;

public class Jraffic extends JPanel implements KeyListener {
    private List<Car> cars = new ArrayList<>();
    private TrafficLight trafficLight = new TrafficLight();

    private final Set<Integer> heldKeys = new HashSet<>();

    private Random rand = new Random();
    private final Sprites[] spriteSheets = {
            new Sprites("assets/car_00.png"),
            new Sprites("assets/car_01.png"),
            new Sprites("assets/car_02.png")
    };

    public Jraffic() {
        setPreferredSize(new Dimension(1440, 900 ));
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (heldKeys.contains(key))
            return;
        heldKeys.add(key);
        if (key == KeyEvent.VK_ESCAPE)
            System.exit(0);
        if (key == KeyEvent.VK_C || key == KeyEvent.VK_BACK_SPACE)
            cars.clear();
        if (key == KeyEvent.VK_UP)
            trySpawn("up");
        if (key == KeyEvent.VK_DOWN)
            trySpawn("down");
        if (key == KeyEvent.VK_LEFT)
            trySpawn("left");
        if (key == KeyEvent.VK_RIGHT)
            trySpawn("right");
        if (key == KeyEvent.VK_R)
            trySpawn(randomDir());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        heldKeys.remove(e.getKeyCode());
    }

    private String randomDir() {
        return new String[] { "up", "down", "left", "right" }[rand.nextInt(4)];
    }

    /** Computes the spawn position for a direction, then spawns if safe. */
    private void trySpawn(String dir) {
        int w = getWidth();
        int h = getHeight();

        record Point(double x, double y) {
        }

        Point p = switch (dir) {
            case "up" -> new Point(w / 2.0 + 35.0, h - 35.0);
            case "down" -> new Point(w / 2.0 - 35.0, 10.0);
            case "left" -> new Point(w , h / 2.0 - 35.0);
            case "right" -> new Point(35.0, h / 2.0 + 35.0);
            default -> new Point(0, 0);
        };

        if (canSpawn(dir, p.x(), p.y()))
            cars.add(new Car(dir, p.x(), p.y(), rand.nextInt(3)));
    }

    // count kola direction xhal fih mn car
    private int[] countCarsPerLane() {
        int up = 0, down = 0, left = 0, right = 0;
        for (Car car : cars) {
            switch (car.direction) {
                case "up" -> up++;
                case "down" -> down++;
                case "left" -> left++;
                case "right" -> right++;
            }
        }
        return new int[] { up, down, left, right };
    }

    // capacity
    private int calculateLaneCapacity() {
        double laneLength = 400.0;
        double vehicleLength = 88.0;
        double safetyGap = 60.0;
        return (int) Math.floor(laneLength / (vehicleLength + safetyGap));
    }

    private boolean canSpawn(String direction, double x, double y) {
        double safeDist =  88.0;
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

        double safetyGap = 110.0;
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
                            case "up" -> isAhead = other.y < myCar.y;
                            case "down" -> isAhead = other.y > myCar.y;
                            case "left" -> isAhead = other.x < myCar.x;
                            case "right" -> isAhead = other.x > myCar.x;
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
        cars.removeIf(car -> car.x < -88.0 || car.x > w + 88.0 || car.y < -88.0 || car.y > h + 88.0);
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
            BufferedImage frame = spriteSheets[car.spriteIndex].getFrame(car.direction, car.animFrame);
            if (frame != null) {
                g2d.drawImage(frame, (int) (car.x - Sprites.FRAME_W / 2.0),
                        (int) (car.y - Sprites.FRAME_H / 2.0),
                        Sprites.FRAME_W, Sprites.FRAME_H, null);
            }
        }
    }
}