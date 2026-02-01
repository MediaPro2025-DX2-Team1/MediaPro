package com.miozune.mediapro.enemy.behavior;

/** 敵の行動パターンを表すインターフェース。 */
public interface EnemyBehavior {
    void performTurn(EnemyActionContext context);
}
