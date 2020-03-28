package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.redsponge.dbf.bossfight.BossFightScreen.FightPhase;
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
    private float timeBeforeBored;
    private boolean bored;

    private HeadState state;

    private Sound stunSound;

    public OctopusPunchHead(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        pos.setX(640-128*2);
        pos.setY(-10);
        pos.setZ(-2);
        size.set(128*2, 128*2);

        render.setRenderOriginY(-20);
        timeBeforeBored = 3;
    }

    @Override
    public void loadAssets() {
        raise = assets.getAnimation("octopusRaiseAnimation");
        stun = assets.getAnimation("octopusStunAnimation");
        sink = assets.getAnimation("octopusSinkAnimation");

        stunSound = assets.get("octopusStunSound", Sound.class);
        anim = new AnimationComponent(raise);
        state = HeadState.RAISE;
        add(anim);
    }

    public void sink() {
        anim.setAnimation(sink);
        anim.setAnimationTime(0);
        state = HeadState.OUT;
        stunSound.stop();
    }

    @Override
    public void additionalTick(float delta) {
        super.additionalTick(delta);
        if(bored) {
            if(anim.getAnimationTime() <= 0) remove();
            return;
        }

        if(state == HeadState.OUT) {
            if(sink.isAnimationFinished(anim.getAnimationTime())) {
                remove();
            }
            return;
        }

        if(state == HeadState.IDLE) {
            timeBeforeBored-=delta;
            if(timeBeforeBored <= 0) {
                sinkBored();
            }
        }

        if(state == HeadState.RAISE && eye == null && raise.isAnimationFinished(anim.getAnimationTime())) {
            eye = new Rectangle(pos.getX() + 8 * 2, pos.getY() + 2*(128-81), 31 * 2, 34 * 2);
            state = HeadState.IDLE;
        }

        if(state == HeadState.STUN) {
            timeLeftStunned -= delta;
            if (timeLeftStunned <= 0) {
                sink();
            }
        }
    }

    private void sinkBored() {
        eye = null;
        anim.setAnimation(raise);
        anim.setAnimationTime(raise.getAnimationDuration());
        anim.setAnimationSpeed(-1);
        bored = true;
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
        timeLeftStunned = 2;
        state = HeadState.STUN;
        eye.set(0, 0, 0, 0);
        stunSound.play();
        BossFightScreen.progressPhase();
        if(BossFightScreen.phase == FightPhase.WIN) {
            screen.addEntity(new WhiteFlagArm(batch, shapeRenderer));
        }
    }

    @Override
    public void removed() {
        notifyScreen(Notifications.OCTOPUS_EYE_GONE);
    }

    private enum HeadState {
        RAISE,
        IDLE,
        STUN,
        OUT
    }
}
