package com.redsponge.dbf.bossfight.visual;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.redsponge.redengine.screen.components.AnimationComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Water extends ScreenEntity {
    public Water(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        pos.set(0, 0, 10);
        size.set(screen.getScreenWidth(), screen.getScreenHeight());
        render.setUseRegH(true).setUseRegW(true).getColor().a = 0.8f;
    }

    @Override
    public void loadAssets() {
        add(new AnimationComponent(assets.getAnimation("water")));
    }
}
