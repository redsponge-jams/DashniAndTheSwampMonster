package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.redsponge.redengine.screen.components.RenderRunnableComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class ParticleManager extends ScreenEntity {

    private ParticleEffectPool bubblePool;
    private DelayedRemovalArray<PooledEffect> bubbleEffects;

    public ParticleManager(SpriteBatch batch, ShapeRenderer sr) {
        super(batch, sr);
        bubbleEffects = new DelayedRemovalArray<>();
    }

    @Override
    public void added() {
        add(new RenderRunnableComponent(this::render));
    }

    public void loadAssets() {
        ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal("particles/bubbling.p"), Gdx.files.internal("particles"));

        bubblePool = new ParticleEffectPool(effect, 10, 100);
    }

    public PooledEffect spawnBubbles(int x, int y) {
        PooledEffect effect = bubblePool.obtain();
        effect.setPosition(x, y);
        bubbleEffects.add(effect);
        return effect;
    }

    public void additionalTick(float delta) {
        for (int i = 0; i < bubbleEffects.size; i++) {
            bubbleEffects.get(i).update(delta);
            if(bubbleEffects.get(i).isComplete()) {
                bubbleEffects.get(i).free();
                bubbleEffects.removeIndex(i);
            }
        }
    }

    public void render() {
        for (int i = 0; i < bubbleEffects.size; i++) {
            bubbleEffects.get(i).draw(batch);
        }
    }
}
