package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.redsponge.dbf.constants.Constants;
import com.redsponge.dbf.notification.IValueNotified;
import com.redsponge.dbf.notification.NotificationHub;

public class MusicManager implements IValueNotified<Float> {

    private String[] paths = {"music/birds.ogg", "music/1.ogg", "music/2.ogg", "music/3.ogg", "music/4.ogg", "music/5.ogg", "music/6.ogg", "music/final.ogg"};
    private boolean[] keepPosition = {false, true, true, true, true, true, false};
    private boolean[] loop = {true, true, true, true, true, true, true, false};
    private float[] volume = {2, 1, 1, 1, 1, 1, 1, 2};
    private int currentIndex;

    private Music current;
    private Music next;

    private AssetManager am;

    public MusicManager() {
        currentIndex = 0;
        am = new AssetManager();
        am.load(paths[currentIndex], Music.class);
        am.finishLoading();
        current = am.get(paths[currentIndex]);
        current.setVolume(Constants.MUSIC_HUB.getValue());
        current.play();
        current.setLooping(true);
        prepNext();
    }

    private void prepNext() {
        am.load(paths[(currentIndex + 1) % paths.length], Music.class);
    }

    public void tick() {
        am.update();
    }

    public void swap() {
        am.finishLoading();
        next = am.get(paths[(currentIndex + 1) % paths.length]);
        float pos = current.getPosition();
        if(!keepPosition[currentIndex]) {
            pos = 0;
        }
        next.play();
        next.setLooping(loop[(currentIndex + 1) % paths.length]);
        next.setVolume(volume[(currentIndex + 1) % paths.length] * Constants.MUSIC_HUB.getValue());
        next.setPosition(pos);
        current.stop();

        current = next;
        next = null;
        am.unload(paths[currentIndex]);
        currentIndex++;
        currentIndex %= paths.length;
        prepNext();
    }

    public void dispose() {
        am.dispose();
    }

    public void stop() {
        current.stop();
    }

    @Override
    public void update(Float newValue) {
        current.setVolume(newValue * volume[currentIndex]);
    }
}
