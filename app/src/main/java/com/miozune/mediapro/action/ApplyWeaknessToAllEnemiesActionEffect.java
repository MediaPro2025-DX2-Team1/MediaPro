package com.miozune.mediapro.action;

import com.miozune.mediapro.enemy.EnemyModel;

/**
 * 全ての敵に弱体を付与。
 */
public final class ApplyWeaknessToAllEnemiesActionEffect implements ActionEffect {
    private final int turns;

    public ApplyWeaknessToAllEnemiesActionEffect(int turns) {
        this.turns = Math.max(0, turns);
    }

    @Override
    public boolean apply(ActionContext context) {
        if (context.enemies() == null || context.enemies().isEmpty()) {
            return false;
        }
        if (turns <= 0) {
            return true;
        }
        for (EnemyModel enemy : context.enemies()) {
            if (enemy != null && !enemy.isDead()) {
                enemy.addWeakness(turns);
            }
        }
        return true;
    }
}
