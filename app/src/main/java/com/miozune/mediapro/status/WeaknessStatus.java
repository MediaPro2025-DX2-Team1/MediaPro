package com.miozune.mediapro.status;

/**
 * 受けるダメージを増加させるデバフ。
 */
public final class WeaknessStatus implements StatusEffect {
    private final int bonusDamage;
    private int remainingTurns;

    public WeaknessStatus(int remainingTurns) {
        this(1, remainingTurns);
    }

    public WeaknessStatus(int bonusDamage, int remainingTurns) {
        this.bonusDamage = Math.max(0, bonusDamage);
        this.remainingTurns = Math.max(0, remainingTurns);
    }

    public int bonusDamage() {
        return bonusDamage;
    }

    public int remainingTurns() {
        return remainingTurns;
    }

    @Override
    public void onTurnStart() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    @Override
    public int onOutgoingDamage(int baseDamage) {
        return baseDamage;
    }

    @Override
    public int onIncomingDamage(int baseDamage) {
        return baseDamage + bonusDamage;
    }

    @Override
    public boolean isExpired() {
        return remainingTurns <= 0;
    }
}
