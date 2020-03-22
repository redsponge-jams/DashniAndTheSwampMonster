package com.redsponge.dbf;

import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.redengine.EngineGame;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DashniBossFight extends EngineGame {

    @Override
    public void init() {
        setScreen(new BossFightScreen(ga));
    }
}