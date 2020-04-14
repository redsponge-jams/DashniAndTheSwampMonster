package com.redsponge.dbf.bossfight.attacks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.redsponge.dbf.bossfight.BossFightScreen;

public class BossAttacks {

    public static void stairwayI(SpriteBatch batch, ShapeRenderer shapeRenderer, BossFightScreen screen) {
        screen.addScheduledEntity(0, new SideAttack(batch, shapeRenderer, 50, MathUtils.randomBoolean(), screen.getScreenWidth() / 2, 2, 2, .8f));
        screen.addScheduledEntity(0.5f, new SideAttack(batch, shapeRenderer, 100, MathUtils.randomBoolean(), screen.getScreenWidth() / 2 - 100, 2, 2, .8f));
    }

    public static void stairwayII(SpriteBatch batch, ShapeRenderer sr, BossFightScreen screen) {
        for(int i = 0; i < 4; i++) {
            screen.addScheduledEntity(i * 0.2f, new SideAttack(batch, sr, i * 80 + 50, MathUtils.randomBoolean(), (7 - i) * 60, 0.2f, 3, 1));
        }
    }

    public static void attackPlayer(SpriteBatch batch, ShapeRenderer sr, BossFightScreen screen, int py) {
        screen.addEntity(new SideAttack(batch, sr, py, MathUtils.randomBoolean(), screen.getScreenWidth(), 1.5f, 0, 1));
    }

    public static void closeLine(SpriteBatch batch, ShapeRenderer sr, BossFightScreen screen, int y) {
        screen.addScheduledEntity(0, new SideAttack(batch, sr, y, true, screen.getScreenWidth() / 2, 1.5f, 2, 1));
        screen.addScheduledEntity(0, new SideAttack(batch, sr, y, false, screen.getScreenWidth() / 2, 1.5f, 2, 1));
    }

}
