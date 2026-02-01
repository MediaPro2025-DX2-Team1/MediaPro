package com.miozune.mediapro.effect.action;

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
        return true;
    }
}
