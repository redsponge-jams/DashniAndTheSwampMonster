package com.redsponge.dbf.bossfight;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.redsponge.dbf.DashniBossFight;
import com.redsponge.dbf.bossfight.attacks.AttackBubble;
import com.redsponge.dbf.bossfight.attacks.BossAttackManager;
import com.redsponge.dbf.bossfight.attacks.BossAttacks;
import com.redsponge.dbf.bossfight.attacks.BubbleAttackArm;
import com.redsponge.dbf.bossfight.fight.*;
import com.redsponge.dbf.bossfight.visual.Background;
import com.redsponge.dbf.bossfight.visual.FlyLight;
import com.redsponge.dbf.bossfight.visual.ParticleManager;
import com.redsponge.dbf.bossfight.visual.Tooltip;
import com.redsponge.dbf.bossfight.visual.TooltipManager;
import com.redsponge.dbf.bossfight.visual.Water;
import com.redsponge.dbf.lights.FadingLight;
import com.redsponge.dbf.menu.MenuScreen;
import com.redsponge.dbf.utils.Constants;
import com.redsponge.dbf.win.WinScreen;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.assets.Fonts;
import com.redsponge.redengine.lighting.LightSystem;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.physics.PhysicsDebugRenderer;
import com.redsponge.redengine.render.util.ScreenFiller;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.screen.components.Mappers;
import com.redsponge.redengine.screen.components.PositionComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.screen.systems.PhysicsSystem;
import com.redsponge.redengine.screen.systems.RenderSystem;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.*;
import org.lwjgl.opengl.Display;

import java.util.concurrent.ConcurrentHashMap;

public class BossFightScreen extends AbstractScreen {

    private Sound[] splashSounds;

    private FPSLogger fpsLogger;

    private RenderSystem renderSystem;
    private PhysicsDebugRenderer pdr;
    private PhysicsSystem physicsSystem;

    private ParticleManager pm;

    private static MusicManager musicManager;

    private DelayedRemovalArray<Rectangle> attackBoxes;

    private ConcurrentHashMap<ScreenEntity, Float> scheduledEntities;
    private DashniPlayer player;

    private DelayedRemovalArray<Island> islands;

    private BossAttackManager bam;

    public static FightPhase phase;
    private boolean headUp;

    private FitViewport guiViewport;

    private int hits;
    private boolean isDashniDead;

    private IntVector2 deadDashniPos;

    private Animation<TextureRegion> dashniDeadAnimation;
    private float deadDashniTime;
    private boolean raisedFlag;

    private float raiseFlagCounter;

    private boolean isEasy;

    private FrameBuffer pauseFrame;
    private Stage pauseStage;
    private boolean paused;
    private boolean grabScreen;

    private Viewport pauseViewport;

    private BitmapFont font;

    private Skin skin;

    private LightSystem lightSystem;

    private DelayedRemovalArray<FadingLight> lights;

    private GeyserHandler geyserHandler;

    public static void progressPhase() {
        musicManager.swap();
        if(phase == FightPhase.ZERO) {
            phase = FightPhase.ONE;
        } else if(phase == FightPhase.ONE) {
            phase = FightPhase.TWO;
        } else if(phase == FightPhase.TWO) {
            phase = FightPhase.THREE;
        } else if(phase == FightPhase.THREE) {
            phase = FightPhase.FOUR;
        } else if(phase == FightPhase.FOUR) {
            phase = FightPhase.FIVE;
        } else if(phase == FightPhase.FIVE) {
            phase = FightPhase.SIX;
        } else if(phase == FightPhase.SIX) {
            phase = FightPhase.WIN;
        }
    }

    public RenderSystem getRenderSystem() {
        return renderSystem;
    }

