package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.redsponge.dbf.input.InputUtil;
import com.redsponge.redengine.physics.PBodyType;
import com.redsponge.redengine.physics.PEntity;
import com.redsponge.redengine.screen.components.AnimationComponent;
import com.redsponge.redengine.screen.components.PhysicsComponent;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.utils.IntVector2;
import com.redsponge.redengine.utils.Logger;
import com.redsponge.redengine.utils.MathUtilities;

public class DashniPlayer extends ScreenEntity {

    private TextureComponent tex;
    private PhysicsComponent physics;
    private float gravity = -600;
    private float horizAccelPerSecond = 320;
    private float maxSpeed = 140;
    private float frictionMultiplier = 0.85f;
    private float horizStartBoostMultiplier = 30;

    private float jumpVelocity = 320;
    private float fallMultiplier = 3f;
    private float lowJumpMultiplier = 4f;

    private AnimationComponent anim;

    private Animation<TextureRegion> idleAnimation;

    private IntVector2 tmpA, tmpB;

    private boolean lookingLeft;

    public DashniPlayer(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        size.set(32, 32);
        pos.set(screen.getScreenWidth() / 2f - size.getX() / 2f, screen.getScreenHeight() / 2f - size.getY() / 2f);
        render.setUseRegW(true).setUseRegH(true);
        physics = new PhysicsComponent(PBodyType.ACTOR);
        physics.setOnCollideY(this::onYCollide);
        add(physics);

        tmpA = new IntVector2();
        tmpB = new IntVector2();
    }

    @Override
    public void loadAssets() {
        idleAnimation = assets.getAnimation("playerIdleAnimation");
        anim = new AnimationComponent(idleAnimation);
        add(anim);
    }

    private void updateStrafing(float delta) {
        float horiz = InputUtil.getHorizontal();
        if(horiz != 0) {
            lookingLeft = horiz < 0;

            if(Math.abs(vel.getX()) < Math.abs(horiz * horizAccelPerSecond * delta * horizStartBoostMultiplier) || Math.signum(vel.getX()) != horiz) {
                vel.setX(horiz * horizAccelPerSecond * delta * horizStartBoostMultiplier);
            } else {
                vel.setX(vel.getX() + horiz * horizAccelPerSecond * delta);
            }

            if (Math.abs(vel.getX()) > maxSpeed) {
                vel.setX(maxSpeed * Math.signum(vel.getX()));
            }
        } else {
            vel.setX(vel.getX() * frictionMultiplier);
            if(Math.abs(vel.getX()) < 0.5f) {
                vel.setX(0);
            }
        }
    }

    @Override
    public void additionalTick(float delta) {
        updateStrafing(delta);
        if(InputUtil.isJustJumping()) {
            vel.setY(jumpVelocity);
        }
        updateBetterJump(delta);
        vel.setY(vel.getY() + gravity * delta);

        render.setFlipX(lookingLeft);
        Array<Rectangle> attackBoxes = ((BossFightScreen)screen).getAttackBoxes();
        for (int i = 0; i < attackBoxes.size; i++) {
            tmpA.set((int) attackBoxes.get(i).x, (int) attackBoxes.get(i).y);
            tmpB.set((int) attackBoxes.get(i).width, (int) attackBoxes.get(i).height);
            if(MathUtilities.rectanglesIntersect(physics.getBody().pos, physics.getBody().size, tmpA, tmpB)) {
                die();
            }
        }
        if(pos.getY() < 0) {
            die();
            pos.set(100, 100);
        }
    }

    private void die() {
        Logger.log(this, "F");
    }

    private void updateBetterJump(float delta) {
        if(vel.getY() < 0) {
            vel.setY(vel.getY() + gravity * (fallMultiplier - 1) * delta);
        } else if(vel.getY() > 0 && !InputUtil.isJumping()) {
            vel.setY(vel.getY() + gravity * (lowJumpMultiplier - 1) * delta);
        }

    }

    private void onYCollide(PEntity other) {
        vel.setY(0);
    }
}
