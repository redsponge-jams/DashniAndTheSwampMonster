package com.redsponge.dbf;

import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.redengine.EngineGame;

public class DashniBossFight extends EngineGame {

    @Override
    public void init() {
        setScreen(new BossFightScreen(ga));
    }
}