    public enum FightPhase {
        ZERO(-1) {
            final IntVector2 octoPoint = new IntVector2(640 / 2, 360 / 3 + 20);
            @Override
            public IntVector2 createOctopusPoint() {
                return octoPoint;
            }

            @Override
            public float getOctopusSpeed() {
                return 0;
            }

            public void processAttack(float delta, BossFightScreen screen, BossAttackManager bam) {
            }

            @Override
            public float getOctopusLerpPower() {
                return 1;
            }

            @Override
            public int getOctopusLife() {
                return 1;
            }
        },
        ONE(-1) {
            float time = 4;
            IntVector2 octoPoint = new IntVector2(640 / 2, 360 / 4 * 3);
            @Override
            public IntVector2 createOctopusPoint() {
                return octoPoint.set(MathUtils.random(100, 640 - 100), octoPoint.y);
            }

            @Override
            public float getOctopusSpeed() {
                return 0;
            }

            public void processAttack(float delta, BossFightScreen screen, BossAttackManager bam) {
                time += delta;
                if(time > 5) {
                    BossAttacks.stairwayI(screen.batch, screen.shapeRenderer, screen);
                    time -= 5;
                }
            }

            @Override
            public float getOctopusLerpPower() {
                return 1;
            }

            @Override
            public int getOctopusLife() {
                return 1;
            }
        },
        TWO(-1) {
            private float time = 4;
            @Override
            public IntVector2 createOctopusPoint() {
                return new IntVector2(MathUtils.random(100, 640 - 100), 300);
            }

            @Override
            public float getOctopusLerpPower() {
                return 0.1f;
            }

            @Override
            public void processAttack(float delta, BossFightScreen screen, BossAttackManager bam) {
                time += delta;
                if(time > 5) {
                    time -= 5;
                    if(MathUtils.randomBoolean()) {
                        BossAttacks.stairwayI(screen.batch, screen.shapeRenderer, screen);
                    } else {
                        BossAttacks.closeLine(screen.batch, screen.shapeRenderer, screen, (int) Mappers.position.get(screen.player).getY());
                    }
                }
            }

            @Override
            public float getOctopusSpeed() {
                return 100;
            }

            @Override
            public int getOctopusLife() {
                return 3;
            }
        },
        THREE(-1) {
            private float time = 4;
            private BubbleAttackArm bubbleArm;
            @Override
            public IntVector2 createOctopusPoint() {
                return new IntVector2(MathUtils.random(100, 640 - 100), MathUtils.random(200, 300));
            }

            @Override
            public float getOctopusLerpPower() {
                return 0.1f;
            }

            @Override
            public void processAttack(float delta, BossFightScreen screen, BossAttackManager bam) {
                time += delta;
                if(time > 5) {
                    time -= 5;
                    float option = MathUtils.random();
                    if(option < 1/3f) {
                        BossAttacks.stairwayI(screen.batch, screen.shapeRenderer, screen);
                    } else if(option > 1/3f && option < 2/3f){
                        BossAttacks.closeLine(screen.batch, screen.shapeRenderer, screen, (int) Mappers.position.get(screen.player).getY());
                    } else {
                        bubbleArm = new BubbleAttackArm(screen.batch, screen.shapeRenderer, GeneralUtils.randomItem(new Integer[] {100, 500}), 2, 3, 1);
                        screen.addEntity(bubbleArm);
                    }
                }
                if(bubbleArm != null && bubbleArm.isRemoved()) {
                    bubbleArm = null;
                }
            }

            @Override
            public float getOctopusSpeed() {
                return 100;
            }

            @Override
            public int getOctopusLife() {
                return 3;
            }
        },
        FOUR(5) {
            private BubbleAttackArm bubbleArm;
            private float time = 5;

            @Override
            public IntVector2 createOctopusPoint() {
                return new IntVector2(MathUtils.random(100, 640 - 100), MathUtils.random(250, 300));
            }

            @Override
            public float getOctopusLerpPower() {
                return 0.1f;
            }

            @Override
            public void processAttack(float delta, BossFightScreen screen, BossAttackManager bam) {
                time += delta;
                if(time > 5) {
                    time -= 5;
                    float option = MathUtils.random();
                    if(option < 1/3f) {
                        BossAttacks.stairwayI(screen.batch, screen.shapeRenderer, screen);
                    } else if(option > 1/3f && option < 2/3f){
                        BossAttacks.closeLine(screen.batch, screen.shapeRenderer, screen, (int) Mappers.position.get(screen.player).getY());
                    } else {
                        bubbleArm = new BubbleAttackArm(screen.batch, screen.shapeRenderer, GeneralUtils.randomItem(new Integer[] {100, 500}), 2, 5, .75f);
                        screen.addEntity(bubbleArm);
                    }
                }
                if(bubbleArm != null && bubbleArm.isRemoved()) {
                    bubbleArm = null;
                }
            }

            @Override
            public float getOctopusSpeed() {
                return 100;
            }

            @Override
            public int getOctopusLife() {
                return 5;
            }
        },
        FIVE(3) {
            private float time = 4;
            private BubbleAttackArm bubbleArm;

            @Override
            public IntVector2 createOctopusPoint() {
                return new IntVector2(MathUtils.random(100, 640 - 100), MathUtils.random(250, 300));
            }

            @Override
            public float getOctopusLerpPower() {
                return 0.1f;
            }

            @Override
            public void processAttack(float delta, BossFightScreen screen, BossAttackManager bam) {
                time += delta;

                if(time > 5) {
                    time -= 5;
                    float option = MathUtils.random();
                    if(option < 1/3f) {
                        BossAttacks.stairwayII(screen.batch, screen.shapeRenderer, screen);
                    } else if(option > 1/3f && option < 2/3f){
                        BossAttacks.closeLine(screen.batch, screen.shapeRenderer, screen, (int) Mappers.position.get(screen.player).getY());
                    } else {
                        bubbleArm = new BubbleAttackArm(screen.batch, screen.shapeRenderer, GeneralUtils.randomItem(new Integer[] {100, 500}), 2, 5, .75f);
                        screen.addEntity(bubbleArm);
                    }
                }
                if(bubbleArm != null && bubbleArm.isRemoved()) {
                    bubbleArm = null;
                }
            }

            @Override
            public float getOctopusSpeed() {
                return 200;
            }

            @Override
            public int getOctopusLife() {
                return 5;
            }
        },
        SIX(1) {
            private float time = 3;
            private BubbleAttackArm bubbleArm;

            @Override
            public IntVector2 createOctopusPoint() {
                return new IntVector2(MathUtils.random(100, 640 - 100), MathUtils.random(250, 300));
            }

            @Override
            public float getOctopusLerpPower() {
                return 0.1f;
            }

            @Override
            public void processAttack(float delta, BossFightScreen screen, BossAttackManager bam) {
                time += delta;

                if(time > 4) {
                    time -= 4;
                    float option = MathUtils.random();
                    if(option < 1/3f) {
                        BossAttacks.stairwayII(screen.batch, screen.shapeRenderer, screen);
                    } else if(option > 1/3f && option < 2/3f){
                        BossAttacks.closeLine(screen.batch, screen.shapeRenderer, screen, (int) Mappers.position.get(screen.player).getY());
                    } else {
                        bubbleArm = new BubbleAttackArm(screen.batch, screen.shapeRenderer, GeneralUtils.randomItem(new Integer[] {100, 500}), 2, 5, .4f);
                        screen.addEntity(bubbleArm);
                    }
                }
                if(bubbleArm != null && bubbleArm.isRemoved()) {
                    bubbleArm = null;
                }
            }

            @Override
            public float getOctopusSpeed() {
                return 300;
            }

            @Override
            public int getOctopusLife() {
                return 5;
            }
        },
        WIN(-1) {
            @Override
            public IntVector2 createOctopusPoint() {
                return new IntVector2(90, 230);
            }

            @Override
            public float getOctopusSpeed() {
                return 50;
            }

            @Override
            public void processAttack(float delta, BossFightScreen screen, BossAttackManager bam) {
            }

            @Override
            public float getOctopusLerpPower() {
                return 0.1f;
            }

            @Override
            public int getOctopusLife() {
                return Integer.MAX_VALUE;
            }
        }
        ;

