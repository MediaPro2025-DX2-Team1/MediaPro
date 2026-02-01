package com.miozune.mediapro.status;

/**
 * 攻撃力を一定ターン増加させるバフ。
 */
public final class StrengthStatus implements StatusEffect {
    private int bonus;
    private int remainingTurns;

    public StrengthStatus(int bonus, int remainingTurns) {
        this.bonus = Math.max(0, bonus);
        this.remainingTurns = Math.max(0, remainingTurns);
    }

    public int bonus() {
        return bonus;
    }

    public int remainingTurns() {
        return remainingTurns;
    }

    /** 同種の強化を重ね掛けする。 */
    public void stack(StrengthStatus other) {
        if (other == null) {
            return;
        }
        bonus = Math.max(0, bonus + other.bonus());
        remainingTurns = Math.max(remainingTurns, other.remainingTurns());
    }

    @Override
    public void onTurnStart() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    @Override
    public int onOutgoingDamage(int baseDamage) {
        return baseDamage + bonus;
    }

    @Override
    public int onIncomingDamage(int baseDamage) {
        return baseDamage;
    }

    @Override
    public boolean isExpired() {
        return remainingTurns <= 0;
    }
}
