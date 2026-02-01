package com.miozune.mediapro.player.events;

import com.miozune.mediapro.player.PlayerModel;
import com.miozune.mediapro.status.StatusEffect;
import java.util.List;

/**
 * プレイヤーに付与されているステータス一覧が変化したことを表すイベント。
 */
public record PlayerStatusesChangedEvent(
    PlayerModel player,
    List<StatusEffect> effects
) implements PlayerPropertyChangeEvent {

    public PlayerStatusesChangedEvent {
        effects = List.copyOf(effects);
    }

    @Override
    public PlayerModel getPlayer() {
        return player;
    }
}
