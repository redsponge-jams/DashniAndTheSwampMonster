package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.assets.atlas.AtlasAnimation;
import com.redsponge.redengine.assets.atlas.AtlasFrame;
import com.redsponge.redengine.screen.components.AnimationComponent;

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

    @AtlasAnimation(animationName = "run", atlas = "playerAtlas", length = 12, frameDuration = 0.05f)
    private Animation<TextureRegion> playerRunAnimation;


    @Asset("textures/target/target.atlas")
    public TextureAtlas targetAtlas;

    @AtlasAnimation(animationName = "idle", atlas = "targetAtlas", length = 6, frameDuration = 0.2f)
    private Animation<TextureRegion> targetIdleAnimation;

    @AtlasAnimation(animationName = "hurt", atlas = "targetAtlas", length = 4, playMode = PlayMode.LOOP_PINGPONG)
    private Animation<TextureRegion> targetHurtAnimation;


    @Asset("textures/octopus/octopus.atlas")
    public TextureAtlas octopusAtlas;

    @AtlasAnimation(animationName = "raise",atlas = "octopusAtlas", length = 4, frameDuration = 0.05f, playMode = PlayMode.NORMAL)
    private Animation<TextureRegion> octopusRaiseAnimation;

    @AtlasAnimation(animationName = "stun",atlas = "octopusAtlas", length = 8, frameDuration = 0.05f)
    private Animation<TextureRegion> octopusStunAnimation;

    @AtlasAnimation(animationName = "sink",atlas = "octopusAtlas", length = 4, frameDuration = 0.05f, playMode = PlayMode.NORMAL)
    private Animation<TextureRegion> octopusSinkAnimation;


    @AtlasAnimation(animationName = "side_signal", atlas = "octopusAtlas", length = 1, playMode = PlayMode.NORMAL, frameDuration = 0.4f)
    private Animation<TextureRegion> octopusAttackSideSignalAnimation;

    @AtlasAnimation(animationName = "side_attack", atlas = "octopusAtlas", length = 5, frameDuration = 0.08f, playMode = PlayMode.NORMAL)
    private Animation<TextureRegion> octopusAttackSideAttackAnimation;

    @AtlasAnimation(animationName = "side_out", atlas = "octopusAtlas", length = 4, playMode = PlayMode.NORMAL)
    private Animation<TextureRegion> octopusAttackSideEndAnimation;



    @Asset("textures/detail/background.png")
    private Texture background;

    @Asset("textures/detail/waste.png")
    private Texture waste;
}
