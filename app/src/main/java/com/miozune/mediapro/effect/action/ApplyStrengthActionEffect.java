package com.miozune.mediapro.effect.action;

/**
 * 筋力を付与。
 */
public final class ApplyStrengthActionEffect implements ActionEffect {
    private final int bonus;
    private final int turns;

    public ApplyStrengthActionEffect(int bonus, int turns) {
        this.bonus = Math.max(0, bonus);
        this.turns = Math.max(0, turns);
    }

    @Override
    public boolean apply(ActionContext context) {
        if (bonus <= 0 || turns <= 0) {
            return true;
        }
        context.player().addStrength(bonus, turns);
        return true;
    }
}
