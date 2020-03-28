package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

public class MusicManager {

    private String[] paths = {"music/birds.ogg", "music/1.ogg", "music/2.ogg", "music/3.ogg", "music/4.ogg", "music/5.ogg", "music/6.ogg", "music/final.ogg"};
    private boolean[] keepPosition = {false, true, true, true, true, true, false};
    private boolean[] loop = {true, true, true, true, true, true, true, false};
    private int currentIndex;

    private Music current;
    private Music next;

    private AssetManager am;

    public MusicManager() {
        currentIndex = 6;
        am = new AssetManager();
        am.load(paths[currentIndex], Music.class);
        am.finishLoading();
        current = am.get(paths[currentIndex]);
        current.play();
        current.setLooping(true);
        prepNext();
    }

    private void prepNext() {
        am.load(paths[(currentIndex + 1) % paths.length], Music.class);
    }

    public void update() {
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
}
