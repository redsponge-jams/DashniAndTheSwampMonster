
package com.redsponge.dbf.splash;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class ToastySplashScreenRenderer implements Disposable {

    private FitViewport viewport;
    private Stage stage;
    private SpriteBatch batch;
    private AssetManager am;
    private boolean complete;
    private Image glasses;
    private Image name;

    public ToastySplashScreenRenderer(SpriteBatch batch, AssetManager am) {
        this.batch = batch;
        this.am = am;
    }

    public void begin() {
        this.viewport = new FitViewport(640, 480);
        this.stage = new Stage(viewport, batch);
        this.complete = false;

        TextureAtlas atlas = this.am.get("textures/splashscreen/splashscreen_textures.atlas", TextureAtlas.class);

        glasses = new Image(atlas.findRegion("toasty/glasses"));
        glasses.setOrigin(Align.center);
        glasses.setScale(16);
        glasses.setPosition(viewport.getWorldWidth() / 2, -glasses.getHeight() * 5);
        glasses.addAction(sequence(moveTo(glasses.getX(), viewport.getWorldHeight() / 3 * 2, 1f, Interpolation.swingOut),
                delay(0.5f), moveBy(-10, 0, 0.1f), moveBy(20, 0, 0.1f),
                moveBy(-20, 0, 0.1f), moveBy(10, 0, 0.1f),
                delay(0.2f), parallel(moveBy(0, 60,0.2f, Interpolation.exp5), scaleTo(14, 14, 0.2f, Interpolation.exp5)),
                delay(0.2f), parallel(moveBy(0, -60,0.2f, Interpolation.swingOut), scaleTo(16, 16, 0.2f, Interpolation.swingOut)),
                delay(1.6f), scaleTo(0, 0, 0.5f, Interpolation.exp5)));

        name = new Image(atlas.findRegion("toasty/name"));
        name.setOrigin(Align.center);
        name.setPosition(viewport.getWorldWidth() / 2 - name.getWidth() / 2, viewport.getWorldHeight() / 3 * 2 - 40);
        name.setScale(0);
        name.addAction(sequence(delay(2.7f), parallel(scaleTo(3, 3, 0.5f, Interpolation.swingOut), moveBy(0, -150, 0.5f, Interpolation.swingOut)),
                delay(0.2f), scaleBy(0.2f, 0.2f, 0.2f), delay(0.2f), scaleBy(-0.2f, -0.2f, 0.2f),
                delay(0.5f), scaleTo(0, 0, 0.5f, Interpolation.exp5)));
        stage.addActor(glasses);
        stage.addActor(name);
    }

    public void tick(float delta) {
        stage.act(delta);
    }

    public void render() {
        viewport.apply(true);

        stage.draw();

        if(name.getActions().size == 0) {
            complete = true; // TODO: Change to TRUE
        }
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public boolean isComplete() {
        return complete;
    }

    @Override
    public void dispose() {
    }

}
