package com.redsponge.dbf.bossfight.fight;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.dbf.bossfight.Notifications;
import com.redsponge.redengine.screen.components.AnimationComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.utils.Logger;

public class WhiteFlagArm extends ScreenEntity {

    private Animation<TextureRegion> raiseAnimation;
    private Animation<TextureRegion> idleAnimation;
    private AnimationComponent anim;

    private PooledEffect bubbles;
    private float time = 0;
    private boolean didRaise;

    public WhiteFlagArm(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        super.added();
        pos.set(50, 0);
        size.set(48 * 2, 128 * 2);
        bubbles = ((BossFightScreen)screen).getParticleManager().spawnIntenseBubbles(bubbleX , 0);


        pos.set(-5000,0);
    }

    int bubbleX = (int) (50 + 48);

    @Override
    public void loadAssets() {
        idleAnimation = assets.getAnimation("octopusWhiteFlagAnimation");
        raiseAnimation = assets.getAnimation("octopusWhiteFlagRaiseAnimation");
        anim = new AnimationComponent(idleAnimation);
        add(anim);
    }

    @Override
    public void additionalTick(float delta) {
        time += delta;
        Logger.log(this, time);
        if(time > 8f && bubbles != null && !didRaise) {
            anim.setAnimation(raiseAnimation);
            anim.setAnimationTime(0);
            bubbles = null;
            pos.set(50, 0);
            notifyScreen(Notifications.RAISED_FLAG);
            didRaise = true;
        }
        if(didRaise && anim.getAnimation() == raiseAnimation && raiseAnimation.isAnimationFinished(anim.getAnimationTime())) {
            anim.setAnimation(idleAnimation);
            anim.setAnimationTime(0);
        }
    }
}
