package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.physics.PhysicsDebugRenderer;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.screen.components.Mappers;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.screen.systems.PhysicsSystem;
import com.redsponge.redengine.screen.systems.RenderSystem;
import com.redsponge.redengine.utils.GameAccessor;
import com.redsponge.redengine.utils.holders.Pair;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class BossFightScreen extends AbstractScreen {

    private RenderSystem renderSystem;
    private PhysicsDebugRenderer pdr;
    private PhysicsSystem physicsSystem;

    private DelayedRemovalArray<Rectangle> attackBoxes;

    private ConcurrentHashMap<ScreenEntity, Float> scheduledEntities;
    private DashniPlayer player;

    public BossFightScreen(GameAccessor ga) {
        super(ga);
    }

    public void addScheduledEntity(float time, ScreenEntity entity) {
        scheduledEntities.put(entity, time);
    }

    @Override
    public void show() {
        physicsSystem = getEntitySystem(PhysicsSystem.class);
        renderSystem = getEntitySystem(RenderSystem.class);
        renderSystem.setBackground(Color.GRAY);

        attackBoxes = new DelayedRemovalArray<>();
        attackBoxes.add(new Rectangle(10, 10, 20, 20));

        addEntity(new TargetOctopus(batch, shapeRenderer));
        addEntity(player = new DashniPlayer(batch, shapeRenderer));
        addEntity(new Island(batch, shapeRenderer, 0, 0, getScreenWidth(), 10));
        addEntity(new Island(batch, shapeRenderer, 300, 50, 100, 10));
        addEntity(new Background(batch, shapeRenderer));
        addEntity(new Waste(batch, shapeRenderer));


        pdr = new PhysicsDebugRenderer();

        scheduledEntities = new ConcurrentHashMap<>();
    }

    @Override
    public void tick(float v) {
        for (ScreenEntity screenEntity : scheduledEntities.keySet()) {
            scheduledEntities.put(screenEntity, scheduledEntities.get(screenEntity) - v);
            if(scheduledEntities.get(screenEntity) <= 0) {
                addEntity(screenEntity);
                scheduledEntities.remove(screenEntity);
            }
        }

        if(Gdx.input.isKeyJustPressed(Keys.R)) {
            BossAttacks.stairs(batch, shapeRenderer, this);
        }
        if(Gdx.input.isKeyJustPressed(Keys.E)) {
            BossAttacks.closeLine(batch, shapeRenderer, this, (int) Mappers.position.get(player).getY());
        }
        tickEntities(v);
        updateEngine(v);
    }

    @Override
    public void render() {
        renderSystem.getViewport().apply();
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
}
