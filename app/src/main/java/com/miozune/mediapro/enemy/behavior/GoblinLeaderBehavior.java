package com.miozune.mediapro.enemy.behavior;

import com.miozune.mediapro.enemy.EnemyType;

/**
 * ゴブリンリーダー:
 * 1T目のみゴブリン2体召喚 → 5ダメージx2 (a) → 自身にシールド10 + 筋力1 → 5ダメージ → aへループ。
 */
public class GoblinLeaderBehavior implements EnemyBehavior {
    private static final int ATTACK_DAMAGE = 5;
    private static final int MULTI_ATTACK_TIMES = 2;
    private static final int BUFF_SHIELD = 10;
    private static final int BUFF_STRENGTH = 1;

    private int step = 0;
    private boolean summoned = false;

    @Override
    public void performTurn(EnemyActionContext context) {
        if (!summoned) {
            context.summon(EnemyType.GOBLIN);
            context.summon(EnemyType.GOBLIN);
            summoned = true;
            return;
        }

        switch (step) {
            case 0 -> context.attackPlayer(ATTACK_DAMAGE, MULTI_ATTACK_TIMES);
            case 1 -> {
                context.addShieldToSelf(BUFF_SHIELD);
                context.addStrengthToSelf(BUFF_STRENGTH);
            }
            case 2 -> context.attackPlayer(ATTACK_DAMAGE);
            default -> { }
        }
        step = (step + 1) % 3;
    }
}
