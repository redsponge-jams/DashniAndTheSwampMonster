package com.redsponge.dbf.menu;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;

public class MenuScreenAssets extends AssetSpecifier {

    public MenuScreenAssets(AssetManager am) {
        super(am);
    }

    @Asset("menu/background.png")
    private Texture background;

    @Asset("menu/title.png")
    private Texture title;
}
