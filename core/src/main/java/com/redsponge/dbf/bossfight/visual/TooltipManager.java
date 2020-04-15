package com.redsponge.dbf.bossfight.visual;

import com.badlogic.gdx.Input.Keys;
import com.redsponge.dbf.bossfight.BossFightScreen;
import com.redsponge.dbf.bossfight.Notifications;
import com.redsponge.dbf.bossfight.fight.DashniPlayer;
import com.redsponge.dbf.input.Input;
import com.redsponge.redengine.screen.INotified;
import com.redsponge.redengine.screen.components.Mappers;
import com.redsponge.redengine.utils.Logger;

import javax.tools.Tool;

public class TooltipManager implements INotified {

    private BossFightScreen screen;

    private float timeUntilMoveJumpHelp;
    private boolean doesNeedMoveJumpHelp;

    private boolean doesNeedAttackSmallHelp;
    private float timeUntilAttackSmallHelp;

    private boolean doesNeedAttackBigHelp;
    private float timeUntilAttackBigHelp;

    private Tooltip current;


    public TooltipManager(BossFightScreen screen) {
        this.screen = screen;
        timeUntilMoveJumpHelp = 5;
        doesNeedMoveJumpHelp = true;

        timeUntilAttackSmallHelp = 5;
        doesNeedAttackSmallHelp = true;

        timeUntilAttackBigHelp = 5;
        doesNeedAttackBigHelp = true;
    }

    public void tick(float delta) {
        if(doesNeedMoveJumpHelp) {
            timeUntilMoveJumpHelp -= delta;
            if(timeUntilMoveJumpHelp <= 0 && current == null) {
                current = new Tooltip(screen.getBatch(), screen.getShapeRenderer(), String.format("Move left with [%s] and right with [%s].\nJump with [%s]", Keys.toString(Input.KEY_LEFT), Keys.toString(Input.KEY_RIGHT), Keys.toString(Input.KEY_JUMP)));
                screen.addEntity(current);
            }
        } else if(doesNeedAttackSmallHelp) {
            timeUntilAttackSmallHelp -= delta;
            if(timeUntilAttackSmallHelp <= 0 && current == null) {
                current = new Tooltip(screen.getBatch(), screen.getShapeRenderer(), String.format("Punch using [%s].\nHold [%s] to punch upwards and [%s] to punch downwards.", Keys.toString(Input.KEY_ATTACK), Keys.toString(Input.KEY_UP), Keys.toString(Input.KEY_DOWN)));
                screen.addEntity(current);
            }
        } else if(doesNeedAttackBigHelp) {
            timeUntilAttackBigHelp -= delta;
            if(timeUntilAttackBigHelp <= 0 && current == null) {
                current = new Tooltip(screen.getBatch(), screen.getShapeRenderer(), "See that glowing eye? You know what to do.");
                screen.addEntity(current);
            }
        }
    }

    @Override
    public void notified(Object o, int i) {
        if(i == Notifications.DASHNI_LAND) {
            if(doesNeedMoveJumpHelp) {
                if(((DashniPlayer)o).getLandedOn() == Mappers.physics.get(screen.getIslands().get(0)).getBody()) {
                    doesNeedMoveJumpHelp = false;
                    if(timeUntilMoveJumpHelp <= 0 && current != null) {
                        current.exit(0.5f);
                        current = null;
                    }
                }
            }
        }
        if(i == Notifications.TARGET_OCTOPUS_DOWN) {
            doesNeedAttackSmallHelp = false;
            if(timeUntilAttackSmallHelp <= 0 && current != null) {
                current.exit(0.5f);
                current = null;
            }
        }
        if(i == Notifications.CHANGED_PHASE) {
            doesNeedAttackBigHelp = false;
            if(timeUntilAttackBigHelp <= 0 && current != null) {
                current.exit(0.5f);
                current = null;
            }
        }
    }
}
