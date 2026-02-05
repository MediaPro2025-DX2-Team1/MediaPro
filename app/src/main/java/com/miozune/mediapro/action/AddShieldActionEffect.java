package com.miozune.mediapro.action;

import com.miozune.mediapro.effect.EffectType;

/**
 * シールドを得る。
 */
public final class AddShieldActionEffect implements ActionEffect {
    private final int amount;

    public AddShieldActionEffect(int amount) {
        this.amount = Math.max(0, amount);
    }

    @Override
    public boolean apply(ActionContext context) {
        if (amount <= 0) {
            return true;
        }
        context.player().addShield(amount);
        context.stage().triggerEffect(EffectType.SHIELD, context.player());

        return true;
    }
}
