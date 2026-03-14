import javax.swing.Timer;
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

        Timer timer = new Timer(16, e -> {
            gamePanel.updateSimulation(0.016);
            gamePanel.repaint();
        });

        timer.start();
    }
}