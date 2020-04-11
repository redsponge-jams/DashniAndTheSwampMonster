package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.redsponge.redengine.screen.components.AnimationComponent;
import com.redsponge.redengine.screen.components.Mappers;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.utils.Logger;

public class BubbleAttackArm extends ScreenEntity {

    private Animation<TextureRegion> raiseAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> attackAnimation;

    private int x;
    private AnimationComponent anim;

    private Rectangle ouchBox;
    private float signalTimeCounter;

    private float telegraphTime;
    private float persistenceTime;

    private BubbleAttackPhase phase;
    private float shootTimer;
    private float idleTimer;
    private float attackTime;
    private boolean spawnedBubble;
    private boolean isRemoved;

    private PooledEffect bubbles;

    private float timeBetweenAttacks;

    public BubbleAttackArm(SpriteBatch batch, ShapeRenderer shapeRenderer, int x, float telegraphTime, int persistenceTime, float timeBetweenAttacks) {
        super(batch, shapeRenderer);
        this.x = x;
        this.telegraphTime = telegraphTime;
        this.persistenceTime = persistenceTime;
        this.timeBetweenAttacks = timeBetweenAttacks;
        this.phase = BubbleAttackPhase.SIGNAL;
    }

    @Override
    public void added() {
        pos.set(x, 0);
        size.set(32, 128);
        ouchBox = new Rectangle();
        ouchBox.x = x + 8;
        ouchBox.y = 0;
        ouchBox.width = 16;
        ((BossFightScreen)screen).getAttackBoxes().add(ouchBox);
    }

    @Override
    public void loadAssets() {
        raiseAnimation = assets.getAnimation("octopusAttackBubbleRaiseAnimation");
        idleAnimation = assets.getAnimation("octopusAttackBubbleIdleAnimation");
        attackAnimation = assets.getAnimation("octopusAttackBubbleAttackAnimation");

        bubbles = ((BossFightScreen)screen).getParticleManager().spawnBubbles((int) pos.getX(), (int) pos.getY() + 16);
        anim = new AnimationComponent(raiseAnimation);
    }

    @Override
    public void additionalTick(float delta) {
        render.setFlipX(Mappers.position.get(((BossFightScreen)screen).getPlayer()).getX() > pos.getX());
        if(((BossFightScreen)screen).isHeadUp() && phase != BubbleAttackPhase.OUT) {
            if(phase == BubbleAttackPhase.SIGNAL) {
                remove();
            } else {
                end();
            }
        }

        if(phase == BubbleAttackPhase.SIGNAL) {
            signalTimeCounter += delta;
            if(signalTimeCounter >= telegraphTime) {
                beginRaise();
            }
        } else if(phase == BubbleAttackPhase.RAISE) {
            ouchBox.height = Math.min(1, (anim.getAnimationTime() / raiseAnimation.getAnimationDuration())) * 128;
            if(raiseAnimation.isAnimationFinished(anim.getAnimationTime())) {
                beginIdle();
            }
        } else if(phase == BubbleAttackPhase.IDLE) {
            shootTimer += delta;
            idleTimer += delta;
            if((shootTimer > timeBetweenAttacks)) {
                shootTimer -= timeBetweenAttacks;
                spawnBubble();
            }
            if(idleTimer > persistenceTime) {
                end();
            }
        } else if(phase == BubbleAttackPhase.ATTACK) {
            idleTimer += delta;
            attackTime += delta;
            if(attackTime > .1f && !spawnedBubble) {
                ((BossFightScreen)screen).spawnBubble(x, 96);
                spawnedBubble = true;
            }
            if(attackAnimation.isAnimationFinished(anim.getAnimationTime())) {
                beginIdle();
            }
        } else if(phase == BubbleAttackPhase.OUT) {
            if(anim.getAnimationTime() <= 0) {
                remove();
            }
        } else {
            Logger.error(this, "This really shouldn't happen! phase of side attack became", phase);
        }
    }

    private void spawnBubble() {
        anim.setAnimation(attackAnimation);
        anim.setAnimationTime(0);
        attackTime = 0;
        spawnedBubble = false;
        phase = BubbleAttackPhase.ATTACK;
    }

    private void beginIdle() {
        anim.setAnimation(idleAnimation);
        anim.setAnimationTime(0);
        phase = BubbleAttackPhase.IDLE;
    }

    private void end() {
        anim.setAnimation(raiseAnimation);
        anim.setAnimationTime(raiseAnimation.getAnimationDuration());
        anim.setAnimationSpeed(-1);
        phase = BubbleAttackPhase.OUT;
    }

    private void beginRaise() {
        anim.setAnimation(raiseAnimation);
        anim.setAnimationTime(0);
        phase = BubbleAttackPhase.RAISE;
        add(anim);
        bubbles.allowCompletion();
    }

    @Override
    public void removed() {
        ((BossFightScreen)screen).getAttackBoxes().removeValue(ouchBox, true);
        isRemoved = true;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    private enum BubbleAttackPhase {
        SIGNAL,
        RAISE,
        IDLE,
        ATTACK,
        OUT
    }

}
