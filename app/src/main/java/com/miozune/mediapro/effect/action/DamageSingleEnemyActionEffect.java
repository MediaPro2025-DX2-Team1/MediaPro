package com.miozune.mediapro.effect.action;

import com.miozune.mediapro.enemy.EnemyModel;

/**
 * 敵単体にダメージ。
 */
public final class DamageSingleEnemyActionEffect implements ActionEffect {
    private final int baseDamage;

    public DamageSingleEnemyActionEffect(int baseDamage) {
        this.baseDamage = Math.max(0, baseDamage);
    }

    @Override
    public boolean apply(ActionContext context) {
        EnemyModel target = context.resolveTarget();
        if (target == null) {
            return false;
        }
        int attackDamage = context.player().applyOutgoingDamageModifiers(baseDamage);
        target.receiveDamage(attackDamage);
        return true;
    }
}
