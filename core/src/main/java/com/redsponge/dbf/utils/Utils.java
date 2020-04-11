package com.redsponge.dbf.utils;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Utils {

    public static void tryPlay(Music music) {
        int fails = 0;
        boolean success = false;
        while(!success) {
            try {
                music.play();
                success = true;
            } catch (GdxRuntimeException e) {
                e.printStackTrace();
                fails++;
                if(fails >= 10) {
                    throw new RuntimeException("Couldn't Play Music", e);
                }
            }
        }
    }

}
