package com.miozune.mediapro.enemy.behavior;

/**
 * ゴブリン: 4ダメージと7ダメージを交互に。
 */
public class GoblinBehavior implements EnemyBehavior {
    private static final int FIRST_ATTACK = 4;
    private static final int SECOND_ATTACK = 7;

    private boolean useFirst = true;

    @Override
    public void performTurn(EnemyActionContext context) {
        if (useFirst) {
            context.attackPlayer(FIRST_ATTACK);
        } else {
            context.attackPlayer(SECOND_ATTACK);
        }
        useFirst = !useFirst;
    }
}
