package com.redsponge.dbf.bossfight.attacks;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.dbf.bossfight.Notifications;
import com.redsponge.dbf.utils.Constants;
import com.redsponge.redengine.physics.PSolid;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.screen.INotified;
import com.redsponge.redengine.screen.components.AnimationComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.screen.systems.PhysicsSystem;
import com.redsponge.redengine.utils.Logger;

public class SideAttack extends ScreenEntity implements INotified {

    private Animation<TextureRegion> signalAnimation;
    private Animation<TextureRegion> attackAnimation;

    private int y;

    private Sound whipSound;

    private AnimationComponent anim;
    private SideAttackPhase phase;
    private float attackLingerTime;

    private PSolid landablePart;
    private PhysicsWorld pWorld;

    private Rectangle ouchBox;
    private float signalTimeCounter;

    private boolean comingFromLeft;
    private int length;
    private float telegraphTime;
    private float persistenceTime;

    private float attackSpeed;
    private boolean didPlayWhipSound;

    public SideAttack(SpriteBatch batch, ShapeRenderer shapeRenderer, int y, boolean comingFromRight, int length, float telegraphTime, float persistenceTime, float attackSpeed) {
        super(batch, shapeRenderer);
        this.y = y;
        this.comingFromLeft = comingFromRight;
        this.length = length;
        this.telegraphTime = telegraphTime;
        this.persistenceTime = persistenceTime;
        this.attackSpeed = attackSpeed;
        this.phase = SideAttackPhase.SIGNAL;
    }

    @Override
    public void added() {
        pos.set(0, y);
        size.set(640, 32);
        render.setFlipX(comingFromLeft);
        pWorld = screen.getEntitySystem(PhysicsSystem.class).getPhysicsWorld();
        landablePart = new PSolid(pWorld);
        landablePart.size.set(640, 6);
        landablePart.setPhysicsBodyTag("Slippery");
        ouchBox = new Rectangle();
        ((BossFightScreen)screen).getAttackBoxes().add(ouchBox);
    }

    @Override
    public void loadAssets() {
        signalAnimation = assets.getAnimation("octopusAttackSideSignalAnimation");
        attackAnimation = assets.getAnimation("octopusAttackSideAttackAnimation");

        anim = new AnimationComponent(signalAnimation);
        add(anim);

        whipSound = assets.get("whipSound", Sound.class);
    }

    @Override
    public void additionalTick(float delta) {
        if(((BossFightScreen)screen).isHeadUp() && phase != SideAttackPhase.OUT) {
            if(phase == SideAttackPhase.SIGNAL) {
                remove();
            } else {
                end();
            }
        }

        if(phase == SideAttackPhase.SIGNAL) {
            signalTimeCounter += delta;
            if(signalAnimation.isAnimationFinished(signalTimeCounter)) {
                beginAttack();
            }
        } else if(phase == SideAttackPhase.ATTACK) {

            if(comingFromLeft) {
                moveStandBoxTo(length * anim.getAnimationTime() / attackAnimation.getAnimationDuration() - 640, pos.getY() + 12);
            } else {
                moveStandBoxTo(640 - length * anim.getAnimationTime() / attackAnimation.getAnimationDuration(), pos.getY() + 12);
            }
            if(anim.getAnimation().getKeyFrameIndex(anim.getAnimationTime()) == 4 && !didPlayWhipSound) {
                didPlayWhipSound = true;
                whipSound.play(Constants.SOUND_HUB.getValue());
            }

            if(attackAnimation.isAnimationFinished(anim.getAnimationTime())) {
                attackLingerTime = persistenceTime;
                phase = SideAttackPhase.WAIT;
                if(comingFromLeft) {
                    moveStandBoxTo(length - 640, pos.getY() + 12);
                } else {
                    moveStandBoxTo(640 - length, pos.getY() + 12);
                }
            }
            ouchBox.set(landablePart.pos.x - 3, landablePart.pos.y - 3, landablePart.size.x + 6, landablePart.size.y);
        } else if(phase == SideAttackPhase.WAIT) {
             attackLingerTime -= delta;
             if(attackLingerTime <= 0) {
                 end();
             }
        } else if(phase == SideAttackPhase.OUT) {

            if(comingFromLeft) {
                moveStandBoxTo(length * anim.getAnimationTime() / attackAnimation.getAnimationDuration() - 640, pos.getY() + 12);
            } else {
                moveStandBoxTo(640 - length * anim.getAnimationTime() / attackAnimation.getAnimationDuration(), pos.getY() + 12);
            }

            ouchBox.set(landablePart.pos.x - 3, landablePart.pos.y - 3, landablePart.size.x + 6, landablePart.size.y);

            if(anim.getAnimationTime() <= 0) {
                remove();
            }
        } else {
            Logger.error(this, "This really shouldn't happen! phase of side attack became", phase);
        }
    }

    private void moveStandBoxTo(float x, float y) {
        landablePart.move( x - landablePart.pos.x, y - landablePart.pos.y);
    }

    private void end() {
        anim.setAnimation(attackAnimation);
        anim.setAnimationTime(attackAnimation.getAnimationDuration());
        anim.setAnimationSpeed(-1);
        phase = SideAttackPhase.OUT;
    }

    private void beginAttack() {
        if(comingFromLeft) {
            pos.set(-640 + length + 50, y);
            landablePart.pos.set(-999999, y);
        } else {
            pos.set(640 - length - 50, y);
            landablePart.pos.set(-999999, y);
        }
        anim.setAnimation(attackAnimation);
        anim.setAnimationTime(0);
        anim.setAnimationSpeed(attackSpeed);
        phase = SideAttackPhase.ATTACK;
        pWorld.addSolid(landablePart);
    }

    @Override
    public void removed() {
        pWorld.getSolids().removeValue(landablePart, true);
        ((BossFightScreen)screen).getAttackBoxes().removeValue(ouchBox, true);
    }

    @Override
    public void notified(Object o, int i) {
        if(i == Notifications.TARGET_OCTOPUS_DOWN) {
            if(phase == SideAttackPhase.SIGNAL) {
                remove();
            } else {
                end();
            }
        }
    }

    private enum SideAttackPhase {
        SIGNAL,
        ATTACK,
        WAIT,
        OUT
    }
}
