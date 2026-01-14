package com.miozune.mediapro.enemy.events;

import com.miozune.mediapro.enemy.EnemyModel;

/**
 * Enemy の名前が変更されたことを表すイベント
 * 
 * @param enemy イベント発生元の EnemyModel
 * @param oldName 変更前の名前
 * @param newName 変更後の名前
 */
public record EnemyNameChangedEvent(
    EnemyModel enemy,
    String oldName,
    String newName
) implements EnemyPropertyChangeEvent {
    
    @Override
    public EnemyModel getEnemy() {
        return enemy;
    }
}
