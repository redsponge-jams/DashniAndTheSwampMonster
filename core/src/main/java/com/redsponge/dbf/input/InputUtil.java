package com.redsponge.dbf.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class InputUtil {

    public static final int KEY_RIGHT = Keys.RIGHT;
    public static final int KEY_LEFT = Keys.LEFT;
    public static final int KEY_JUMP = Keys.SPACE;

    public static float getHorizontal() {
        return (Gdx.input.isKeyPressed(KEY_RIGHT) ? 1 : 0) - (Gdx.input.isKeyPressed(KEY_LEFT) ? 1 : 0);
    }

    public static boolean isJustJumping() {
        return Gdx.input.isKeyJustPressed(KEY_JUMP);
    }

    public static boolean isJumping() {
        return Gdx.input.isKeyPressed(KEY_JUMP);
    }
}
