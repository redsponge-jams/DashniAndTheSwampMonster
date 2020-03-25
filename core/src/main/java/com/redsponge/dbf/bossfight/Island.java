package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.redsponge.redengine.physics.PBodyType;
import com.redsponge.redengine.screen.components.NinePatchComponent;
import com.redsponge.redengine.screen.components.PhysicsComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Island extends ScreenEntity {

    private int x, y, w, h;
    private NinePatch patch;

    private float timeExists;
    private float boostTime;
    private boolean boosting;
    private float boostYStart;
    private PhysicsComponent physics;

    public Island(SpriteBatch batch, ShapeRenderer shapeRenderer, int x, int y, int w, int h) {
        super(batch, shapeRenderer);
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.timeExists = MathUtils.random(0, (float) (2 * Math.PI));
    }

    @Override
    public void added() {
        pos.set(x, y);
        size.set(w, h);
        add(physics = new PhysicsComponent(PBodyType.SOLID));
    }

    @Override
    public void additionalTick(float delta) {

        if(boosting) {
            boostTime += delta;
            vel.setY(100 * (-8*boostTime + 4)); // derivative of -4x^2+4x
            if(boostTime >= 1) {
                vel.setY((boostYStart - pos.getY()) / delta);
                boosting = false;
            }
        } else {
            timeExists += delta;
            vel.setY((float) (Math.cos(2 * timeExists) * 10)); // derivative of sin
            vel.setX((float) (-Math.sin(2 * timeExists) * 5)); // derivative of cos
        }
    }

    @Override
    public void loadAssets() {
        patch = new NinePatch(assets.getTextureRegion("island"), 4, 4, 3, 7);
        NinePatchComponent npc = new NinePatchComponent(patch);
        add(npc);
    }

    public void boost() {
        boosting = true;
        boostTime = 0;
        boostYStart = pos.getY();
    }
}
