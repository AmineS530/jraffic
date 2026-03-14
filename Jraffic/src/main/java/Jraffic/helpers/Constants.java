package Jraffic.helpers;

public final class Constants {

    private Constants() {}

    // Target frames per second — change this one value to affect everything
    public static final int TARGET_FPS = 75;

    // Derived — do not edit these
    public static final long FRAME_NS        = 1_000_000_000L / TARGET_FPS;
    public static final long SPRITE_FRAME_NS = FRAME_NS * 6; // advance sprite every 6 sim frames
}