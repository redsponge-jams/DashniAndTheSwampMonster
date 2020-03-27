package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class BossAttacks {

    public static void stairs(SpriteBatch batch, ShapeRenderer sr, BossFightScreen screen) {
        for(int i = 0; i < 6; i++) {
            screen.addScheduledEntity(i * 0.2f, new SideAttack(batch, sr, i * 64 + 50, MathUtils.randomBoolean(), (7 - i) * 60, 0.2f, 3, 1));
        }
    }

    public static void attackPlayer(SpriteBatch batch, ShapeRenderer sr, BossFightScreen screen, int py) {
        screen.addEntity(new SideAttack(batch, sr, py, MathUtils.randomBoolean(), screen.getScreenWidth(), 1.5f, 0, 1));
    }

    public static void closeLine(SpriteBatch batch, ShapeRenderer sr, BossFightScreen screen, int y) {
//        screen.addScheduledEntity(0, new SideAttack(batch, sr, y, true, screen.getScreenWidth() / 2 + 60, 2, 2));
        screen.addScheduledEntity(0, new SideAttack(batch, sr, y, Gdx.input.isKeyPressed(Keys.NUM_1), screen.getScreenWidth() / 2, 2, 2, 1));
    }

}
