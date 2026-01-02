package com.miozune.mediapro.player.events;

import com.miozune.mediapro.player.PlayerModel;

/**
 * プレイヤーのHP変更イベント。
 * 
 * @param player イベント発生源のPlayerModel
 * @param oldHp 変更前のHP
 * @param newHp 変更後のHP
 */
public record PlayerHpChangedEvent(
    PlayerModel player,
    int oldHp,
    int newHp
) implements PlayerPropertyChangeEvent {

    @Override
    public PlayerModel getPlayer() {
        return player;
    }

    /**
     * HP変更量を取得する。
     * 
     * @return HP変更量（正の値は回復、負の値はダメージ）
     */
    public int getDelta() {
        return newHp - oldHp;
    }
}
