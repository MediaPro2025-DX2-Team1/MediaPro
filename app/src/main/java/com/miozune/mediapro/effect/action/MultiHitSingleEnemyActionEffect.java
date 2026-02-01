package com.miozune.mediapro.effect.action;

import com.miozune.mediapro.enemy.EnemyModel;

/**
 * 敵単体へ複数回攻撃。
 */
public final class MultiHitSingleEnemyActionEffect implements ActionEffect {
    private final int baseDamage;
    private final int hits;

    public MultiHitSingleEnemyActionEffect(int baseDamage, int hits) {
        this.baseDamage = Math.max(0, baseDamage);
        this.hits = Math.max(1, hits);
    }

    @Override
    public boolean apply(ActionContext context) {
        EnemyModel target = context.resolveTarget();
        if (target == null) {
            return false;
        }
        for (int i = 0; i < hits; i++) {
            int attackDamage = context.player().applyOutgoingDamageModifiers(baseDamage);
            target.receiveDamage(attackDamage);
            if (target.isDead()) {
                break;
            }
        }
        return true;
    }
}
