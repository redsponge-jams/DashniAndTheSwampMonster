package com.redsponge.dbf.sound;

import com.badlogic.gdx.utils.Array;

public class SoundHub {

    private static SoundHub instance;

    public static SoundHub getInstance() {
        if(instance == null) {
            instance = new SoundHub();
        }
        return instance;
    }

    private float value;

    private Array<ISoundUpdateListener> listeners;

    private SoundHub() {
        listeners = new Array<>();
    }

    public void addListener(ISoundUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ISoundUpdateListener listener) {
        listeners.removeValue(listener, true);
    }

    public void updateValue(float value) {
        this.value =value;
        notifyListeners();
    }

    private void notifyListeners() {
        for (int i = 0; i < listeners.size; i++) {
            listeners.get(i).update(value);
        }
    }
}
