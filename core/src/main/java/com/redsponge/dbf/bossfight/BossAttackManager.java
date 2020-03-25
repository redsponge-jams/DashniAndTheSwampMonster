package com.redsponge.dbf.bossfight;

import com.badlogic.gdx.math.MathUtils;
import com.redsponge.redengine.screen.INotified;
import com.redsponge.redengine.screen.components.Mappers;
import com.redsponge.redengine.utils.GeneralUtils;
import com.redsponge.redengine.utils.Logger;

public class BossAttackManager implements INotified {

    private BossFightScreen screen;
    private float timeCounter;
    private boolean paused;

    private BubbleAttackArm bubbleAttacker;
    private float timeSinceBubbleAttacker;

    public BossAttackManager(BossFightScreen screen) {
        this.screen = screen;
    }

    public void update(float delta) {
        if(bubbleAttacker != null && bubbleAttacker.isRemoved()) {
            Logger.log(this, "BUBBLE ATTACKER OUT");
            bubbleAttacker = null;
            timeSinceBubbleAttacker = 0;
        }

        if(paused) return;
        timeCounter += delta;
        if(timeCounter > 5) {
            timeCounter -= 5;

            BossAttacks.attackPlayer(screen.getBatch(), screen.getShapeRenderer(), screen, (int) Mappers.position.get(screen.getPlayer()).getY());

        }
        if(bubbleAttacker == null) {
            timeSinceBubbleAttacker += delta;
            if(timeSinceBubbleAttacker > 3) {
                if(true) {
                    bubbleAttacker = new BubbleAttackArm(screen.getBatch(), screen.getShapeRenderer(), GeneralUtils.randomItem(new Integer[] {120, 420}), 0.3f, 5);
                    screen.addEntity(bubbleAttacker);
                }
            }
        }
    }

    @Override
    public void notified(Object o, int i) {
        if(i == Notifications.TARGET_OCTOPUS_DOWN) {
            stop();
        }
        if(i == Notifications.OCTOPUS_EYE_GONE) {
            resume();
        }
    }

    public void resume() {
        paused = false;
    }

    public void stop() {
        paused = true;
    }
}
