package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.lighting.LightTextures;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.lighting.PointLight;
import com.redsponge.redengine.screen.INotified;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.utils.MathUtilities;

public class FlyLight extends ScreenEntity implements INotified {

    private final Vector2 targetPos;
    private PointLight light;
    private int speed;

    public FlyLight(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
        targetPos = new Vector2();
        speed = 10;
    }

    @Override
    public void added() {
        generateTarget();
        pos.set(targetPos.x, targetPos.y);
        light = new PointLight(0, 0, 16, LightTextures.Point.feathered);
        light.getColor().set(Color.YELLOW);
        light.getColor().a = 0.1f;
        light.getColor().b = 0.5f;

        ((BossFightScreen)screen).getLightSystem().addLight(light, LightType.ADDITIVE);
    }

    private void generateTarget() {
        targetPos.set(MathUtils.random(screen.getScreenWidth()), MathUtils.random(screen.getScreenHeight() / 4, screen.getScreenHeight()));
    }

    @Override
    public void removed() {
        ((BossFightScreen)screen).getLightSystem().removeLight(light, LightType.ADDITIVE);
    }

    @Override
    public void additionalTick(float delta) {
        if(Vector2.dst2(pos.getX(), pos.getY(), targetPos.x, targetPos.y) < 100 * 100) {
            generateTarget();
        }
        float angle = MathUtils.atan2(targetPos.y - pos.getY(), targetPos.x - pos.getX());
        vel.setX(MathUtilities.lerp(vel.getX(), (float) Math.cos(angle) * speed, MathUtils.random(0.01f, 0.1f)));
        vel.setY(MathUtilities.lerp(vel.getY(), (float) Math.sin(angle) * speed, MathUtils.random(0.01f, 0.1f)));

        light.pos.set(pos.getX(), pos.getY());
    }

    @Override
    public void notified(Object o, int i) {
        if(i == Notifications.CHANGED_PHASE) {
            if(BossFightScreen.phase == BossFightScreen.FightPhase.ONE) {
                speed = 150;
            } else {
                speed += 100;
            }
        }
    }
}
