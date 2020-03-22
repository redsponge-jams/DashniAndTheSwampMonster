package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.assets.atlas.AtlasAnimation;
import com.redsponge.redengine.assets.atlas.AtlasFrame;
import com.sun.org.apache.xerces.internal.dom.RangeImpl;

public class BossFightAssets extends AssetSpecifier {

    public BossFightAssets(AssetManager am) {
        super(am);
    }

    @Asset("textures/player/player.atlas")
    public TextureAtlas playerAtlas;

    @AtlasAnimation(animationName = "idle", atlas = "playerAtlas", length = 6, playMode = PlayMode.LOOP_PINGPONG, frameDuration = 0.2f)
    private Animation<TextureRegion> playerIdleAnimation;



    @Asset("textures/target/target.atlas")
    public TextureAtlas targetAtlas;

    @AtlasFrame(frameName = "idle", atlas = "targetAtlas")
    private TextureRegion targetIdle;

}
