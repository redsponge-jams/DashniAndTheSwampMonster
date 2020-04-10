package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.dbf.constants.Constants;
import com.redsponge.redengine.lighting.Light;
import com.redsponge.redengine.lighting.LightTextures.Point;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.lighting.PointLight;
import com.redsponge.redengine.screen.INotified;
import com.redsponge.redengine.screen.components.AnimationComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.utils.IntVector2;
import com.redsponge.redengine.utils.Logger;
import com.redsponge.redengine.utils.MathUtilities;

public class TargetOctopus extends ScreenEntity implements INotified {

    private static final Color stunColor = new Color(1, 1, 1, 0.3f);

    private AnimationComponent anim;

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private Animation<TextureRegion> sleepAnimation;
    private Animation<TextureRegion> flagAnimation;

    private IntVector2 targetLoc;
    private Vector2 singularVel;

    private int hitsLeft;
    private Rectangle self;
    private float timeExists;

    private float hurtTime;

    private Sound ouchSound;
    private boolean done;

    private PointLight light;
    private PointLight mulLight;


    public TargetOctopus(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
        targetLoc = new IntVector2();
        singularVel = new Vector2();
        singularVel.set(1, 1);
        self = new Rectangle();
        hitsLeft = BossFightScreen.phase.getOctopusLife();
    }

    @Override
    public void added() {
        pos.set(screen.getScreenWidth() / 2f - 24, screen.getScreenHeight() / 3);
        size.set(48, 48);
        render.setUseRegH(true).setUseRegW(true).setScaleX(2).setScaleY(2).setOffsetX(-8).setOffsetY(-8);
        light = new PointLight(pos.getX(), pos.getY(), 96, Point.star);
        light.getColor().set(0xAAFFFF55);

        mulLight = new PointLight(pos.getX(), pos.getY(), 96, Point.feathered);

        ((BossFightScreen)screen).getLightSystem().addLight(light, LightType.ADDITIVE);
        ((BossFightScreen)screen).getLightSystem().addLight(mulLight, LightType.MULTIPLICATIVE);
        generateTarget();
    }

    private void generateTarget() {
        targetLoc.set(BossFightScreen.phase.createOctopusPoint());
    }

    @Override
    public void additionalTick(float delta) {
        timeExists += delta;
        if(Vector2.dst2(pos.getX() + size.getX() / 2f, pos.getY() + size.getY() / 2f, targetLoc.x, targetLoc.y) < 1000) {
            generateTarget();
        }
        if(hurtTime > 0) {
            hurtTime -= delta;
            render.getColor().lerp(stunColor, 0.1f);
            if(hurtTime <= 0) {
                anim.setAnimation(idleAnimation);
                anim.setAnimationTime(0);
            }
            vel.set(0, 0);
        }
        else if(hitsLeft == 0) {
            render.setRotation(MathUtilities.lerp(render.getRotation(), 0, 0.3f));
            pos.setX(MathUtilities.lerp(pos.getX(), screen.getScreenWidth() / 2f - size.getX() / 2f, 0.2f));
            pos.setY(MathUtilities.lerp(pos.getY(), screen.getScreenHeight() / 2f - size.getY() / 2f + 100, 0.2f));
            vel.set(0, 0);
            light.getColor().set(Color.CLEAR);
            mulLight.getColor().set(Color.CLEAR);
            render.getColor().lerp(stunColor, 0.1f);
        } else {
            anim.setAnimationSpeed(1);
            render.setRotation(MathUtilities.lerp(render.getRotation(), (float) Math.sin(3 * timeExists) * 10, 0.1f));
            render.getColor().a = MathUtils.map(-1, 1, 0.4f, 0.9f, (float) Math.sin(timeExists));

            float diffX = targetLoc.x - (pos.getX() + size.getX() / 2f);
            float diffY = targetLoc.y - (pos.getY() + size.getY() / 2f);

            float angle = (float) Math.atan2(diffY, diffX);
            float wantedVX = (float) Math.cos(angle);
            float wantedVY = (float) Math.sin(angle);

            vel.setX(MathUtilities.lerp(vel.getX(), wantedVX * BossFightScreen.phase.getOctopusSpeed(), BossFightScreen.phase.getOctopusLerpPower()));
            vel.setY(MathUtilities.lerp(vel.getY(), wantedVY * BossFightScreen.phase.getOctopusSpeed(), BossFightScreen.phase.getOctopusLerpPower()));
            self.set(pos.getX(), pos.getY(), size.getX(), size.getY());
            light.getColor().set(0xAAFFFF55);
            mulLight.getColor().set(Color.WHITE);
        }
        if(done) {
            vel.set(0, 0);
        }
        light.pos.set(pos.getX() + size.getX() / 2f, pos.getY() + size.getY() / 2f);
        mulLight.pos.set(pos.getX() + size.getX() / 2f, pos.getY() + size.getY() / 2f);
    }

