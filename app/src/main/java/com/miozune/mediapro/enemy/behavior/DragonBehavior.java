package com.miozune.mediapro.enemy.behavior;

/**
 * ドラゴン: 20ダメージ → 弱体2ターン付与 → 1ダメージx15回（弱体が乗る）。
 */
public class DragonBehavior implements EnemyBehavior {
    private static final int FIRST_ATTACK = 20;
    private static final int WEAK_TURNS = 2;
    private static final int MULTI_DAMAGE = 1;
    private static final int MULTI_TIMES = 15;

    private int step = 0;

    @Override
    public void performTurn(EnemyActionContext context) {
        switch (step) {
            case 0 -> context.attackPlayer(FIRST_ATTACK);
            case 1 -> context.applyWeaknessToPlayer(WEAK_TURNS);
            case 2 -> context.attackPlayer(MULTI_DAMAGE, MULTI_TIMES);
        }
        step = (step + 1) % 3;
    }
}
