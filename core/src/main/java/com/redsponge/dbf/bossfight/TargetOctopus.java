package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.screen.INotified;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.screen.systems.RenderSystem;
import com.redsponge.redengine.utils.IntVector2;
import com.redsponge.redengine.utils.Logger;
import com.redsponge.redengine.utils.MathUtilities;

public class TargetOctopus extends ScreenEntity implements INotified {

    private static final Color stunColor = new Color(1, 1, 1, 0.5f);

    private TextureComponent tex;

    private IntVector2 targetLoc;
    private Vector2 singularVel;

    private int hitsLeft;
    private Rectangle self;

    public TargetOctopus(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
        targetLoc = new IntVector2();
        singularVel = new Vector2();
        singularVel.set(1, 1);
        self = new Rectangle();
        hitsLeft = 5;
    }

    @Override
    public void added() {
        pos.set(screen.getScreenWidth() / 2f, screen.getScreenHeight() / 2f);
        size.set(64, 64);
        generateTarget();
    }

    private void generateTarget() {
        targetLoc.set(MathUtils.random(screen.getScreenWidth()), MathUtils.random(screen.getScreenHeight()));
    }

    @Override
    public void additionalTick(float delta) {
        if(Vector2.dst2(pos.getX() + size.getX() / 2f, pos.getY() + size.getY() / 2f, targetLoc.x, targetLoc.y) < 1000) {
            generateTarget();
        }
        if(hitsLeft == 0) {
            pos.setX(MathUtilities.lerp(pos.getX(), screen.getScreenWidth() / 2f - size.getX() / 2f, 0.2f));
            pos.setY(MathUtilities.lerp(pos.getY(), screen.getScreenHeight() / 2f - size.getY() / 2f + 100, 0.2f));
            vel.set(0, 0);
            render.getColor().lerp(stunColor, 0.1f);
        } else {
            render.getColor().lerp(Color.WHITE, 0.1f);

            float diffX = targetLoc.x - (pos.getX() + size.getX() / 2f);
            float diffY = targetLoc.y - (pos.getY() + size.getY() / 2f);

            float angle = (float) Math.atan2(diffY, diffX);
            float wantedVX = (float) Math.cos(angle);
            float wantedVY = (float) Math.sin(angle);

            vel.setX(MathUtilities.lerp(vel.getX(), wantedVX * 100, 0.01f));
            vel.setY(MathUtilities.lerp(vel.getY(), wantedVY * 100, 0.01f));
            self.set(pos.getX(), pos.getY(), size.getX(), size.getY());
        }
    }

    @Override
    public void additionalRender() {
        shapeRenderer.setProjectionMatrix(screen.getEntitySystem(RenderSystem.class).getCamera().combined);
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(pos.getX(), pos.getY(), size.getX(), size.getY());
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(targetLoc.x, targetLoc.y, 10);
        shapeRenderer.end();
    }

    @Override
    public void loadAssets() {
        tex = new TextureComponent(assets.getTextureRegion("targetIdle"));
        add(tex);
    }

    @Override
    public void notified(Object o, int i) {
        if(i == Notifications.PLAYER_ATTACK_BOX_SPAWNED && hitsLeft > 0) {
            Rectangle attack = ((DashniPlayer)o).getAttackBox();
            if(attack.overlaps(self)) {
                attacked();
            }
        } else if(hitsLeft == 0 && i == Notifications.OCTOPUS_EYE_GONE) {
            hitsLeft = 5;
        }
    }

    private void attacked() {
        Logger.log(this, "OUCHY WOWCHI");
        hitsLeft--;
        if(hitsLeft == 0) {
            notifyScreen(Notifications.TARGET_OCTOPUS_DOWN);
        }
    }
}
