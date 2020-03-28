package com.redsponge.dbf.splash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.redsponge.dbf.intro.IntroScreen;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.GameAccessor;

public class ToastySplashScreen extends AbstractScreen {

    private ToastySplashScreenRenderer renderer;

    public ToastySplashScreen(GameAccessor ga) {
        super(ga);
    }

    AssetManager am;
    @Override
    public void show() {
        this.am = new AssetManager();
        this.am.load("textures/splashscreen/splashscreen_textures.atlas", TextureAtlas.class);
        this.am.finishLoading();
        renderer = new ToastySplashScreenRenderer(batch, am);
        renderer.begin();
    }

    @Override
    public void tick(float v) {
        if(transitioning) return;
        renderer.tick(v);
        if(renderer.isComplete()) {
            ga.transitionTo(new IntroScreen(ga), Transitions.linearFade(1, batch, shapeRenderer));
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(9f / 255, 208f / 255, 170f / 255f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return null;
    }

    @Override
    public void reSize(int width, int height) {
        renderer.resize(width, height);
    }

    @Override
    public void disposeAssets() {
        renderer.dispose();
        am.dispose();
    }
}
