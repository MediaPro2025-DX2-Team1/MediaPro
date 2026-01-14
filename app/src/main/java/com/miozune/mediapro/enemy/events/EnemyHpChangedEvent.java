package com.miozune.mediapro.enemy.events;

import com.miozune.mediapro.enemy.EnemyModel;

/**
 * Enemy の HP が変更されたことを表すイベント
 * 
 * @param enemy イベント発生元の EnemyModel
 * @param oldHp 変更前の HP
 * @param newHp 変更後の HP
 */
public record EnemyHpChangedEvent(
    EnemyModel enemy,
    int oldHp,
    int newHp
) implements EnemyPropertyChangeEvent {
    
    @Override
    public EnemyModel getEnemy() {
        return enemy;
    }
    
    /**
     * HP の変化量を取得
     * 
     * @return HP の変化量（正の値は回復、負の値はダメージ）
     */
    public int getDelta() {
        return newHp - oldHp;
    }
}
