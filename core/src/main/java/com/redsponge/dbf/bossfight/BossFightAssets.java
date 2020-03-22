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

public class BossFightAssets extends AssetSpecifier {

    public BossFightAssets(AssetManager am) {
        super(am);
    }

    @Asset("textures/player/player.atlas")
    public TextureAtlas playerAtlas;

    @AtlasAnimation(animationName = "idle", atlas = "playerAtlas", length = 6, playMode = PlayMode.LOOP_PINGPONG, frameDuration = 0.2f)
    private Animation<TextureRegion> playerIdleAnimation;

    @AtlasAnimation(animationName = "attack", atlas = "playerAtlas", length = 10, frameDuration = 0.05f)
    private Animation<TextureRegion> playerAttackHorizAnimation;

    @AtlasAnimation(animationName = "attack_up", atlas = "playerAtlas", length = 10, frameDuration = 0.05f)
    private Animation<TextureRegion> playerAttackUpAnimation;

    @AtlasAnimation(animationName = "attack_down", atlas = "playerAtlas", length = 10, frameDuration = 0.05f)
    private Animation<TextureRegion> playerAttackDownAnimation;


    @Asset("textures/target/target.atlas")
    public TextureAtlas targetAtlas;

    @AtlasFrame(frameName = "idle", atlas = "targetAtlas")
    private TextureRegion targetIdle;


    @Asset("textures/octopus/octopus.atlas")
    public TextureAtlas octopusAtlas;

    @AtlasAnimation(animationName = "raise",atlas = "octopusAtlas", length = 2, playMode = PlayMode.NORMAL)
    private Animation<TextureRegion> octopusRaiseAnimation;

    @AtlasAnimation(animationName = "stun",atlas = "octopusAtlas", length = 2)
    private Animation<TextureRegion> octopusStunAnimation;

    @AtlasAnimation(animationName = "sink",atlas = "octopusAtlas", length = 2, playMode = PlayMode.NORMAL)
    private Animation<TextureRegion> octopusSinkAnimation;

}
