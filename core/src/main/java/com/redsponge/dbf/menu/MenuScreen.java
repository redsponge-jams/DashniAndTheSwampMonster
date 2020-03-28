package com.redsponge.dbf.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.dbf.bossfight.MusicManager;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.GameAccessor;
import com.redsponge.redengine.utils.GeneralUtils;

public class MenuScreen extends AbstractScreen {

    private Texture background;

    private FitViewport viewport;

    private Stage stage;
    private Skin skin;

    private Music intro, loop;
    private boolean disposedIntro;

    public MenuScreen(GameAccessor ga, Music alreadyStartedIntro) {
        super(ga);
        this.intro = alreadyStartedIntro;
    }

    @Override
    public void show() {
        viewport = new FitViewport(640, 360);
        background = assets.get("background", Texture.class);

        skin = new Skin(Gdx.files.internal("skins/menu/menu_skin.json"));
        stage = new Stage(viewport, batch);

        Image title = new Image(assets.get("title", Texture.class));
        title.setSize(128 * 2, 72 * 2);
        title.setPosition(viewport.getWorldWidth() / 2, 400, Align.center);
        title.addAction(Actions.moveBy(0, -100, 2, Interpolation.exp5));

        stage.addActor(title);

        showMenuScreen();

        Gdx.input.setInputProcessor(stage);
        System.out.println(intro + " " + intro.getPosition() + " " + intro.isPlaying());
        loop = Gdx.audio.newMusic(Gdx.files.internal("music/musica_loop.ogg"));

        if(intro == null) {
            intro = Gdx.audio.newMusic(Gdx.files.internal("music/musica.ogg"));
            intro.setVolume(0.5f);
            intro.play();
        } else if(!intro.isPlaying()){
            intro.dispose();
            loop.setLooping(true);
            loop.setVolume(0.5f);
            loop.play();
        } else {
            intro.setOnCompletionListener((music) -> {
                intro.dispose();
                disposedIntro = true;
                loop.setVolume(0.5f);
                loop.play();
                loop.setLooping(true);
            });
        }
    }

    private void swapMenu(Runnable menuBuilder) {
        for (int i = 0; i < stage.getActors().size; i++) {
            Actor actor = stage.getActors().get(i);
            if(!(actor instanceof Image)) {
                if (i == stage.getActors().size - 1) {
                    actor.addAction(Actions.sequence(Actions.moveTo(-1000, actor.getY(), 1, Interpolation.exp5),Actions.run(menuBuilder), Actions.removeActor()));
                } else {
                    actor.addAction(Actions.sequence(Actions.moveTo(-300, actor.getY(), 1, Interpolation.exp5), Actions.removeActor()));
                }
            }
        }
    }

    public void showMenuScreen() {
        addButton(viewport.getWorldWidth() / 2, 200, 1, "Play", () -> {
            ga.transitionTo(new BossFightScreen(ga), Transitions.sineSlide(2, batch, shapeRenderer));
        });

        addButton(viewport.getWorldWidth() / 2, 150, 1.5f, "Credits", () -> {
            swapMenu(this::showCredits);
        });

        addButton(viewport.getWorldWidth() / 2, 100, 2, "Exit", () -> {
            Gdx.app.exit();
        });
    }

    private void showCredits() {
        String[] contents = {
            "Programing", "RedSponge",
            "Art & Music", "TheCrispyToasty",
            "Some sounds taken from", "freesound.org",
            "Tools Used", "Bosca Ceoil, Aseprite,\nSkin Composer"
        };
        String[] stupidSentences = {
                "I wonder how much does a shwarma cost...",
                "LibGDX is cool",
                "MINE MINE MINE MINE",
                "f(x) = d/dx * (3x^2-2x+1)",
                "Stay home & Wash your hands!",
                "Don't ignore your ice cream",
                "Dashni, Dashni, and Dashni.. AND DASHNI",
        };

        for(int i = 0; i < contents.length / 2; i++) {
            String title = contents[i * 2];
            String content = contents[i * 2 + 1];

            Label lbl = new Label(title, skin);
            lbl.setPosition(-lbl.getWidth(), (4 - i) * 50 + 30);
            lbl.addAction(Actions.moveTo(10, lbl.getY(), 1, Interpolation.exp5));

            Label lbl2 = new Label(content, skin);
            lbl2.setPosition(viewport.getWorldWidth() + lbl2.getWidth(), (4 - i) * 50 + 30);
            lbl2.addAction(Actions.moveTo(viewport.getWorldWidth() - 300, lbl2.getY(), 1, Interpolation.exp5));

            stage.addActor(lbl);
            stage.addActor(lbl2);
        }
        Label lbl = new Label(GeneralUtils.randomItem(stupidSentences), skin);
        lbl.setPosition(viewport.getWorldWidth() / 2f, -30, Align.center);
        lbl.addAction(Actions.moveBy(0, 80, 1, Interpolation.exp5));
        stage.addActor(lbl);

        addButton(viewport.getWorldWidth() / 2f, 20, 2, "Back", () -> {
            swapMenu(this::showMenuScreen);
        });
    }

    private void addButton(float x, float y, float delay, String text, Runnable onClick) {
        TextButton button = new TextButton(text, skin);
        button.setTransform(true);
        button.setOrigin(Align.center);
        button.setPosition(x, y, Align.center);
        button.setScale(0);
        button.addAction(Actions.delay(delay, Actions.scaleTo(1, 1, 1, Interpolation.exp5)));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                onClick.run();
            }
        });
        stage.addActor(button);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void tick(float v) {
        stage.act(v);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        stage.draw();
    }

    @Override
    public void reSize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return MenuScreenAssets.class;
    }

    @Override
    public void disposeAssets() {
        skin.dispose();
        if(!disposedIntro) {
            intro.dispose();
        }
        loop.dispose();
    }
}