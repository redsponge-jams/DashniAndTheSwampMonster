package com.redsponge.dbf;

import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.dbf.bossfight.WinScreen;
import com.redsponge.dbf.intro.IntroScreen;
import com.redsponge.dbf.menu.MenuScreen;
import com.redsponge.dbf.splash.ToastySplashScreen;
import com.redsponge.redengine.EngineGame;
import com.redsponge.redengine.screen.splashscreen.SplashScreenScreen;
import com.redsponge.redengine.transitions.Transitions;

public class DashniBossFight extends EngineGame {

    public static boolean wonNormal = false;

    @Override
    public void init() {
        setScreen(new SplashScreenScreen(ga, new ToastySplashScreen(ga), Transitions.linearFade(1, batch, shapeRenderer)));
//        setScreen(new WinScreen(ga));
    }
}