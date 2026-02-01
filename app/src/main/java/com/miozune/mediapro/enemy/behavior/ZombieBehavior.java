package com.miozune.mediapro.enemy.behavior;

/**
 * ゾンビ: 毎ターン4ダメージ攻撃。HPが7以下ならシールド5を得る。
 */
public class ZombieBehavior implements EnemyBehavior {
    private static final int ATTACK_DAMAGE = 4;
    private static final int SHIELD_THRESHOLD = 7;
    private static final int SHIELD_AMOUNT = 5;

    private boolean appliedShield = false;

    @Override
    public void performTurn(EnemyActionContext context) {
        if (!appliedShield && context.self().getHp() <= SHIELD_THRESHOLD) {
            context.addShieldToSelf(SHIELD_AMOUNT);
            appliedShield = true;
        }
        context.attackPlayer(ATTACK_DAMAGE);
    }
}
