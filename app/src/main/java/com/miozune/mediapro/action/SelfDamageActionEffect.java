package com.miozune.mediapro.action;

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
        return true;
    }
}
