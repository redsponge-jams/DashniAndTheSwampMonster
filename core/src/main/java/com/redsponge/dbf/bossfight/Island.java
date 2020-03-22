package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.redsponge.redengine.physics.PBodyType;
import com.redsponge.redengine.screen.components.PhysicsComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Island extends ScreenEntity {

    private int x, y, w, h;
    public Island(SpriteBatch batch, ShapeRenderer shapeRenderer, int x, int y, int w, int h) {
        super(batch, shapeRenderer);
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @Override
    public void added() {
        pos.set(x, y);
        size.set(w, h);
        add(new PhysicsComponent(PBodyType.SOLID));
    }
}
