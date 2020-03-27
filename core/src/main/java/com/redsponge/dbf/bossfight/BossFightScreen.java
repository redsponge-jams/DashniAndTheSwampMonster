package com.redsponge.dbf.bossfight;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.physics.PhysicsDebugRenderer;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.screen.components.Mappers;
import com.redsponge.redengine.screen.components.PositionComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.screen.systems.PhysicsSystem;
import com.redsponge.redengine.screen.systems.RenderSystem;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.GameAccessor;
import com.redsponge.redengine.utils.GeneralUtils;
import com.redsponge.redengine.utils.IntVector2;

import java.util.concurrent.ConcurrentHashMap;

public class BossFightScreen extends AbstractScreen {

    private RenderSystem renderSystem;
    private PhysicsDebugRenderer pdr;
    private PhysicsSystem physicsSystem;

    private ParticleManager pm;

    private DelayedRemovalArray<Rectangle> attackBoxes;

    private ConcurrentHashMap<ScreenEntity, Float> scheduledEntities;
    private DashniPlayer player;

    private DelayedRemovalArray<Island> islands;

    private BossAttackManager bam;

    public static FightPhase phase;
    private boolean headUp;

    public static void progressPhase() {
        if(phase == FightPhase.ZERO) {
            phase = FightPhase.ONE;
        } else if(phase == FightPhase.ONE) {
            phase = FightPhase.TWO;
        }
    }

    public enum FightPhase {
        ZERO {
            IntVector2 octoPoint = new IntVector2(640 / 2, 360 / 3 + 20);
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
        ONE {
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
        TWO {
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
                return 5;
            }
        },
        THREE {
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
        FOUR {
            private float timeUntilGeiserChosen;
            private float time = 4;
            private float timeUntilGeiser;
            private Island chosenIsland;
            private BubbleAttackArm bubbleArm;
            private ParticleEffectPool.PooledEffect bubbles;

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
                timeUntilGeiserChosen -= delta;
                if(timeUntilGeiserChosen <= 0 && chosenIsland == null) {
                    chosenIsland = screen.islands.random();
                    timeUntilGeiser = MathUtils.random(2, 4);
                    PositionComponent pos = Mappers.position.get(chosenIsland);
                    bubbles = screen.getPm().spawnBubbles((int) pos.getX() + 10, (int) pos.getY());
                }
                if(chosenIsland != null) {
                    timeUntilGeiser -= delta;

                    if (timeUntilGeiser <= 0) {
                        chosenIsland.boost();
                        bubbles.allowCompletion();
                        chosenIsland = null;
                        timeUntilGeiserChosen = MathUtils.random(3, 5);
                    }
                }

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
                return 3;
            }
        },
        FIVE {
            private float timeUntilGeiserChosen;
            private float time = 4;
            private float timeUntilGeiser;
            private Island chosenIsland;
            private BubbleAttackArm bubbleArm;
            private ParticleEffectPool.PooledEffect bubbles;

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
                timeUntilGeiserChosen -= delta;
                if(timeUntilGeiserChosen <= 0 && chosenIsland == null) {
                    chosenIsland = screen.islands.random();
                    timeUntilGeiser = MathUtils.random(2, 4);
                    PositionComponent pos = Mappers.position.get(chosenIsland);
                    bubbles = screen.getPm().spawnBubbles((int) pos.getX() + 10, (int) pos.getY());
                }
                if(chosenIsland != null) {
                    timeUntilGeiser -= delta;

                    if (timeUntilGeiser <= 0) {
                        chosenIsland.boost();
                        bubbles.allowCompletion();
                        chosenIsland = null;
                        timeUntilGeiserChosen = MathUtils.random(3, 5);
                    }
                }

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
                return 3;
            }
        }
        ;

        public IntVector2 createOctopusPoint() { throw new RuntimeException("Not Implemented"); }

        public float getOctopusSpeed() { throw new RuntimeException("Not Implemented");}

        public void processAttack(float delta, BossFightScreen screen, BossAttackManager bam) { throw new RuntimeException("Not Implemented");}

        public float getOctopusLerpPower() {  throw new RuntimeException("Not Implemented"); }

        public int getOctopusLife() {
            throw new RuntimeException("Not Implemented");
        }
    }

