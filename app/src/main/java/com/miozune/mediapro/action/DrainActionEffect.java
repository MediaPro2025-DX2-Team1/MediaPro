package com.miozune.mediapro.action;

import com.miozune.mediapro.effect.EffectType;
import com.miozune.mediapro.enemy.EnemyModel;

/**
 * 単体攻撃し、撃破時に自分を回復。
 */
public final class DrainActionEffect implements ActionEffect {
    private final int baseDamage;
    private final int healAmount;

    public DrainActionEffect(int baseDamage, int healAmount) {
        this.baseDamage = Math.max(0, baseDamage);
        this.healAmount = Math.max(0, healAmount);
    }

    @Override
    public boolean apply(ActionContext context) {
        EnemyModel target = context.resolveTarget();
        if (target == null) {
            return false;
        }

        int attackDamage = context.player().applyOutgoingDamageModifiers(baseDamage);
        int beforeHp = target.getHp();
        target.receiveDamage(attackDamage);
        context.stage().triggerEffect(EffectType.PLAYER_ATTACK, target);

        if (beforeHp > 0 && target.isDead()) {
            context.player().heal(healAmount);
            context.stage().triggerEffect(EffectType.HEAL, context.player());
        }
        return true;
    }
}
