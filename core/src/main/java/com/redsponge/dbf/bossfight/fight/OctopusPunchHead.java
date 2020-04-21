package com.redsponge.dbf.bossfight.fight;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.dbf.bossfight.BossFightScreen.FightPhase;
import com.redsponge.dbf.bossfight.Notifications;
import com.redsponge.dbf.utils.Constants;
import com.redsponge.redengine.lighting.LightTextures.Point;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.lighting.PointLight;
import com.redsponge.redengine.screen.INotified;
import com.redsponge.redengine.screen.components.AnimationComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

import java.util.HashMap;

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
    private Sound splashSound;

    private PointLight eyeLights;

    private HashMap<Animation<TextureRegion>, EyePos[]> eyeLightPositions;

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
        if(BossFightScreen.phase == FightPhase.ZERO) {
            timeBeforeBored = Integer.MAX_VALUE;
        }

        eyeLights = new PointLight(0, 0, 64, Point.feathered);
        eyeLights.getColor().set(Color.ORANGE);
        eyeLights.getColor().a = 0.5f;
        eyeLights.getColor().b += 0.2f;
        eyeLights.getColor().g += 0.2f;
        ((BossFightScreen)screen).getLightSystem().addLight(eyeLights, LightType.ADDITIVE);
//        ((BossFightScreen) screen).getParticleManager().spawnSplash((int) pos.getX() + 50, (int) pos.getY() - 10);
    }

    private void performCoveringSplash() {
        for(int i = 70; i <= 150; i += 10) {
            ((BossFightScreen) screen).getParticleManager().wideSplash().spawn( pos.getX() + i, pos.getY() - 20);
        }
        splashSound.play(Constants.SOUND_HUB.getValue() / 2);
    }

    @Override
    public void loadAssets() {
        raise = assets.getAnimation("octopusRaiseAnimation");
        stun = assets.getAnimation("octopusStunAnimation");
        sink = assets.getAnimation("octopusSinkAnimation");
        splashSound = assets.get("bigSplashSound", Sound.class);

        stunSound = assets.get("octopusStunSound", Sound.class);
        anim = new AnimationComponent(raise);
        state = HeadState.RAISE;
        add(anim);

        eyeLightPositions = new HashMap<>();
        eyeLightPositions.put(raise, new EyePos[]{EyePos.NONE, EyePos.NONE, new EyePos(35, 65, 64), new EyePos(45, 125, 96)});
        eyeLightPositions.put(stun, new EyePos[] {new EyePos(48, 115), new EyePos(60, 96), new EyePos(68, 85), new EyePos(78, 70), new EyePos(62, 69), new EyePos(38, 103), new EyePos(29, 124), new EyePos(27, 134), new EyePos(25, 161), new EyePos(28, 153), new EyePos(29, 142), new EyePos(43, 131)});
        eyeLightPositions.put(sink, new EyePos[] {new EyePos(46, 133), new EyePos(46, 206), new EyePos(45, 255), EyePos.NONE});

        performCoveringSplash();
    }

    public void sink() {
        anim.setAnimation(sink);
        anim.setAnimationTime(0);
        state = HeadState.OUT;
        stunSound.stop();
        performCoveringSplash();
    }

    @Override
    public void additionalTick(float delta) {
        EyePos eyePos = eyeLightPositions.get(anim.getAnimation())[anim.getAnimation().getKeyFrameIndex(anim.getAnimationTime())];
        eyeLights.pos.set(pos.getX() + eyePos.x() * render.getScaleX(), pos.getY() + eyePos.y());
        eyeLights.setRadius(eyePos.rad());

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
        performCoveringSplash();
    }

    @Override
    public void additionalRender() {
//        shapeRenderer.setProjectionMatrix(screen.getEntitySystem(RenderSystem.class).getCamera().combined);
//        shapeRenderer.begin(ShapeType.Line);
//        shapeRenderer.setColor(Color.GREEN);
//        if(eye != null) {
//            shapeRenderer.rect(eye.x, eye.y, eye.width, eye.height);
//        }
//        shapeRenderer.end();
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
        stunSound.play(Constants.SOUND_HUB.getValue());
        BossFightScreen.progressPhase();
        notifyScreen(Notifications.CHANGED_PHASE);
        if(BossFightScreen.phase == FightPhase.WIN) {
            screen.addEntity(new WhiteFlagArm(batch, shapeRenderer));
        }
    }

    @Override
    public void removed() {
        notifyScreen(Notifications.OCTOPUS_EYE_GONE);
        ((BossFightScreen)screen).getLightSystem().removeLight(eyeLights, LightType.ADDITIVE);
    }

    private enum HeadState {
        RAISE,
        IDLE,
        STUN,
        OUT
    }
}
