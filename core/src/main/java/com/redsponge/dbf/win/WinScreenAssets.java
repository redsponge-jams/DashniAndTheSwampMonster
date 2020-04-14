package com.redsponge.dbf.win;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;

public class WinScreenAssets extends AssetSpecifier {

    public WinScreenAssets(AssetManager am) {
        super(am);
    }

    @Asset("win.png")
    private Texture win;
}
