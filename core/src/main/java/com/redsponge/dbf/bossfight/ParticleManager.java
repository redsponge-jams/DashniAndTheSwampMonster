package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.redsponge.redengine.screen.INotified;
import com.redsponge.redengine.screen.components.RenderRunnableComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class ParticleManager extends ScreenEntity implements INotified {

    private ParticleEffectPool bubblePool;
    private DelayedRemovalArray<PooledEffect> bubbleEffects;

    private ParticleEffectPool intenseBubblePool;
    private DelayedRemovalArray<PooledEffect> intenseBubbleEffects;

    private ParticleEffectPool lineBubblePool;
    private DelayedRemovalArray<PooledEffect> lineBubbleEffects;

    public ParticleManager(SpriteBatch batch, ShapeRenderer sr) {
        super(batch, sr);
        bubbleEffects = new DelayedRemovalArray<>();
        intenseBubbleEffects = new DelayedRemovalArray<>();
        lineBubbleEffects = new DelayedRemovalArray<>();
    }

    @Override
    public void added() {
        add(new RenderRunnableComponent(this::render));
        pos.setZ(5);
    }

    public void loadAssets() {
        ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal("particles/bubbling.p"), Gdx.files.internal("particles"));

        bubblePool = new ParticleEffectPool(effect, 10, 100);

        ParticleEffect intenseEffect = new ParticleEffect();
        intenseEffect.load(Gdx.files.internal("particles/bubbling_tense.p"), Gdx.files.internal("particles"));

        intenseBubblePool = new ParticleEffectPool(intenseEffect, 10, 100);


        ParticleEffect lineEffect = new ParticleEffect();
        lineEffect.load(Gdx.files.internal("particles/bubbling_line.p"), Gdx.files.internal("particles"));

        lineBubblePool = new ParticleEffectPool(lineEffect, 10, 100);
    }

    public PooledEffect spawnBubbles(int x, int y) {
        PooledEffect effect = bubblePool.obtain();
        effect.setPosition(x, y);
        bubbleEffects.add(effect);
        return effect;
    }

    public PooledEffect spawnIntenseBubbles(int x, int y) {
        PooledEffect effect = intenseBubblePool.obtain();
        effect.setPosition(x, y);
        intenseBubbleEffects.add(effect);
        return effect;
    }

    public PooledEffect spawnLineBubbles(int x, int y) {
        PooledEffect effect = lineBubblePool.obtain();
        effect.setPosition(x, y);
        lineBubbleEffects.add(effect);
        return effect;
    }

    private void updateEffect(DelayedRemovalArray<PooledEffect> effects, float delta) {
        for (int i = 0; i < effects.size; i++) {
            effects.get(i).update(delta);
            if(effects.get(i).isComplete()) {
                effects.get(i).free();
                effects.removeIndex(i);
            }
        }
    }

    public void additionalTick(float delta) {
        updateEffect(bubbleEffects, delta);
        updateEffect(intenseBubbleEffects, delta);
        updateEffect(lineBubbleEffects, delta);
    }

    private void drawEffect(DelayedRemovalArray<PooledEffect> effects) {
        for (int i = 0; i < effects.size; i++) {
            effects.get(i).draw(batch);
        }
    }

    public void render() {
        drawEffect(bubbleEffects);
        drawEffect(intenseBubbleEffects);
        drawEffect(lineBubbleEffects);
    }

    @Override
    public void notified(Object o, int i) {
        if(Notifications.TARGET_OCTOPUS_DOWN == i) {
            removeAllSpawned(bubbleEffects);
            removeAllSpawned(intenseBubbleEffects);
            removeAllSpawned(lineBubbleEffects);
        }
    }

    private void removeAllSpawned(DelayedRemovalArray<PooledEffect> effects) {
        for (int i = 0; i < effects.size; i++) {
            effects.get(i).free();
        }
        effects.clear();
    }
}
