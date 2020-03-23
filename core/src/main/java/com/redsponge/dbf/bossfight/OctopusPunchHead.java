package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.redsponge.redengine.screen.INotified;
import com.redsponge.redengine.screen.components.AnimationComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.screen.systems.RenderSystem;

public class OctopusPunchHead extends ScreenEntity implements INotified {

    private Animation<TextureRegion> raise;
    private Animation<TextureRegion> stun;
    private Animation<TextureRegion> sink;

    private AnimationComponent anim;
    private Rectangle eye;
    private float timeLeftStunned;
    private boolean sinking;

    public OctopusPunchHead(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        pos.setX(640-128*2-30);
        pos.setZ(-2);
        size.set(128*2, 128*2);
    }

    @Override
    public void loadAssets() {
        raise = assets.getAnimation("octopusRaiseAnimation");
        stun = assets.getAnimation("octopusStunAnimation");
        sink = assets.getAnimation("octopusSinkAnimation");

        anim = new AnimationComponent(raise);
        add(anim);
    }

    public void sink() {
        eye.set(0, 0, 0, 0);
        anim.setAnimation(sink);
        anim.setAnimationTime(0);
    }

    @Override
    public void additionalTick(float delta) {
        super.additionalTick(delta);
        if(eye == null && raise.isAnimationFinished(anim.getAnimationTime())) {
            eye = new Rectangle(pos.getX() + 11 * 2, pos.getY() + 2*(128-73), 61 * 2, 39 * 2);
        }

        if(timeLeftStunned > 0) {
            timeLeftStunned-= delta;
            if(timeLeftStunned <= 0) {
                sink();
                sinking = true;
            }
        }
        if(sinking) {
            if(sink.isAnimationFinished(anim.getAnimationTime())) {
                notifyScreen(Notifications.OCTOPUS_EYE_GONE);
                remove();
            }
        }
    }

    @Override
    public void additionalRender() {
        shapeRenderer.setProjectionMatrix(screen.getEntitySystem(RenderSystem.class).getCamera().combined);
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        if(eye != null) {
            shapeRenderer.rect(eye.x, eye.y, eye.width, eye.height);
        }
        shapeRenderer.end();
    }

    @Override
    public void notified(Object o, int i) {
        if(i == Notifications.PLAYER_ATTACK_BOX_SPAWNED) {
            if(eye != null && ((DashniPlayer)o).getAttackBox().overlaps(eye)) {
                ouch();
            }
        }
    }

    private void ouch() {
        anim.setAnimation(stun);
        anim.setAnimationTime(0);
        timeLeftStunned = 3;
        eye.set(0, 0, 0, 0);
    }
}
