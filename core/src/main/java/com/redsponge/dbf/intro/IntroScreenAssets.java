package com.redsponge.dbf.intro;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.assets.atlas.AtlasAnimation;

public class IntroScreenAssets extends AssetSpecifier {

    public IntroScreenAssets(AssetManager am) {
        super(am);
    }

    @Asset("textures/intro/intro.atlas")
    private TextureAtlas introAtlas;

    @AtlasAnimation(animationName = "intro", atlas = "introAtlas", length = 8, frameDuration = 1, playMode = PlayMode.NORMAL)
    private Animation<TextureRegion> introAnimation;
}
