package components;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Sprites {

    public static final int FRAME_W = 88;
    public static final int FRAME_H = 88;

    private final Map<String, BufferedImage[]> strips = new HashMap<>();

    public Sprites(String resourcePath) {
        try {
            BufferedImage sheet = ImageIO.read(Sprites.class.getResourceAsStream(resourcePath));
            strips.put("down", sliceRow(sheet, 0, 8));
            strips.put("left", sliceRow(sheet, 1, 2));
            strips.put("right", sliceRow(sheet, 2, 2));
            strips.put("up", sliceRow(sheet, 3, 2));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Failed to load sprite sheet: " + resourcePath);
            e.printStackTrace();
        }
    }

    private BufferedImage[] sliceRow(BufferedImage sheet, int row, int frameCount) {
        BufferedImage[] frames = new BufferedImage[frameCount];
        for (int col = 0; col < frameCount; col++) {
            frames[col] = sheet.getSubimage(col * FRAME_W, row * FRAME_H, FRAME_W, FRAME_H);
        }
        return frames;
    }

    public BufferedImage getFrame(String direction, int animFrame) {
        BufferedImage[] strip = strips.get(direction);
        if (strip == null)
            return null;
        return strip[animFrame % strip.length];
    }
}