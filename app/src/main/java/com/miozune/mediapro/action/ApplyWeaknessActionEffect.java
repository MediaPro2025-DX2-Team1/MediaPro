package com.miozune.mediapro.action;

import com.miozune.mediapro.effect.EffectType;
import com.miozune.mediapro.enemy.EnemyModel;

/**
 * 敵に弱体を付与。
 */
public final class ApplyWeaknessActionEffect implements ActionEffect {
    private final int turns;

    public ApplyWeaknessActionEffect(int turns) {
        this.turns = Math.max(0, turns);
    }

    @Override
    public boolean apply(ActionContext context) {
        EnemyModel target = context.resolveTarget();
        if (target == null || turns <= 0) {
            return false;
        }
        target.addWeakness(turns);
        context.stage().triggerEffect(EffectType.DEBUFF, target);

        return true;
    }
}
