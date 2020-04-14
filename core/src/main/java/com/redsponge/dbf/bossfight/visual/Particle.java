package com.redsponge.dbf.bossfight.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Disposable;

public class Particle implements Disposable {

    private final String path;
    private final DelayedRemovalArray<ParticleEffectPool.PooledEffect> effects;
    private final ParticleEffect baseEffect;
    private final ParticleEffectPool pool;

    public Particle(String path) {
        this.path = path;
        effects = new DelayedRemovalArray<>();
        baseEffect = new ParticleEffect();
        baseEffect.load(Gdx.files.internal(path), Gdx.files.internal("particles"));

        pool = new ParticleEffectPool(baseEffect, 16, 128);
    }

    public ParticleEffectPool.PooledEffect spawn(float x, float y) {
        ParticleEffectPool.PooledEffect effect = pool.obtain();
        effect.setPosition(x, y);
        effect.start();
        effects.add(effect);
        return effect;
    }

    public void tick(float delta) {
        for (int i = 0; i < effects.size; i++) {
            ParticleEffectPool.PooledEffect effect = effects.get(i);
            effect.update(delta);
            if(effect.isComplete()) {
                effect.free();
                effects.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < effects.size; i++) {
            effects.get(i).draw(batch);
        }
    }

    @Override
    public void dispose() {
        baseEffect.dispose();
    }

    public void clearAll() {
        for (int i = 0; i < effects.size; i++) {
            effects.get(i).free();
        }
        effects.clear();
    }

    public String getPath() {
        return path;
    }
}
