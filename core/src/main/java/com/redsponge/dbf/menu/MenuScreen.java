package com.redsponge.dbf.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.redsponge.dbf.bossfight.MusicManager;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.utils.GameAccessor;

public class MenuScreen extends AbstractScreen {

    private MusicManager mm;

    public MenuScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        mm = new MusicManager();
    }

    @Override
    public void tick(float v) {
        mm.update();
        if(Gdx.input.isKeyJustPressed(Keys.K)) {
            mm.swap();
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return null;
    }

    @Override
    public void disposeAssets() {
        super.disposeAssets();
        mm.dispose();
    }
}
