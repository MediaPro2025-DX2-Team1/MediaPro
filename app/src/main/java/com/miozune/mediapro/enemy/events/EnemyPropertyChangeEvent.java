package com.miozune.mediapro.enemy.events;

import com.miozune.mediapro.enemy.EnemyModel;

/**
 * Enemy の状態変化を表すイベントの基底インターフェース
 */
public sealed interface EnemyPropertyChangeEvent
    permits EnemyHpChangedEvent, EnemyNameChangedEvent {
    
    /**
     * このイベントの発生元となった Enemy を取得
     * 
     * @return イベント発生元の EnemyModel
     */
    EnemyModel getEnemy();
}
