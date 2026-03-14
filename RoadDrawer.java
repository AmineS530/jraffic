import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class RoadDrawer {

    public static final Color GOLD = new Color(255, 215, 0);

    public static void drawRoad(Graphics2D g, int screenWidth, int screenHeight) {
        double gap = 60.0f;
        double thickness = 3.0f;

        g.setStroke(new BasicStroke((float) thickness));
        g.setColor(GOLD);

        // Vertical lines
        g.draw(new Line2D.Double(screenWidth / 2.0f - gap, screenHeight / 2.0f - gap, screenWidth / 2.0f - gap, 0));
        g.draw(new Line2D.Double(screenWidth / 2.0f + gap, screenHeight / 2.0f - gap, screenWidth / 2.0f + gap, 0));
        g.draw(new Line2D.Double(screenWidth / 2.0f + gap, screenHeight / 2.0f + gap, screenWidth / 2.0f + gap,
                screenHeight));
        g.draw(new Line2D.Double(screenWidth / 2.0f - gap, screenHeight / 2.0f + gap, screenWidth / 2.0f - gap,
                screenHeight));

        // Horizontal lines
        g.draw(new Line2D.Double(screenWidth / 2.0f + gap, screenHeight / 2.0f - gap, screenWidth,
                screenHeight / 2.0f - gap));
        g.draw(new Line2D.Double(screenWidth / 2.0f + gap, screenHeight / 2.0f + gap, screenWidth,
                screenHeight / 2.0f + gap));
        g.draw(new Line2D.Double(screenWidth / 2.0f - gap, screenHeight / 2.0f + gap, 0, screenHeight / 2.0f + gap));
        g.draw(new Line2D.Double(screenWidth / 2.0f - gap, screenHeight / 2.0f - gap, 0, screenHeight / 2.0f - gap));

        // Black roads
        g.setColor(Color.BLACK);
        g.fillRect((int) (screenWidth / 2.0f - gap), 0, (int) (gap * 2), screenHeight);
        g.fillRect(0, (int) (screenHeight / 2.0f - gap), screenWidth, (int) (gap * 2));

        drawDashedMiddleLines(g, screenWidth, screenHeight, 20.0f);
    }

    private static void drawDashedMiddleLines(Graphics2D g, int w, int h, float gap) {
        double cx = w / 2.0f;
        double cy = h / 2.0f;
        double dash = 18.0f;
        double space = 12.0f;
        double thickness = 2.0f;

        drawDashedLine(g, cx, 0, cx, cy - gap, dash, space, thickness, Color.YELLOW);
        drawDashedLine(g, cx, cy + gap, cx, h, dash, space, thickness, Color.YELLOW);
        drawDashedLine(g, 0, cy, cx - gap, cy, dash, space, thickness, Color.YELLOW);
        drawDashedLine(g, cx + gap, cy, w, cy, dash, space, thickness, Color.YELLOW);
    }

    private static void drawDashedLine(Graphics2D g, double x1, double y1, double x2, double y2,
            double dashLen, double gapLen, double thickness, Color color) {
        g.setColor(color);
        g.setStroke(new BasicStroke((float) thickness));

        double dx = x2 - x1;
        double dy = y2 - y1;
        double len = (double) Math.sqrt(dx * dx + dy * dy);

        if (len <= 0)
            return;

        double stepX = dx / len;
        double stepY = dy / len;
        double dist = 0.0f;

        while (dist < len) {
            double startX = x1 + stepX * dist;
            double startY = y1 + stepY * dist;

            double dashEndDist = Math.min(dist + dashLen, len);
            double endX = x1 + stepX * dashEndDist;
            double endY = y1 + stepY * dashEndDist;

            g.draw(new Line2D.Double(startX, startY, endX, endY));

            dist += dashLen + gapLen;
        }
    }
}