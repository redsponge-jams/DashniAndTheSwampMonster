package com.redsponge.dbf.intro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.dbf.menu.MenuScreen;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.assets.Fonts;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.GameAccessor;

public class IntroScreen extends AbstractScreen {


    private FitViewport viewport;
    private IntroFrame frame;
    private int frameIndex;
    private TypingLabel label;

    private Music music;

    private Animation<TextureRegion> introAnimation;

    public IntroScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        super.show();
        viewport = new FitViewport(getScreenWidth(), getScreenHeight());
        frame = IntroFrame.LONG_AGO;
        frameIndex = 0;
        music = Gdx.audio.newMusic(Gdx.files.internal("music/musica.ogg"));
        music.setVolume(0.5f);
        music.play();

        label = new TypingLabel("{SPEED=0.3} This is a testy testy test", new LabelStyle(Fonts.getFont("pixelmix", 16), Color.WHITE));
        setText(frame.getText());

        label.setWrap(true);
        label.setDebug(true);
        introAnimation = assets.getAnimation("introAnimation");
    }

    @Override
    public int getScreenWidth() {
        return 640;
    }

    @Override
    public int getScreenHeight() {
        return 360;
    }

    private void setText(String text) {
        label.setHeight(100);
        label.restart("{SLOW}" + text + " {WAIT=3} ");
        label.setPosition(getScreenWidth() / 2f, 100, Align.center);
        label.setAlignment(Align.center, Align.left);
    }


    @Override
    public void tick(float v) {
        if(transitioning) return;
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            ga.transitionTo(new MenuScreen(ga, music), Transitions.sineSlide(1, batch, shapeRenderer));
            return;
        }
        label.act(v);
        if(label.hasEnded()) {
            frameIndex++;
            if(frameIndex == IntroFrame.ALL.length) {
                ga.transitionTo(new MenuScreen(ga, music), Transitions.sineSlide(1, batch, shapeRenderer));
                return;
            }
            frame = IntroFrame.ALL[frameIndex];
            setText(frame.getText());
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(introAnimation.getKeyFrame(frameIndex), viewport.getWorldWidth() / 2 - 128, 200, 256, 72 * 2);
        label.draw(batch, 1);
        batch.end();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeType.Line);
        label.drawDebug(shapeRenderer);
        shapeRenderer.end();
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return IntroScreenAssets.class;
    }

    @Override
    public void reSize(int width, int height) {
        viewport.update(width, height, true);
    }
}
