package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.redsponge.dbf.constants.Constants;
import com.redsponge.dbf.input.Input;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PBodyType;
import com.redsponge.redengine.physics.PEntity;
import com.redsponge.redengine.screen.components.AnimationComponent;
import com.redsponge.redengine.screen.components.PhysicsComponent;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.screen.systems.RenderSystem;
import com.redsponge.redengine.utils.IntVector2;
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

    private Sound attackSound;

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> attackUpAnimation;
    private Animation<TextureRegion> attackDownAnimation;
    private Animation<TextureRegion> deathAnimation;

    private float attackTime;
    private float attackCooldown;
    private boolean createdAttackBox;

    private Rectangle attackBox;

    private AnimationComponent anim;

    private IntVector2 tmpA, tmpB;

    private boolean lookingLeft;
    private boolean isAttacking;
    private AttackType attackType;

    private int jumpsLeft;
    private boolean dead;
    private boolean locked;

    public DashniPlayer(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        size.set(18, 32);
        render.setOffsetX(-6);
        pos.set(screen.getScreenWidth() / 2f - size.getX() / 2f, screen.getScreenHeight() / 2f - size.getY() / 2f);
        render.setUseRegW(true).setUseRegH(true);
        physics = new PhysicsComponent(PBodyType.ACTOR);
        add(physics);

        tmpA = new IntVector2();
        tmpB = new IntVector2();
    }

    @Override
    public void addedToEngine() {
        physics.setOnCollideY(this::onYCollide);
        ((PActor)physics.getBody()).setRidingCheck((self, solid) -> {
            if(solid.getPhysicsBodyTag().equals("Slippery")) {
                return false;
            }
            return PActor.defaultRidingCheck.isRiding(self, solid);
        });
    }

    @Override
    public void loadAssets() {
        this.idleAnimation = assets.getAnimation("playerIdleAnimation");
        this.runAnimation = assets.getAnimation("playerRunAnimation");
        this.attackAnimation = assets.getAnimation("playerAttackHorizAnimation");
        this.attackUpAnimation = assets.getAnimation("playerAttackUpAnimation");
        this.attackDownAnimation = assets.getAnimation("playerAttackDownAnimation");
        this.deathAnimation = assets.getAnimation("playerDieAnimation");

        anim = new AnimationComponent(idleAnimation);
        attackSound = assets.get("dashniAttackSound", Sound.class);
        add(anim);
    }

    private void updateStrafing(float delta) {
        float horiz = Input.getHorizontal();
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
        if(locked) {
            vel.set(0, 0);
            lookingLeft = true;
            return;
        }
        if(dead) {
            vel.set(0, 0);
            return;
        }
        if(isAttacking) {
            processAttack(delta);
        }
        else {
            attackCooldown -= delta;
            if(Input.isJustJumping() && canJump()) {
                vel.setY(jumpVelocity);
                jumpsLeft--;
            }
            if(Input.isJustAttacking() && !isAttacking && attackCooldown <= 0) {
                beginAttacking();
                processAttack(delta);
                return;
            }

            updateStrafing(delta);
            updateBetterJump(delta);
            vel.setY(vel.getY() + gravity * delta);

            render.setFlipX(lookingLeft);
            if(Input.getHorizontal() == 0) {
                anim.setAnimation(idleAnimation);
            } else {
                anim.setAnimation(runAnimation);
            }
        }



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
        }
    }

    private boolean canJump() {
        return jumpsLeft > 0;
    }

    @Override
    public void additionalRender() {
        shapeRenderer.setProjectionMatrix(screen.getEntitySystem(RenderSystem.class).getCamera().combined);
        shapeRenderer.begin(ShapeType.Line);
        if(attackBox != null) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(attackBox.x, attackBox.y, attackBox.width, attackBox.height);
        }
        shapeRenderer.end();
    }

    private void beginAttacking() {
        float vert = Input.getVertical();
        attackSound.play(Constants.SOUND_HUB.getValue());
        if(vert > 0) {
            attackType = AttackType.UP;
            anim.setAnimation(attackUpAnimation);
        } else if(vert < 0) {
            attackType = AttackType.DOWN;
            anim.setAnimation(attackDownAnimation);
            render.setOffsetY(-86);
        } else {
            attackType = AttackType.REGULAR;
            anim.setAnimation(attackAnimation);
        }
        isAttacking = true;
        anim.setAnimationTime(0);
        attackTime = 0;
    }

    private void processAttack(float delta) {
        attackTime += delta;
        vel.set(0, 0);
        if(attackTime > 0.2f && attackBox == null) {
            createAttackBox();
            notifyScreen(Notifications.PLAYER_ATTACK_BOX_SPAWNED);
        }
        if(attackType == AttackType.REGULAR && lookingLeft) {
            render.setOffsetX(-96-8);
        }
        if(attackType == AttackType.DOWN) {
            if(lookingLeft) {
                render.setOffsetX(-16);
            }
        }
        if(attackType == AttackType.UP && lookingLeft) {
            render.setOffsetX(-18);
        }
        if(attackAnimation.isAnimationFinished(attackTime)) {
            endAttack();
        }
    }

    private void createAttackBox() {
        switch (attackType) {
            case UP: {
                attackBox = new Rectangle(pos.getX(), pos.getY() + size.getY(), 24, 90);
            } break;
            case DOWN: {
                attackBox = new Rectangle(pos.getX() + 8, pos.getY() - 72, 16, 80);
            } break;
            case REGULAR: {
                if(lookingLeft) {
                    attackBox = new Rectangle(pos.getX() - 80, pos.getY() + 16, 80, 32-6);
                } else {
                    attackBox = new Rectangle(pos.getX() + size.getX(), pos.getY() + 16, 80, 32-6);
                }
            } break;
        }
    }

    private void endAttack() {
        isAttacking = false;
        anim.setAnimation(idleAnimation);
        attackCooldown = .3f;
        render.setOffsetX(-6).setOffsetY(0);
        attackBox = null;
    }

    private void die() {
        notifyScreen(Notifications.DASHNI_DEAD);
        dead = true;
        anim.setAnimation(deathAnimation);
        anim.setAnimationTime(0);
    }

    private void updateBetterJump(float delta) {
        if(vel.getY() < 0) {
            vel.setY(vel.getY() + gravity * (fallMultiplier - 1) * delta);
        } else if(vel.getY() > 0 && !Input.isJumping()) {
            vel.setY(vel.getY() + gravity * (lowJumpMultiplier - 1) * delta);
        }

    }

    private void onYCollide(PEntity other) {
        if(vel.getY() < 0) {
            jumpsLeft = 1;
            if(BossFightScreen.phase == BossFightScreen.FightPhase.WIN) {
                locked = true;
            }
        }
        vel.setY(0);
    }

    public Rectangle getAttackBox() {
        return attackBox;
    }

    private enum AttackType {
        UP,
        DOWN,
        REGULAR
    }
}
