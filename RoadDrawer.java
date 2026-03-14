import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class RoadDrawer {

    public static final Color GOLD = new Color(255, 215, 0);

    public static void drawRoad(Graphics2D g, int screenWidth, int screenHeight) {
        float gap = 60.0f;
        float thickness = 3.0f;

        g.setStroke(new BasicStroke(thickness));
        g.setColor(GOLD);

        // Vertical lines
        g.draw(new Line2D.Float(screenWidth / 2.0f - gap, screenHeight / 2.0f - gap, screenWidth / 2.0f - gap, 0));
        g.draw(new Line2D.Float(screenWidth / 2.0f + gap, screenHeight / 2.0f - gap, screenWidth / 2.0f + gap, 0));
        g.draw(new Line2D.Float(screenWidth / 2.0f + gap, screenHeight / 2.0f + gap, screenWidth / 2.0f + gap, screenHeight));
        g.draw(new Line2D.Float(screenWidth / 2.0f - gap, screenHeight / 2.0f + gap, screenWidth / 2.0f - gap, screenHeight));

        // Horizontal lines
        g.draw(new Line2D.Float(screenWidth / 2.0f + gap, screenHeight / 2.0f - gap, screenWidth, screenHeight / 2.0f - gap));
        g.draw(new Line2D.Float(screenWidth / 2.0f + gap, screenHeight / 2.0f + gap, screenWidth, screenHeight / 2.0f + gap));
        g.draw(new Line2D.Float(screenWidth / 2.0f - gap, screenHeight / 2.0f + gap, 0, screenHeight / 2.0f + gap));
        g.draw(new Line2D.Float(screenWidth / 2.0f - gap, screenHeight / 2.0f - gap, 0, screenHeight / 2.0f - gap));

        // Black roads
        g.setColor(Color.BLACK);
        g.fillRect((int)(screenWidth / 2.0f - gap), 0, (int)(gap * 2), screenHeight);
        g.fillRect(0, (int)(screenHeight / 2.0f - gap), screenWidth, (int)(gap * 2));

        drawDashedMiddleLines(g, screenWidth, screenHeight, 20.0f);
    }

    private static void drawDashedMiddleLines(Graphics2D g, int w, int h, float gap) {
        float cx = w / 2.0f;
        float cy = h / 2.0f;
        float dash = 18.0f;
        float space = 12.0f;
        float thickness = 2.0f;

        drawDashedLine(g, cx, 0, cx, cy - gap, dash, space, thickness, Color.YELLOW);
        drawDashedLine(g, cx, cy + gap, cx, h, dash, space, thickness, Color.YELLOW);
        drawDashedLine(g, 0, cy, cx - gap, cy, dash, space, thickness, Color.YELLOW);
        drawDashedLine(g, cx + gap, cy, w, cy, dash, space, thickness, Color.YELLOW);
    }

    private static void drawDashedLine(Graphics2D g, float x1, float y1, float x2, float y2, 
                                       float dashLen, float gapLen, float thickness, Color color) {
        g.setColor(color);
        g.setStroke(new BasicStroke(thickness));
        
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (len <= 0) return;

        float stepX = dx / len;
        float stepY = dy / len;
        float dist = 0.0f;

        while (dist < len) {
            float startX = x1 + stepX * dist;
            float startY = y1 + stepY * dist;
            
            float dashEndDist = Math.min(dist + dashLen, len);
            float endX = x1 + stepX * dashEndDist;
            float endY = y1 + stepY * dashEndDist;

            g.draw(new Line2D.Float(startX, startY, endX, endY));

            dist += dashLen + gapLen;
        }
    }
}