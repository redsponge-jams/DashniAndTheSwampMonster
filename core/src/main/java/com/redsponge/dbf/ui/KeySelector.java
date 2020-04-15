package com.redsponge.dbf.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.lang.reflect.Field;
import java.util.HashMap;

public class KeySelector extends TextButton {

    private int key;
    private boolean active;
    private Field connectedField;

    private KeySelectorGroup keySelectorGroup;
    private Object fieldOwner;

    public KeySelector(Skin skin, Class<?> ownerClass, Object fieldOwner, String fieldName) {
        super("", skin);

        try {
            connectedField = ownerClass.getDeclaredField(fieldName);
            setKey(connectedField.getInt(fieldOwner));
            this.fieldOwner = fieldOwner;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                keySelectorGroup.deactivateAll();
                setActive(true);
                getStage().setKeyboardFocus(KeySelector.this);
            }
        });


        addListener(new InputListener() {

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return super.keyUp(event, keycode);
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(active) {
                    setKey(keycode);
                    active = false;
                    getStage().setKeyboardFocus(null);
                    return true;
                }
                return super.keyDown(event, keycode);
            }
        });
    }

    public void setKey(int key) {
        this.key = key;
        setText(stringifyKey(key));
        try {
            connectedField.set(fieldOwner, key);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static final HashMap<Integer, String> KEY_STRINGS = new HashMap<Integer, String>(){{
        put(Keys.UP, "^");
        put(Keys.DOWN, "v");
        put(Keys.RIGHT, ">");
        put(Keys.LEFT, "<");
    }};

    public static String stringifyKey(int key) {
        if(KEY_STRINGS.containsKey(key)) {
            return KEY_STRINGS.get(key);
        }
        return Keys.toString(key);
    }

    public void setKeySelectorGroup(KeySelectorGroup keySelectorGroup) {
        this.keySelectorGroup = keySelectorGroup;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        NinePatch toDraw = active ? getSkin().getPatch("button_hold") : getSkin().getPatch("button_reg");
        toDraw.draw(batch, getX(), getY(), getWidth(), getHeight());

        BitmapFont font = getSkin().getFont("pixelmix16");

        GlyphLayout layout = new GlyphLayout(font, getText());
        font.draw(batch, getText(), getX(), getY() + getHeight() / 2 + layout.height / 2, getWidth(), Align.center, false);
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}