    public BossFightScreen(GameAccessor ga) {
        super(ga);
    }

    public void addScheduledEntity(float time, ScreenEntity entity) {
        scheduledEntities.put(entity, time);
    }

    @Override
    public void show() {
        phase = FightPhase.FIVE;
        pm = new ParticleManager(batch, shapeRenderer);
        addEntity(pm);

        physicsSystem = getEntitySystem(PhysicsSystem.class);
        renderSystem = getEntitySystem(RenderSystem.class);
        renderSystem.setBackground(Color.GRAY);
        this.bam = new BossAttackManager(this);

        islands = new DelayedRemovalArray<Island>();
        islands.add(new Island(batch, shapeRenderer, 300, 20, 100, 20));
        islands.add(new Island(batch, shapeRenderer, 150, 20, 100, 20));

        for (int i = 0; i < islands.size; i++) {
            addEntity(islands.get(i));
        }
        attackBoxes = new DelayedRemovalArray<>();

        addEntity(new TargetOctopus(batch, shapeRenderer));
        addEntity(player = new DashniPlayer(batch, shapeRenderer));
        addEntity(new Background(batch, shapeRenderer));
        addEntity(new Water(batch, shapeRenderer));


        pdr = new PhysicsDebugRenderer();

        scheduledEntities = new ConcurrentHashMap<>();
    }

    public void spawnBubble(float x, float y) {
        PositionComponent playerPos = Mappers.position.get(player);
        addEntity(new AttackBubble(batch, shapeRenderer, x, y, playerPos.getX(), playerPos.getY()));
    }

    @Override
    public void tick(float v) {
        if(transitioning) return;
        if(!headUp) {
            phase.processAttack(v, this, bam);
        }
        pm.additionalTick(v);
        for (ScreenEntity screenEntity : scheduledEntities.keySet()) {
            scheduledEntities.put(screenEntity, scheduledEntities.get(screenEntity) - v);
            if(scheduledEntities.get(screenEntity) <= 0) {
                addEntity(screenEntity);
                scheduledEntities.remove(screenEntity);
            }
        }


        if(Gdx.input.isKeyJustPressed(Keys.R)) {
            BossAttacks.stairwayII(batch, shapeRenderer, this);
        }
        if(Gdx.input.isKeyJustPressed(Keys.E)) {
            BossAttacks.closeLine(batch, shapeRenderer, this, (int) Mappers.position.get(player).getY());
        }
        if(Gdx.input.isKeyJustPressed(Keys.T)) {
            islands.get(0).boost();
        }
        if(Gdx.input.isKeyJustPressed(Keys.J)) {
            addEntity(new BubbleAttackArm(batch, shapeRenderer, 100, 0.1f, 10, 0.2f));
        }
        tickEntities(v);
        updateEngine(v);
    }

    @Override
    public void render() {

        renderSystem.getViewport().apply();
        batch.setProjectionMatrix(renderSystem.getCamera().combined);

        pdr.render(physicsSystem.getPhysicsWorld(), renderSystem.getViewport().getCamera().combined);

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (int i = 0; i < attackBoxes.size; i++) {
            Rectangle r = attackBoxes.get(i);
            shapeRenderer.rect(r.x, r.y, r.width, r.height);
        }
        shapeRenderer.end();
        renderEntities();
    }



    @Override
    public void notified(Object notifier, int notification) {
        super.notified(notifier, notification);
        if(notification == Notifications.TARGET_OCTOPUS_DOWN) {
            addEntity(new OctopusPunchHead(batch, shapeRenderer));
            headUp = true;
        } else if(notification == Notifications.OCTOPUS_EYE_GONE) {
            headUp = false;
        } else if(notification == Notifications.DASHNI_DEAD) {
            ga.transitionTo(new BossFightScreen(ga), Transitions.linearFade(2, batch, shapeRenderer));
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
        renderSystem.resize(width, height);
    }

    @Override
    public void disposeAssets() {
        pdr.dispose();
    }

    public Array<Rectangle> getAttackBoxes() {
        return attackBoxes;
    }

    public Entity getPlayer() {
        return player;
    }

    public ParticleManager getPm() {
        return pm;
    }

    public boolean isHeadUp() {
        return headUp;
    }
}
