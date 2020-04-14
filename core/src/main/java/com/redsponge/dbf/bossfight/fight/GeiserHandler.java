package com.redsponge.dbf.bossfight.fight;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.dbf.utils.Constants;
import com.redsponge.redengine.screen.components.Mappers;
import com.redsponge.redengine.screen.components.PositionComponent;

public class GeiserHandler {

    private final BossFightScreen screen;
    private float time;
    private final Sound bubblingSound;
    private float timeUntilGeiser;
    private Island chosenIsland;
    private PooledEffect effect;

    public GeiserHandler(BossFightScreen screen) {
        this.screen = screen;
        bubblingSound = screen.getAssets().get("bubblingSound", Sound.class);
    }

    public void update(float delta, float goTime) {
        if(timeUntilGeiser > 0) {
            updateCurrentGeiser(delta);
        } else {
            time += delta;
            if (time >= goTime) {
                time -= goTime;
                spawnGeiser();
            }
        }
        if(effect != null && effect.isComplete()) {
            effect.free();
            effect = null;
        }
    }

    private void updateCurrentGeiser(float delta) {
        timeUntilGeiser -= delta;
        if(timeUntilGeiser <= 0) {
            activateGeiser();
        }
    }

    private void activateGeiser() {
        chosenIsland.boost();
        bubblingSound.stop();
        chosenIsland = null;
        effect.allowCompletion();
    }

    private void spawnGeiser() {
        timeUntilGeiser = 2;
        chosenIsland = screen.getIslands().random();
        bubblingSound.play(Constants.SOUND_HUB.getValue());

        PositionComponent pos = Mappers.position.get(chosenIsland);
        effect = screen.getParticleManager().spawnLineBubbles((int) pos.getX(), (int) pos.getY());
    }

    public void cancelGeiser() {
        if(timeUntilGeiser <= 0) return;

        chosenIsland = null;
        timeUntilGeiser = 0;
        bubblingSound.stop();

        effect.allowCompletion();
        effect.free();
    }

}
