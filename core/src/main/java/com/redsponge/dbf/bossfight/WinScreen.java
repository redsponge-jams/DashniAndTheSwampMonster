package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.dbf.menu.MenuScreen;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.GameAccessor;

public class WinScreen extends AbstractScreen {

    private FitViewport viewport;
    private Texture winTex;

    private Sound win;
    private float time;
    public WinScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        viewport = new FitViewport(128, 72);
        winTex = assets.get("win", Texture.class);
        win = Gdx.audio.newSound(Gdx.files.internal("music/win.ogg"));
        win.play(.5f);
    }

    @Override
    public void tick(float v) {
        if(transitioning) return;
        time += v;
        if(time > 5) {
            ga.transitionTo(new MenuScreen(ga, null), Transitions.sineSlide(1, batch, shapeRenderer));
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(winTex, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return WinScreenAssets.class;
    }

    @Override
    public void reSize(int width, int height) {
        viewport.update(width, height, true);
    }
}
