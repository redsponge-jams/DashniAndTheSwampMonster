package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.redsponge.redengine.physics.PSolid;
import com.redsponge.redengine.physics.PhysicsWorld;
import com.redsponge.redengine.screen.components.AnimationComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.screen.systems.PhysicsSystem;
import com.redsponge.redengine.screen.systems.RenderSystem;
import com.redsponge.redengine.utils.Logger;

import javax.swing.JViewport;

public class SideAttack extends ScreenEntity {

    private Animation<TextureRegion> signalAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> endAnimation;

    private int y;
    private AnimationComponent anim;
    private SideAttackPhase phase;
    private float attackLingerTime;

    private PSolid landablePart;
    private PhysicsWorld pWorld;

    private Rectangle ouchBox;

    public SideAttack(SpriteBatch batch, ShapeRenderer shapeRenderer, int y) {
        super(batch, shapeRenderer);
        this.y = y;
        phase = SideAttackPhase.SIGNAL;
    }

    @Override
    public void added() {
        pos.set(0, y);
        size.set(640, 32);
        pWorld = screen.getEntitySystem(PhysicsSystem.class).getPhysicsWorld();
        landablePart = new PSolid(pWorld);
        landablePart.pos.set(640, y + 12);
        landablePart.size.set(640, 6);
        ouchBox = new Rectangle();
        ((BossFightScreen)screen).getAttackBoxes().add(ouchBox);
    }

    @Override
    public void loadAssets() {
        signalAnimation = assets.getAnimation("octopusAttackSideSignalAnimation");
        attackAnimation = assets.getAnimation("octopusAttackSideAttackAnimation");
        endAnimation = assets.getAnimation("octopusAttackSideEndAnimation");

        anim = new AnimationComponent(signalAnimation);
        add(anim);
    }

    @Override
    public void additionalTick(float delta) {
        if(phase == SideAttackPhase.SIGNAL) {
            if(signalAnimation.isAnimationFinished(anim.getAnimationTime())) {
                beginAttack();
            }
        } else if(phase == SideAttackPhase.ATTACK) {
            if(attackAnimation.isAnimationFinished(anim.getAnimationTime())) {
                attackLingerTime = 1;
                phase = SideAttackPhase.WAIT;
            }
            landablePart.move((-600 / (attackAnimation.getAnimationDuration())) * delta, 0);
            ouchBox.set(landablePart.pos.x - 3, landablePart.pos.y - 3, landablePart.size.x + 6, landablePart.size.y);
            Logger.log(this, landablePart.pos, landablePart.size);
        } else if(phase == SideAttackPhase.WAIT) {
             attackLingerTime -= delta;
             if(attackLingerTime <= 0) {
                 end();
             }
        } else if(phase == SideAttackPhase.OUT) {
            ouchBox.set(landablePart.pos.x - 3, landablePart.pos.y - 3, landablePart.size.x + 6, landablePart.size.y);
            landablePart.move((600 / (endAnimation.getAnimationDuration())) * delta, 0);
            if(endAnimation.isAnimationFinished(anim.getAnimationTime())) {
                remove();
            }
        } else {
            Logger.error(this, "This really shouldn't happen! phase of side attack became", phase);
        }
    }

    private void end() {
        anim.setAnimation(endAnimation);
        anim.setAnimationTime(0);
        phase = SideAttackPhase.OUT;
    }

    private void beginAttack() {
        anim.setAnimation(attackAnimation);
        anim.setAnimationTime(0);
        phase = SideAttackPhase.ATTACK;
        pWorld.addSolid(landablePart);
    }

    @Override
    public void removed() {
        pWorld.getSolids().removeValue(landablePart, true);
        ((BossFightScreen)screen).getAttackBoxes().removeValue(ouchBox, true);
    }

    private enum SideAttackPhase {
        SIGNAL,
        ATTACK,
        WAIT,
        OUT
    }
}
