package com.miozune.mediapro.status;

/**
 * 攻撃力を永続的に増加させるバフ。
 * 一度付与された筋力はターン経過で消えることはない。
 */
public final class StrengthStatus implements StatusEffect {
    private int bonus;

    public StrengthStatus(int bonus) {
        this.bonus = Math.max(0, bonus);
    }

    public int bonus() {
        return bonus;
    }

    /** 同種の強化を重ね掛けする。 */
    public void stack(StrengthStatus other) {
        if (other == null) {
            return;
        }
        bonus = Math.max(0, bonus + other.bonus());
    }

    @Override
    public void onTurnStart() {
        // 永続効果なので何もしない
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
        return false; // 永続効果なので期限切れなし
    }
}
