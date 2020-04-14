package com.redsponge.dbf.bossfight.fight;

public class EyePos {
    public static final EyePos NONE = new EyePos(0, 0, 0);

    private final int x, y, rad;

    public EyePos(int x, int y) {
        this(x, 256 - y, 96);
    }

    public EyePos(int x, int y, int rad) {
        this.x = x;
        this.y = y;
        this.rad = rad;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int rad() {
        return rad;
    }
}
