import javax.swing.JFrame;

import components.Jraffic;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Jraffic");
        Jraffic gamePanel = new Jraffic();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        long lastTime = System.nanoTime();
        // our loop
        while (true) {
            long now = System.nanoTime();
            double dt = (double) ((now - lastTime) / 1000000000.0);
            lastTime = now;

            gamePanel.updateSimulation(dt);
            gamePanel.repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}