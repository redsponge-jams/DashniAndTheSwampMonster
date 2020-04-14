package com.redsponge.dbf.bossfight.attacks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.dbf.bossfight.Notifications;
import com.redsponge.redengine.lighting.LightTextures;
import com.redsponge.redengine.lighting.LightTextures.Point;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.lighting.PointLight;
import com.redsponge.redengine.screen.INotified;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class AttackBubble extends ScreenEntity implements INotified {

    private float x, y, px, py;
    private float timeAlive;

    private Rectangle ouchBox;

    private PointLight light;

    public AttackBubble(SpriteBatch batch, ShapeRenderer shapeRenderer, float x, float y, float px, float py) {
        super(batch, shapeRenderer);
        this.x = x;
        this.y = y;
        this.px = px;
        this.py = py;
    }

    @Override
    public void added() {
        pos.set(x, y);
        size.set(16, 16);
        render.getColor().a = 0.5f;
        ouchBox = new Rectangle(pos.getX() + 2, pos.getY() + 2, size.getX() - 4, size.getY() - 4);
        ((BossFightScreen) screen).getAttackBoxes().add(ouchBox);
        double angle = Math.atan2(py - y, px - x);
        vel.set((float) Math.cos(angle) * 100, (float) (Math.sin(angle) * 100));

        light = new PointLight(0, 0, 32, Point.feathered);
        light.getColor().set(Color.CYAN).a = 0.5f;
        ((BossFightScreen) screen).getLightSystem().addLight(light, LightType.ADDITIVE);
    }

    @Override
    public void additionalTick(float delta) {
        timeAlive += delta;
        ouchBox.set(pos.getX() + 2, pos.getY() + 2, size.getX() - 4, size.getY() - 4);
        light.pos.set(pos.getX() + size.getX() / 2f + vel.getX() * delta, pos.getY() + size.getY() / 2f + vel.getY() * delta);
        if (timeAlive > 3 || ((BossFightScreen) screen).isHeadUp()) {
            remove();
        }
    }

    @Override
    public void loadAssets() {
        TextureComponent tex = new TextureComponent(assets.getTextureRegion("bubble"));
        add(tex);
    }

    @Override
    public void notified(Object o, int i) {
        if (i == Notifications.TARGET_OCTOPUS_DOWN) {
            remove();
        }
    }

    @Override
    public void removed() {
        ((BossFightScreen) screen).getAttackBoxes().removeValue(ouchBox, true);
        ((BossFightScreen) screen).getLightSystem().removeLight(light, LightType.ADDITIVE);
    }
}
