package com.redsponge.dbf.bossfight.visual;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Background extends ScreenEntity {

    public Background(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        pos.set(0, 0, -10);
        size.set(screen.getScreenWidth(), screen.getScreenHeight());
    }

    @Override
    public void additionalTick(float delta) {
        super.additionalTick(delta);
        Vector3 camPos =((BossFightScreen)screen).getRenderSystem().getViewport().getCamera().position;
        pos.set(camPos.x - screen.getScreenWidth() / 2f, camPos.y - screen.getScreenHeight() / 2f);
    }

    @Override
    public void loadAssets() {
        add(new TextureComponent(assets.getTextureRegion("background")));
    }
}
