package com.miozune.mediapro.status;

/**
 * 受けるダメージを軽減する一時シールド。
 * プレイヤーターン開始時に失われる。
 */
public final class ShieldStatus implements StatusEffect {
    private int amount;

    public ShieldStatus(int amount) {
        this.amount = Math.max(0, amount);
    }

    public int amount() {
        return amount;
    }

    public void addShield(int value) {
        amount = Math.max(0, amount + value);
    }

    @Override
    public void onTurnStart() {
        amount = 0;
    }

    @Override
    public int onOutgoingDamage(int baseDamage) {
        return baseDamage;
    }

    @Override
    public int onIncomingDamage(int baseDamage) {
        if (amount <= 0) {
            return baseDamage;
        }
        int remainingDamage = Math.max(0, baseDamage - amount);
        amount = Math.max(0, amount - baseDamage);
        return remainingDamage;
    }

    @Override
    public boolean isExpired() {
        return amount <= 0;
    }
}
