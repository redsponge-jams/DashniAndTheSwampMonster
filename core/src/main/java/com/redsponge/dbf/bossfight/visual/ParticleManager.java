package com.redsponge.dbf.bossfight.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Disposable;
import com.redsponge.dbf.bossfight.Notifications;
import com.redsponge.redengine.screen.INotified;
import com.redsponge.redengine.screen.components.RenderRunnableComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class ParticleManager extends ScreenEntity implements INotified, Disposable {

    private final Particle bubble;
    private final Particle intenseBubble;
    private final Particle lineBubble;
    private final Particle wideSplash;
    private final Particle thinSplash;
    private final Particle tinySplash;
    private final Particle mediumSplash;
    private final Particle geyser;

    private final Particle[] particles;

    public Particle geyser() {
        return geyser;
    }

    public ParticleManager(SpriteBatch batch, ShapeRenderer sr) {
        super(batch, sr);
        bubble = new Particle("particles/bubbling.p");
        intenseBubble = new Particle("particles/bubbling_tense.p");
        lineBubble = new Particle("particles/bubbling_line.p");
        wideSplash = new Particle("particles/splash.p");
        thinSplash = new Particle("particles/splash_small.p");
        mediumSplash = new Particle("particles/splash_medium.p");
        geyser = new Particle("particles/geyser.p");
        tinySplash = new Particle("particles/splash_tiny.p");

        particles = new Particle[] {
                bubble,
                intenseBubble,
                lineBubble,
                wideSplash,
                thinSplash,
                mediumSplash,
                geyser,
                tinySplash
        };
    }

    @Override
    public void added() {
        add(new RenderRunnableComponent(this::render));
        pos.setZ(5);
    }

    public void additionalTick(float delta) {
        for (int i = 0; i < particles.length; i++) {
            particles[i].tick(delta);
        }
    }

    public void render() {
        for (int i = 0; i < particles.length; i++) {
            particles[i].render(batch);
        }
    }

    @Override
    public void notified(Object o, int notification) {
        if(Notifications.TARGET_OCTOPUS_DOWN == notification) {
            for (Particle particle : particles) {
                if(!particle.getPath().contains("splash")) {
                    particle.clearAll();
                }
            }
        }
    }

    @Override
    public void dispose() {
        for (Particle particle : particles) {
            particle.dispose();
        }
    }

    public Particle bubble() {
        return bubble;
    }

    public Particle intenseBubble() {
        return intenseBubble;
    }

    public Particle lineBubble() {
        return lineBubble;
    }

    public Particle wideSplash() {
        return wideSplash;
    }

    public Particle thinSplash() {
        return thinSplash;
    }

    public Particle mediumSplash() {
        return mediumSplash;
    }

    public Particle tinySplash() {
        return tinySplash;
    }
}
