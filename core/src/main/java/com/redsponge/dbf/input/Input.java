package com.redsponge.dbf.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class Input {

    public static final int KEY_RIGHT = Keys.RIGHT;
    public static final int KEY_LEFT = Keys.LEFT;
    public static final int KEY_UP = Keys.UP;
    public static final int KEY_DOWN = Keys.DOWN;
    public static final int KEY_JUMP = Keys.SPACE;
    public static final int KEY_ATTACK = Keys.X;

    public static float getHorizontal() {
        return (Gdx.input.isKeyPressed(KEY_RIGHT) ? 1 : 0) - (Gdx.input.isKeyPressed(KEY_LEFT) ? 1 : 0);
    }

    public static float getVertical() {
        return (Gdx.input.isKeyPressed(KEY_UP) ? 1 : 0) - (Gdx.input.isKeyPressed(KEY_DOWN) ? 1 : 0);
    }

    public static boolean isJustJumping() {
        return Gdx.input.isKeyJustPressed(KEY_JUMP);
    }

    public static boolean isJumping() {
        return Gdx.input.isKeyPressed(KEY_JUMP);
    }

    public static boolean isJustAttacking() {
        return Gdx.input.isKeyJustPressed(KEY_ATTACK);
    }
}
