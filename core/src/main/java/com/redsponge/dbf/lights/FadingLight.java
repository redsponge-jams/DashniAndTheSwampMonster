package com.redsponge.dbf.lights;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.redsponge.redengine.lighting.PointLight;

public class FadingLight extends PointLight {

    public FadingLight(float x, float y, float radius, AtlasRegion texture) {
        super(x, y, radius, texture);
        time = MathUtils.random(MathUtils.PI2);
    }


    private float time;

    public void tick(float delta) {
        time += delta;
        getColor().a = MathUtils.map(-1, 1, 0.3f, 0.7f, (float) Math.sin((time)));
    }
}
