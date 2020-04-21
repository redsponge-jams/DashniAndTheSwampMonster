package com.redsponge.dbf.bossfight.visual;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.redsponge.redengine.assets.Fonts;
import com.redsponge.redengine.screen.components.NinePatchComponent;
import com.redsponge.redengine.screen.components.RenderCentering;
import com.redsponge.redengine.screen.components.RenderRunnableComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Tooltip extends ScreenEntity {

    private NinePatchComponent npc;
    private String text;

    private BitmapFont font;
    private GlyphLayout textLayout;

    private float timeExisting;
    private boolean isExiting;
    private float timeExiting;
    private float timeLeftExiting;

    public Tooltip(SpriteBatch batch, ShapeRenderer shapeRenderer, String text) {
        super(batch, shapeRenderer);
        this.text = text;
    }

    @Override
    public void loadAssets() {
        npc = new NinePatchComponent(new NinePatch(assets.get("tooltipBackground", Texture.class), 6, 6, 6, 6));
        render.setCentering(RenderCentering.CENTER);
        pos.set(screen.getScreenWidth() / 2f, 20, 22);
        size.set(300, 16);

        add(npc);
        add(new RenderRunnableComponent(this::renderText));

        font = Fonts.getFont("pixelmix", 8);
        setText(text);
    }

    @Override
    public void additionalTick(float delta) {
        timeExisting += delta;
        if(timeExisting < 1) {
            render.getColor().a = timeExisting / 1;
        } else {
            render.getColor().a = 1;
        }
        if(isExiting) {
            timeLeftExiting -= delta;
            if(timeLeftExiting <= 0) remove();
            render.getColor().a = timeLeftExiting / timeExiting;
        }
    }

    public Tooltip setText(String text) {
        this.text = text;
        this.textLayout = new GlyphLayout(font, text);
        this.size.setY((int) (textLayout.height + 8));
        this.size.setX((int) (textLayout.width + 16));
        return this;
    }

    public String getText() {
        return text;
    }

    private void renderText() {
        font.setColor(render.getColor());
        font.draw(batch, text, pos.getX() - textLayout.width / 2f, pos.getY() + textLayout.height / 2f, textLayout.width, Align.center, false);
    }

    public void exit(float time) {
        timeExiting = time;
        timeLeftExiting = time;
        isExiting = true;
    }
}
