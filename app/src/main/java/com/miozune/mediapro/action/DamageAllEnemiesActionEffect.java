package com.miozune.mediapro.action;

import com.miozune.mediapro.enemy.EnemyModel;

/**
 * 全ての敵にダメージ。
 */
public final class DamageAllEnemiesActionEffect implements ActionEffect {
    private final int baseDamage;

    public DamageAllEnemiesActionEffect(int baseDamage) {
        this.baseDamage = Math.max(0, baseDamage);
    }

    @Override
    public boolean apply(ActionContext context) {
        if (context.enemies() == null || context.enemies().isEmpty()) {
            return false;
        }
        int attackDamage = context.player().applyOutgoingDamageModifiers(baseDamage);
        for (EnemyModel enemy : context.enemies()) {
            if (enemy != null && !enemy.isDead()) {
                enemy.receiveDamage(attackDamage);
            }
        }
        return true;
    }
}
