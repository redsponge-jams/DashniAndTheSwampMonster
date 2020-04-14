package com.redsponge.dbf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.redengine.EngineGame;

public class DashniBossFight extends EngineGame {

    public static boolean wonNormal = false;

    @Override
    public void init() {
        Music m = Gdx.audio.newMusic(Gdx.files.internal("music/1.ogg"));
        m.play();
        m.dispose();
        Music m2 = Gdx.audio.newMusic(Gdx.files.internal("music/2.ogg"));
        m2.play();
        m2.dispose();
        Music m3 = Gdx.audio.newMusic(Gdx.files.internal("music/3.ogg"));
        m3.play();
        m3.dispose();
        Music m4 = Gdx.audio.newMusic(Gdx.files.internal("music/4.ogg"));
        m4.play();
        m4.dispose();
        Music m5 = Gdx.audio.newMusic(Gdx.files.internal("music/5.ogg"));
        m5.play();
        m5.setPosition(10);
        m5.dispose();

//        setScreen(new IntroScreen(ga));
//        setScreen(new MenuScreen(ga, null));
        setScreen(new BossFightScreen(ga, true));
//        setScreen(new SplashScreenScreen(ga, new ToastySplashScreen(ga), Transitions.linearFade(1, batch, shapeRenderer)));
//        setScreen(new WinScreen(ga));
    }
}