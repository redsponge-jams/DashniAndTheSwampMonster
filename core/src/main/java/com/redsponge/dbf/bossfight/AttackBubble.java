package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class AttackBubble extends ScreenEntity {

    private float x, y, px, py;
    private float timeAlive;

    private Rectangle ouchBox;

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
        ((BossFightScreen)screen).getAttackBoxes().add(ouchBox);
        double angle = Math.atan2(py - y, px - x);
        vel.set((float) Math.cos(angle) * 100, (float) (Math.sin(angle) * 100));
    }

    @Override
    public void additionalTick(float delta) {
        timeAlive += delta;
        ouchBox.set(pos.getX() + 2, pos.getY() + 2, size.getX() - 4, size.getY() - 4);
        if(timeAlive > 3) {
            remove();
        }
    }

    @Override
    public void loadAssets() {
        TextureComponent tex = new TextureComponent(assets.getTextureRegion("bubble"));
        add(tex);
    }

    @Override
    public void removed() {
        ((BossFightScreen)screen).getAttackBoxes().removeValue(ouchBox, true);
    }
}
