package com.redsponge.dbf.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.redsponge.dbf.input.Input;

public class InputKeySelector extends KeySelectorGroup {

    private KeySelector up, down, right, left, jump, attack;

    public InputKeySelector(Skin skin) {
        super(skin);
        this.up = new KeySelector(skin, Input.class, null, "KEY_UP");
        this.down = new KeySelector(skin, Input.class, null, "KEY_DOWN");
        this.right = new KeySelector(skin, Input.class, null, "KEY_RIGHT");
        this.left = new KeySelector(skin, Input.class, null, "KEY_LEFT");
        this.jump = new KeySelector(skin, Input.class, null, "KEY_JUMP");
        this.attack = new KeySelector(skin, Input.class, null, "KEY_ATTACK");

        up.setKeySelectorGroup(this);
        down.setKeySelectorGroup(this);
        right.setKeySelectorGroup(this);
        left.setKeySelectorGroup(this);
        jump.setKeySelectorGroup(this);
        attack.setKeySelectorGroup(this);

        build();
    }

    private Label getLabel(String text) {
        Label lbl = new Label(text, getSkin());
        lbl.setFontScale(0.5f);
        return lbl;
    }

    @Override
    public void build() {
        clear();

        add(getLabel("Up")).colspan(3).center().pad(5);
        add(getLabel("Attack")).colspan(3).pad(5);
        row().pad(3);
        add(up).colspan(3).pad(5);
        add(attack).colspan(3).pad(5);

        row().pad(3);
        add(getLabel("Left")).pad(5);
        add(getLabel("Down")).pad(5);
        add(getLabel("Right")).pad(3);
        add(getLabel("Jump")).colspan(3).pad(5);
        row().pad(3);
        add(left).pad(5);
        add(down).pad(5);
        add(right).pad(5);
        add(jump).colspan(3).pad(5);

        pack();
    }
}