    @Override
    public void additionalRender() {
//        shapeRenderer.setProjectionMatrix(screen.getEntitySystem(RenderSystem.class).getCamera().combined);
//        shapeRenderer.begin(ShapeType.Line);
//        shapeRenderer.setColor(Color.BLUE);
//        shapeRenderer.rect(pos.getX(), pos.getY(), size.getX(), size.getY());
//        shapeRenderer.setColor(Color.ORANGE);
//        shapeRenderer.circle(targetLoc.x, targetLoc.y, 10);
//        shapeRenderer.end();
    }

    @Override
    public void loadAssets() {
        idleAnimation = assets.getAnimation("targetIdleAnimation");
        hurtAnimation = assets.getAnimation("targetHurtAnimation");
        sleepAnimation = assets.getAnimation("targetSleepAnimation");
        flagAnimation = assets.getAnimation("targetFlagAnimation");
        anim = new AnimationComponent(sleepAnimation);
        ouchSound = assets.get("ghostOuchSound", Sound.class);
        add(anim);
    }

    @Override
    public void removed() {
        ((BossFightScreen)screen).getLightSystem().removeLight(light, LightType.ADDITIVE);
        ((BossFightScreen)screen).getLightSystem().removeLight(mulLight, LightType.MULTIPLICATIVE);
    }

    @Override
    public void notified(Object o, int i) {
        if(done) return;
        if(i == Notifications.PLAYER_ATTACK_BOX_SPAWNED && hitsLeft > 0 && hurtTime <= 0) {
            Rectangle attack = ((DashniPlayer)o).getAttackBox();
            if(attack.overlaps(self)) {
                attacked();
            }
        } else if(hitsLeft == 0 && i == Notifications.OCTOPUS_EYE_GONE) {
            hitsLeft = BossFightScreen.phase.getOctopusLife();
            if(((BossFightScreen)screen).isEasy()) {
                if(hitsLeft == 3) {
                    hitsLeft = 2;
                } else if(hitsLeft == 5) {
                    hitsLeft = 3;
                }
            }
            generateTarget();
            anim.setAnimationSpeed(1);
            anim.setAnimation(idleAnimation);
            anim.setAnimationTime(0);
        } else if(i == Notifications.RAISED_FLAG) {
            anim.setAnimation(flagAnimation);
            done = true;
        }
    }

    private void attacked() {
        Logger.log(this, "OUCHY WOWCHI");
        hitsLeft--;
        ouchSound.play(Constants.SOUND_HUB.getValue());
        if(hitsLeft == 0) {
            notifyScreen(Notifications.TARGET_OCTOPUS_DOWN);
            anim.setAnimation(hurtAnimation);
            anim.setAnimationTime(0.3f);
            anim.setAnimationSpeed(0);
        } else {
            hurtTime = hurtAnimation.getAnimationDuration();
            anim.setAnimation(hurtAnimation);
            anim.setAnimationTime(3);
            render.getColor().set(1, 0.2f, 0.2f, 0.5f);
        }
    }
}
