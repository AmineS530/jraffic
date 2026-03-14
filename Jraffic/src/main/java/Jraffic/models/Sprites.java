package Jraffic.models;

import java.util.EnumMap;
import java.util.Map;

import Jraffic.helpers.Direction;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Sprites {

    public static final int FRAME_W = 88;
    public static final int FRAME_H = 88;

    private final Map<Direction, WritableImage[]> strips = new EnumMap<>(Direction.class);

    public Sprites(String path) {
        Image sheet = new Image(
            getClass().getResourceAsStream(path)
        );
        strips.put(Direction.SOUTH, sliceRow(sheet, 0, 8));
        strips.put(Direction.WEST,  sliceRow(sheet, 1, 2));
        strips.put(Direction.EAST,  sliceRow(sheet, 2, 2));
        strips.put(Direction.NORTH, sliceRow(sheet, 3, 2));
    }

    private WritableImage[] sliceRow(Image sheet, int row, int frameCount) {
        WritableImage[] frames = new WritableImage[frameCount];
        for (int col = 0; col < frameCount; col++) {
            frames[col] = new WritableImage(
                sheet.getPixelReader(),
                col * FRAME_W,
                row * FRAME_H,
                FRAME_W,
                FRAME_H
            );
        }
        return frames;
    }

    public WritableImage getFrame(Direction direction, int animFrame) {
        WritableImage[] strip = strips.get(direction);
        return strip[animFrame % strip.length];
    }
}