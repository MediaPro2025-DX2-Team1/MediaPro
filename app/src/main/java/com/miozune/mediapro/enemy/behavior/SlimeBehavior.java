package com.miozune.mediapro.enemy.behavior;

/**
 * スライム: 2ダメージ → 1ダメージx4 → ループ。
 */
public class SlimeBehavior implements EnemyBehavior {
    private static final int FIRST_HIT_DAMAGE = 2;
    private static final int SMALL_HIT_DAMAGE = 1;
    private static final int SMALL_HIT_TIMES = 4;

    private int step = 0;

    @Override
    public void performTurn(EnemyActionContext context) {
        if (step == 0) {
            context.attackPlayer(FIRST_HIT_DAMAGE);
        } else {
            context.attackPlayer(SMALL_HIT_DAMAGE, SMALL_HIT_TIMES);
        }
        step = (step + 1) % 2;
    }
}
