package com.miozune.mediapro.enemy.events;

import com.miozune.mediapro.enemy.EnemyModel;
import com.miozune.mediapro.status.StatusEffect;
import java.util.List;

/**
 * 敵に付与されているステータス一覧が変化したことを表すイベント。
 */
public record EnemyStatusesChangedEvent(
    EnemyModel enemy,
    List<StatusEffect> effects
) implements EnemyPropertyChangeEvent {

    public EnemyStatusesChangedEvent {
        effects = List.copyOf(effects);
    }

    @Override
    public EnemyModel getEnemy() {
        return enemy;
    }
}