        private final float geyserTime;

        FightPhase(float geyserTime) {
            this.geyserTime = geyserTime;
        }

        public IntVector2 createOctopusPoint() { throw new RuntimeException("Not Implemented"); }

        public float getOctopusSpeed() { throw new RuntimeException("Not Implemented");}

        public void processAttack(float delta, BossFightScreen screen, BossAttackManager bam) { throw new RuntimeException("Not Implemented");}

        public float getOctopusLerpPower() {  throw new RuntimeException("Not Implemented"); }

        public int getOctopusLife() {
            throw new RuntimeException("Not Implemented");
        }

        public float getGeyserTime() {
            return geyserTime;
        }
    }

    private TooltipManager tooltipManager;

    public BossFightScreen(GameAccessor ga, boolean isEasy) {
        super(ga);
        this.isEasy = isEasy;
        fpsLogger = new FPSLogger();
    }

    public void addScheduledEntity(float time, ScreenEntity entity) {
        scheduledEntities.put(entity, time);
    }

    @Override
    public void show() {
        tooltipManager = new TooltipManager(this);
        geyserHandler = new GeyserHandler(this);

        lightSystem = new LightSystem(getScreenWidth(), getScreenHeight(), batch);
        lightSystem.registerLightType(LightType.MULTIPLICATIVE);
        lightSystem.registerLightType(LightType.ADDITIVE);
        lightSystem.setAmbianceColor(new Color(0.6f, 0.8f, 0.9f, 0.5f), LightType.MULTIPLICATIVE);

        lights = new DelayedRemovalArray<>();

        addFadingLight(getScreenWidth() - 96, getScreenHeight() - 96, 256 * 2, assets.getTextureRegion("lightDiag"), 0x44FDFFFF);
        addFadingLight(getScreenWidth() - 96, getScreenHeight() - 96, 256 * 1.5f, assets.getTextureRegion("lightDiagSide"), 0x7F6987FF);
        addFadingLight(getScreenWidth() - 96, getScreenHeight() - 96*2, 256 * 2, assets.getTextureRegion("lightDiagDown"), 0x007F7FFF);
        addFadingLight(getScreenWidth() - 96, getScreenHeight() - 96, 256 * 3, assets.getTextureRegion("lightDiagSide"), 0xFCFFB199);
        addFadingLight(getScreenWidth() - 96 * 4, getScreenHeight() - 96, 256 * 2, assets.getTextureRegion("lightDiag"), 0x228b22ff);

        for (int i = 0; i < 10; i++) {
            addEntity(new FlyLight(batch, shapeRenderer));
        }

        musicManager = new MusicManager();
        phase = FightPhase.ZERO;
        guiViewport = new FitViewport(getScreenWidth(), getScreenHeight());
        pm = new ParticleManager(batch, shapeRenderer);
        addEntity(pm);

        physicsSystem = getEntitySystem(PhysicsSystem.class);
        renderSystem = getEntitySystem(RenderSystem.class);
        this.bam = new BossAttackManager(this);

        islands = new DelayedRemovalArray<Island>();
        islands.add(new Island(batch, shapeRenderer, 300, 10, 104, 42));
        islands.add(new Island(batch, shapeRenderer, 150, 10, 104, 42));

        for (int i = 0; i < islands.size; i++) {
            addEntity(islands.get(i));
        }
        attackBoxes = new DelayedRemovalArray<>();

        addEntity(new TargetOctopus(batch, shapeRenderer));
        addEntity(player = new DashniPlayer(batch, shapeRenderer));
        addEntity(new Background(batch, shapeRenderer));
        addEntity(new Water(batch, shapeRenderer));

        dashniDeadAnimation = assets.getAnimation("playerDieAnimation");

        splashSounds = new Sound[5];
        for (int i = 0; i < splashSounds.length; i++) {
            splashSounds[i] = assets.get("splashSound" + (i + 1), Sound.class);
        }

        pdr = new PhysicsDebugRenderer();

        scheduledEntities = new ConcurrentHashMap<>();
        pauseStage = new Stage(guiViewport, batch);
        skin = new Skin(Gdx.files.internal("skins/menu/menu_skin.json"));
        Slider musicSlider = new Slider(0, 1, 0.01f, false, skin);
        musicSlider.setValue(Constants.MUSIC_HUB.getValue());
        musicSlider.setPosition(guiViewport.getWorldWidth() / 2, 200);
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Constants.MUSIC_HUB.updateValue(((Slider)actor).getValue());
            }
        });
        pauseStage.addActor(musicSlider);
        Label lbl = new Label("Music", skin);
        lbl.setPosition(guiViewport.getWorldWidth() / 3, 200);
        pauseStage.addActor(lbl);
        Constants.MUSIC_HUB.addListener(musicManager);

        Slider soundSlider = new Slider(0, 1, 0.01f, false, skin);
        soundSlider.setValue(Constants.SOUND_HUB.getValue());
        soundSlider.setPosition(guiViewport.getWorldWidth() / 2, 175);
        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Constants.SOUND_HUB.updateValue(((Slider)actor).getValue());
            }
        });
        pauseStage.addActor(soundSlider);
        Label soundLbl = new Label("Sfx", skin);
        soundLbl.setPosition(guiViewport.getWorldWidth() / 3, 175);
        pauseStage.addActor(soundLbl);

        TextButton resumeButton = new TextButton("Resume", skin);
        resumeButton.setPosition(guiViewport.getWorldWidth() / 2, 100, Align.center);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        });
        pauseStage.addActor(resumeButton);

        TextButton backButton = new TextButton("Exit", skin);
        backButton.setPosition(guiViewport.getWorldWidth() / 2, 50, Align.center);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ga.transitionTo(new MenuScreen(ga, null), Transitions.sineSlide(1, batch, shapeRenderer));
            }
        });
        pauseStage.addActor(backButton);

        font = Fonts.getFont("pixelmix", 16);
    }

    public void playRandomSplash() {
        GeneralUtils.randomItem(splashSounds).play(Constants.SOUND_HUB.getValue());
    }

    private FadingLight addFadingLight(float x, float y, float rad, TextureRegion tex, int color) {
        FadingLight line = new FadingLight(x, y, rad, new AtlasRegion(tex));
        line.getColor().set(color);
        lightSystem.addLight(line, LightType.ADDITIVE);
        lights.add(line);
        return line;
    }

    public void spawnBubble(float x, float y) {
        PositionComponent playerPos = Mappers.position.get(player);
        addEntity(new AttackBubble(batch, shapeRenderer, x, y, playerPos.getX(), playerPos.getY()));
    }

    @Override
    public void tick(float v) {
        fpsLogger.log();
        if(transitioning) return;
        if(!paused && !Display.isActive()) {
            togglePause();
            return;
        }
        musicManager.tick();
        if(isDashniDead) {
            if(Gdx.input.isKeyJustPressed(Keys.ENTER)) {
                ga.transitionTo(new BossFightScreen(ga, isEasy), Transitions.linearFade(1, batch, shapeRenderer));
            }
            else if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
                ga.transitionTo(new MenuScreen(ga, null), Transitions.linearFade(1, batch, shapeRenderer));
            }
            deadDashniTime += v;
            return;
        }
        if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            togglePause();
        }

        if(!paused) {
            for (int i = 0; i < lights.size; i++) {
                lights.get(i).tick(v);
            }
            if (!headUp && phase != FightPhase.WIN) {
                phase.processAttack(v, this, bam);
            }
            if (phase == FightPhase.WIN && !raisedFlag) {
                renderSystem.getCamera().position.lerp(new Vector3(150, getScreenHeight() / 2f, 0), 0.001f);
                renderSystem.getCamera().zoom = MathUtilities.lerp(renderSystem.getCamera().zoom, 0.9f, 0.001f);
            } else if (raisedFlag) {
                raiseFlagCounter += v;
                if (raiseFlagCounter > 5) {
                    ga.transitionTo(new WinScreen(ga), Transitions.sineCircle(2, batch, shapeRenderer));
                }
            }
            pm.additionalTick(v);
            for (ScreenEntity screenEntity : scheduledEntities.keySet()) {
                scheduledEntities.put(screenEntity, scheduledEntities.get(screenEntity) - v);
                if (scheduledEntities.get(screenEntity) <= 0) {
                    addEntity(screenEntity);
                    scheduledEntities.remove(screenEntity);
                }
            }
            if(phase == FightPhase.ZERO) {
                tooltipManager.tick(v);
            }
        } else {
            pauseStage.act(v);
        }
        lightSystem.prepareMap(LightType.ADDITIVE, renderSystem.getViewport());
        lightSystem.prepareMap(LightType.MULTIPLICATIVE, renderSystem.getViewport());

        if(grabScreen) {
            pauseFrame.begin();
        }
        if(!paused || grabScreen) {
            tickEntities(v);
            updateEngine(v);
            if(!headUp) {
                float geyserTime = phase.getGeyserTime();
                if(geyserTime > 0) {
                    geyserHandler.update(v, geyserTime);
                }
            }

            lightSystem.renderToScreen(LightType.MULTIPLICATIVE);
            lightSystem.renderToScreen(LightType.ADDITIVE);

//            renderSystem.getViewport().apply();
//            batch.setProjectionMatrix(renderSystem.getCamera().combined);
//            batch.begin();
//            getParticleManager().geyser().render(batch);
//            batch.end();
        }
        if(grabScreen) {
            pauseFrame.end();
            grabScreen = false;
        }
    }

    private void togglePause() {
        paused = !paused;
        if(paused) {
            if(pauseFrame != null) {
                pauseFrame.dispose();
            }
            Gdx.input.setInputProcessor(pauseStage);
            scheduleGrabScreen();
        } else {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void pause() {
        Logger.log(this, "YE");
        togglePause();
    }

    private void scheduleGrabScreen() {
        pauseFrame = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        pauseViewport = new FitViewport(pauseFrame.getWidth(), pauseFrame.getHeight());
        pauseViewport.update(pauseFrame.getWidth(), pauseFrame.getHeight(), true);
        grabScreen = true;
    }

    @Override
    public void render() {
        renderSystem.getViewport().apply();
        batch.setProjectionMatrix(renderSystem.getCamera().combined);

        if(paused && !grabScreen) {
            Gdx.gl.glClearColor(0, 0, 0, 1.0f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            pauseViewport.apply();
            batch.setProjectionMatrix(pauseViewport.getCamera().combined);

            batch.setColor(Color.WHITE);
            batch.begin();
            TextureRegion tr = new TextureRegion(pauseFrame.getColorBufferTexture());
            tr.flip(false, true);
            batch.draw(tr, 0, 0);
            batch.end();

            ScreenFiller.fillScreen(shapeRenderer, 0, 0, 0, 0.5f);
            guiViewport.apply();
            batch.setProjectionMatrix(guiViewport.getCamera().combined);

            batch.begin();
            font.draw(batch, "Paused", 0, 300, guiViewport.getWorldWidth(), Align.center, true);
            batch.end();
            pauseStage.draw();
            return;
        }

        if(isDashniDead) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            float timeX = Math.max(deadDashniTime - 1, 0);
            float yOffset = timeX * (1 - timeX);

            batch.begin();
            batch.getColor().a = 1;
            batch.draw(dashniDeadAnimation.getKeyFrame(deadDashniTime), deadDashniPos.x, deadDashniPos.y + yOffset * 200);
            if(deadDashniTime > 3) {
                batch.getColor().a = Math.min(deadDashniTime - 3, 1);
                BitmapFont font = Fonts.getFont("pixelmix", 32);
                font.getColor().a = Math.min(deadDashniTime - 3, 1);
                font.getData().setScale(1);
                font.draw(batch, "You Died!", 0, 200, 0, "You died!".length(), getScreenWidth(), Align.center, true);

                if(deadDashniTime >= 4) {
                    font.getColor().a = Math.min(deadDashniTime - 4, 1);
                    font.getData().setScale(0.5f);
                    font.draw(batch, "Press Enter To Try Again", 0, 150, 0, "Press enter to try again".length(), getScreenWidth(), Align.center, true);
                }
                if(deadDashniTime >= 5) {
                    font.getColor().a = Math.min(deadDashniTime - 5, 1);
                    font.getData().setScale(0.5f);
                    font.draw(batch, "Press ESC To Return To Menu", 0, 100, 0, "Press ESC To Return To Menu".length(), getScreenWidth(), Align.center, true);
                }
            }
            batch.end();
        } else {
//            pdr.render(physicsSystem.getPhysicsWorld(), renderSystem.getViewport().getCamera().combined);
//
//            shapeRenderer.begin(ShapeType.Line);
//            shapeRenderer.setColor(Color.RED);
//            for (int i = 0; i < attackBoxes.size; i++) {
//                Rectangle r = attackBoxes.get(i);
//                shapeRenderer.rect(r.x, r.y, r.width, r.height);
//            }
//            shapeRenderer.end();
            renderEntities();
        }
    }



    @Override
    public void notified(Object notifier, int notification) {
        super.notified(notifier, notification);
        tooltipManager.notified(notifier, notification);
        if(notification == Notifications.TARGET_OCTOPUS_DOWN) {
            addEntity(new OctopusPunchHead(batch, shapeRenderer));
            headUp = true;
            geyserHandler.cancelGeyser();
        } else if(notification == Notifications.OCTOPUS_EYE_GONE) {
            headUp = false;
        } else if(notification == Notifications.DASHNI_DEAD) {
            hits++;
            isDashniDead = true;
            musicManager.stop();
            PositionComponent pos = Mappers.position.get(player);
            deadDashniPos = new IntVector2((int) pos.getX(),(int) pos.getY());
            deadDashniTime = 0;
        } else if(notification == Notifications.RAISED_FLAG) {
            raisedFlag = true;
            if(!isEasy) {
                DashniBossFight.wonNormal = true;
            }
        }
    }

    @Override
    public int getScreenWidth() {
        return 640;
    }

    @Override
    public int getScreenHeight() {
        return 360;
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return BossFightAssets.class;
    }

    @Override
    public void reSize(int width, int height) {
        lightSystem.resize(width, height);
        renderSystem.resize(width, height);
        guiViewport.update(width, height, true);
        ScreenFiller.resize(width, height);
        if(paused) {
            scheduleGrabScreen();
        }
    }

    @Override
    public void disposeAssets() {
        pdr.dispose();
        musicManager.dispose();
        pauseStage.dispose();
    }

    public Array<Rectangle> getAttackBoxes() {
        return attackBoxes;
    }

    public Entity getPlayer() {
        return player;
    }

    public ParticleManager getParticleManager() {
        return pm;
    }

    public boolean isHeadUp() {
        return headUp;
    }

    public boolean isEasy() {
        return isEasy;
    }

    public LightSystem getLightSystem() {
        return lightSystem;
    }

    public DelayedRemovalArray<Island> getIslands() {
        return islands;
    }
}
