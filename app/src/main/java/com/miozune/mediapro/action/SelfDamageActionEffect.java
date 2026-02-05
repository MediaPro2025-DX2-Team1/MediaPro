package com.miozune.mediapro.action;

import com.miozune.mediapro.effect.EffectType;

/**
 * 自傷効果。
 */
public final class SelfDamageActionEffect implements ActionEffect {
    private final int amount;

    public SelfDamageActionEffect(int amount) {
        this.amount = Math.max(0, amount);
    }

    @Override
    public boolean apply(ActionContext context) {
        context.player().receiveDamage(amount);
        context.stage().triggerEffect(EffectType.PLAYER_ATTACK, context.player());
        return true;
    }
}
