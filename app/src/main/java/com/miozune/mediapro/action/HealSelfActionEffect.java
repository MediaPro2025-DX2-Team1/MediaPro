package com.miozune.mediapro.action;

import com.miozune.mediapro.effect.EffectType;

/**
 * 自分を回復。
 */
public final class HealSelfActionEffect implements ActionEffect {
    private final int amount;

    public HealSelfActionEffect(int amount) {
        this.amount = Math.max(0, amount);
    }

    @Override
    public boolean apply(ActionContext context) {
        context.player().heal(amount);
        context.stage().triggerEffect(EffectType.HEAL, context.player());

        return true;
    }
}
