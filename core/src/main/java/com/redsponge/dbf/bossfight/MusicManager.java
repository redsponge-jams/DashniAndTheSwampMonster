package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
import com.redsponge.dbf.utils.Constants;
import com.redsponge.dbf.notification.IValueNotified;
import com.redsponge.dbf.utils.Utils;

public class MusicManager implements Disposable, IValueNotified<Float> {

    private String[] paths = {"music/birds.ogg", "music/1.ogg", "music/2.ogg", "music/3.ogg", "music/4.ogg", "music/5_new.ogg", "music/6.ogg", "music/final.ogg"};
    private boolean[] keepPosition = {false, true, true, true, true, true, false};
    private boolean[] loop = {true, true, true, true, true, true, true, false};
    private float[] volume = {2, 1, 1, 1, 1, 1, 1, 2};
    private int currentIndex;

    private Music current;

    public MusicManager() {
        currentIndex = 0;
        current = Gdx.audio.newMusic(Gdx.files.internal(paths[currentIndex]));
        current.setVolume(Constants.MUSIC_HUB.getValue() * volume[currentIndex]);
        current.setLooping(true);
        Utils.tryPlay(current);
    }

    public void tick() {
//        am.update();
    }

    public void swap() {
        Music next = Gdx.audio.newMusic(Gdx.files.internal(paths[(currentIndex + 1) % paths.length]));
        float pos = current.getPosition();
        if(!keepPosition[currentIndex % keepPosition.length]) {
            pos = 0;
        }
        Utils.tryPlay(next);
        next.setLooping(loop[(currentIndex + 1) % paths.length]);
        next.setVolume(volume[(currentIndex + 1) % paths.length] * Constants.MUSIC_HUB.getValue());
        next.setPosition(pos);

        current.dispose();

        current = next;

        currentIndex++;
        currentIndex %= paths.length;
    }

    @Override
    public void dispose() {
        current.dispose();
    }

    public void stop() {
        current.stop();
    }

    @Override
    public void update(Float newValue) {
        current.setVolume(newValue * volume[currentIndex]);
    }
}
