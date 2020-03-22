package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.physics.PBodyType;
import com.redsponge.redengine.screen.components.PhysicsComponent;
import com.redsponge.redengine.screen.components.RenderCentering;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.screen.systems.RenderSystem;
import com.redsponge.redengine.utils.IntVector2;
import com.redsponge.redengine.utils.MathUtilities;

public class TargetOctopus extends ScreenEntity {

    private TextureComponent tex;

    private IntVector2 targetLoc;
    private Vector2 singularVel;

    public TargetOctopus(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
        targetLoc = new IntVector2();
        singularVel = new Vector2();
        singularVel.set(1, 1);
    }

    @Override
    public void added() {
        pos.set(screen.getScreenWidth() / 2f, screen.getScreenHeight() / 2f);
        size.set(64, 64);
        generateTarget();
    }

    private void generateTarget() {
        targetLoc.set(MathUtils.random(screen.getScreenWidth()), MathUtils.random(screen.getScreenHeight()));
    }

    @Override
    public void additionalTick(float delta) {
        if(Vector2.dst2(pos.getX() + size.getX() / 2f, pos.getY() + size.getY() / 2f, targetLoc.x, targetLoc.y) < 1000) {
            generateTarget();
        }


        float diffX = targetLoc.x - (pos.getX() + size.getX() / 2f);
        float diffY = targetLoc.y - (pos.getY() + size.getY() / 2f);

        float angle = (float) Math.atan2(diffY, diffX);
        float wantedVX = (float) Math.cos(angle);
        float wantedVY = (float) Math.sin(angle);

        vel.setX(MathUtilities.lerp(vel.getX(), wantedVX * 100, 0.01f));
        vel.setY(MathUtilities.lerp(vel.getY(), wantedVY * 100, 0.01f));
    }

    @Override
    public void additionalRender() {
        shapeRenderer.setProjectionMatrix(screen.getEntitySystem(RenderSystem.class).getCamera().combined);
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(pos.getX(), pos.getY(), size.getX(), size.getY());
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(targetLoc.x, targetLoc.y, 10);
        shapeRenderer.end();
    }

    @Override
    public void loadAssets() {
        tex = new TextureComponent(assets.getTextureRegion("targetIdle"));
        add(tex);
    }
}
