package com.redsponge.dbf.bossfight.fight;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.dbf.utils.Constants;
import com.redsponge.redengine.screen.components.Mappers;
import com.redsponge.redengine.screen.components.PositionComponent;

public class GeyserHandler {

    private final BossFightScreen screen;
    private float time;
    private final Sound bubblingSound;
    private float timeUntilGeyser;
    private Island chosenIsland;
    private PooledEffect effect;

    private Sound geyserSound;

    public GeyserHandler(BossFightScreen screen) {
        this.screen = screen;
        bubblingSound = screen.getAssets().get("bubblingSound", Sound.class);
        geyserSound = screen.getAssets().get("geyserSound", Sound.class);
    }

    public void update(float delta, float goTime) {
        if(timeUntilGeyser > 0) {
            updateCurrentGeyser(delta);
        } else {
            time += delta;
            if (time >= goTime) {
                time -= goTime;
                spawnGeyser();
            }
        }
        if(effect != null && effect.isComplete()) {
            effect.free();
            effect = null;
        }
    }

    private void updateCurrentGeyser(float delta) {
        timeUntilGeyser -= delta;
        if(timeUntilGeyser <= 0) {
            activateGeyser();
        }
    }

    private void activateGeyser() {
        chosenIsland.boost();
        bubblingSound.stop();
        chosenIsland = null;
        effect.allowCompletion();
        geyserSound.play(Constants.SOUND_HUB.getValue());
    }

    private void spawnGeyser() {
        timeUntilGeyser = 2;
        chosenIsland = screen.getIslands().random();
        bubblingSound.play(Constants.SOUND_HUB.getValue());

        PositionComponent pos = Mappers.position.get(chosenIsland);
        effect = screen.getParticleManager().lineBubble().spawn(pos.getX(), pos.getY());
    }

    public void cancelGeyser() {
        if(timeUntilGeyser <= 0) return;

        chosenIsland = null;
        timeUntilGeyser = 0;
        bubblingSound.stop();

        effect.allowCompletion();
        effect.free();
    }

}
