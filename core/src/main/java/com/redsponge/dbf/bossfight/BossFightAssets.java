package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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

    @AtlasAnimation(animationName = "run", atlas = "playerAtlas", length = 12, frameDuration = 0.05f)
    private Animation<TextureRegion> playerRunAnimation;

    @AtlasAnimation(animationName = "dead", atlas = "playerAtlas", length = 2, frameDuration = 0.1f, playMode = PlayMode.NORMAL)
    private Animation<TextureRegion> playerDieAnimation;

    @AtlasAnimation(animationName = "jump_up", atlas = "playerAtlas", length = 1, frameDuration = 0.1f, playMode = PlayMode.NORMAL, startsWith = -1)
    private Animation<TextureRegion> playerJumpUpAnimation;

    @AtlasAnimation(animationName = "jump_down", atlas = "playerAtlas", length = 1, frameDuration = 0.1f, playMode = PlayMode.NORMAL, startsWith = -1)
    private Animation<TextureRegion> playerJumpDownAnimation;


    @Asset("textures/target/target.atlas")
    public TextureAtlas targetAtlas;

    @AtlasAnimation(animationName = "idle", atlas = "targetAtlas", length = 6, frameDuration = 0.2f)
    private Animation<TextureRegion> targetIdleAnimation;

    @AtlasAnimation(animationName = "hurt", atlas = "targetAtlas", length = 4, playMode = PlayMode.LOOP_PINGPONG)
    private Animation<TextureRegion> targetHurtAnimation;

    @AtlasAnimation(animationName = "sleeping", atlas = "targetAtlas", length = 4, frameDuration = .5f)
    private Animation<TextureRegion> targetSleepAnimation;

    @AtlasAnimation(animationName = "flag", atlas = "targetAtlas", length = 3, frameDuration = .2f, playMode = PlayMode.LOOP_PINGPONG)
    private Animation<TextureRegion> targetFlagAnimation;


    @Asset("textures/octopus/octopus.atlas")
    public TextureAtlas octopusAtlas;

    @AtlasAnimation(animationName = "raise",atlas = "octopusAtlas", length = 4, frameDuration = 0.05f, playMode = PlayMode.NORMAL)
    private Animation<TextureRegion> octopusRaiseAnimation;

    @AtlasAnimation(animationName = "stun",atlas = "octopusAtlas", length = 12, frameDuration = 0.08f)
    private Animation<TextureRegion> octopusStunAnimation;

    @AtlasAnimation(animationName = "sink",atlas = "octopusAtlas", length = 4, frameDuration = 0.05f, playMode = PlayMode.NORMAL)
    private Animation<TextureRegion> octopusSinkAnimation;


    @AtlasAnimation(animationName = "side_signal", atlas = "octopusAtlas", length = 16, playMode = PlayMode.NORMAL, frameDuration = 0.1f)
    private Animation<TextureRegion> octopusAttackSideSignalAnimation;

    @AtlasAnimation(animationName = "side_attack", atlas = "octopusAtlas", length = 6, frameDuration = 0.06f, playMode = PlayMode.NORMAL)
    private Animation<TextureRegion> octopusAttackSideAttackAnimation;


    @AtlasAnimation(animationName = "bubble_raise", atlas = "octopusAtlas", length = 5, playMode = PlayMode.NORMAL, frameDuration = 0.2f/5)
    private Animation<TextureRegion> octopusAttackBubbleRaiseAnimation;

    @AtlasAnimation(animationName = "bubble_idle", atlas = "octopusAtlas", length = 1, playMode = PlayMode.NORMAL, frameDuration = 0.4f)
    private Animation<TextureRegion> octopusAttackBubbleIdleAnimation;

    @AtlasAnimation(animationName = "bubble_attack", atlas = "octopusAtlas", length = 4, frameDuration = 0.1f)
    private Animation<TextureRegion> octopusAttackBubbleAttackAnimation;

    @AtlasAnimation(animationName = "flag_raise", atlas = "octopusAtlas", length = 3, frameDuration = 0.02f, playMode = PlayMode.NORMAL)
    private Animation<TextureRegion> octopusWhiteFlagRaiseAnimation;

    @AtlasAnimation(animationName = "flag", atlas = "octopusAtlas", length = 12)
    private Animation<TextureRegion> octopusWhiteFlagAnimation;


    @Asset("textures/detail/detail.atlas")
    private TextureAtlas detailAtlas;

    @AtlasFrame(frameName = "background", atlas = "detailAtlas")
    private TextureRegion background;

    @AtlasAnimation(animationName = "water", atlas = "detailAtlas", length = 4, frameDuration = 0.3f)
    private Animation<TextureRegion> water;

    @AtlasFrame(frameName = "island", atlas = "detailAtlas")
    private TextureRegion island;


    @Asset("textures/attacks/attacks.atlas")
    private TextureAtlas attackAtlas;

    @AtlasFrame(frameName = "bubble", atlas = "attackAtlas")
    private TextureRegion bubble;


    @Asset("particles/bubbling.p")
    private ParticleEffect bubblingWaterParticle;



    @Asset("sounds/dashni_attack.ogg")
    private Sound dashniAttackSound;

    @Asset("sounds/ghost_ouch.ogg")
    private Sound ghostOuchSound;

    @Asset("sounds/octopus_stun.ogg")
    private Sound octopusStunSound;

    @Asset("sounds/bubbling.ogg")
    private Sound bubblingSound;

    @Asset("sounds/geyser.ogg")
    private Sound geyserSound;


    // Splashes
    @Asset("sounds/small_splash_1.ogg")
    private Sound splashSound1;

    @Asset("sounds/small_splash_2.ogg")
    private Sound splashSound2;

    @Asset("sounds/small_splash_3.ogg")
    private Sound splashSound3;

    @Asset("sounds/small_splash_4.ogg")
    private Sound splashSound4;

    @Asset("sounds/small_splash_5.ogg")
    private Sound splashSound5;


    @Asset("sounds/step_1.ogg")
    private Sound stepSound1;

    @Asset("sounds/step_2.ogg")
    private Sound stepSound2;

    @Asset("sounds/step_3.ogg")
    private Sound stepSound3;

    @Asset("sounds/step_4.ogg")
    private Sound stepSound4;

    @Asset("sounds/step_5.ogg")
    private Sound stepSound5;

    @Asset("sounds/step_6.ogg")
    private Sound stepSound6;

    @Asset("sounds/jump.ogg")
    private Sound jumpSound;

    @Asset("sounds/hit.ogg")
    private Sound hitSound;


    @Asset("sounds/octo_head_splash_up.ogg")
    private Sound bigSplashSound;

    @Asset("sounds/whip.ogg")
    private Sound whipSound;

    @Asset("sounds/bubble_shoot.ogg")
    private Sound bubbleShootSound;

    @Asset("textures/tooltip/tooltip_background.png")
    private Texture tooltipBackground;


    @Asset("textures/lights/lights.atlas")
    private TextureAtlas lightAtlas;

    @AtlasFrame(atlas = "lightAtlas", frameName = "diagonal_regular")
    private TextureRegion lightDiag;

    @AtlasFrame(atlas = "lightAtlas", frameName = "diagonal_down")
    private TextureRegion lightDiagDown;

    @AtlasFrame(atlas = "lightAtlas", frameName = "diagonal_side")
    private TextureRegion lightDiagSide;
}
