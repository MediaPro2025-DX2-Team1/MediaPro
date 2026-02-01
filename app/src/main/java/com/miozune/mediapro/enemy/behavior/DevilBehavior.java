package com.miozune.mediapro.enemy.behavior;

/**
 * デビル: 弱体2ターン付与 → 5ダメージ → 7ダメージでループ。
 */
public class DevilBehavior implements EnemyBehavior {
    private static final int WEAK_TURNS = 2;
    private static final int FIRST_ATTACK = 5;
    private static final int SECOND_ATTACK = 7;

    private int step = 0;

    @Override
    public void performTurn(EnemyActionContext context) {
        switch (step) {
            case 0 -> context.applyWeaknessToPlayer(WEAK_TURNS);
            case 1 -> context.attackPlayer(FIRST_ATTACK);
            case 2 -> context.attackPlayer(SECOND_ATTACK);
            default -> { }
        }
        step = (step + 1) % 3;
    }
}
