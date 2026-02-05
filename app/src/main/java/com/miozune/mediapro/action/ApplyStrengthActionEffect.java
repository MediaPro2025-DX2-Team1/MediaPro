package com.miozune.mediapro.action;

import com.miozune.mediapro.effect.EffectType;

/**
 * 筋力を付与（永続）。
 */
public final class ApplyStrengthActionEffect implements ActionEffect {
    private final int bonus;

    public ApplyStrengthActionEffect(int bonus) {
        this.bonus = Math.max(0, bonus);
    }

    @Override
    public boolean apply(ActionContext context) {
        if (bonus <= 0) {
            return true;
        }
        context.player().addStrength(bonus);
        context.stage().triggerEffect(EffectType.BUFF, context.player());

        return true;
    }
}
