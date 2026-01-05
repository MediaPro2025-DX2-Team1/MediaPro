package com.miozune.mediapro.player.events;

import com.miozune.mediapro.player.PlayerModel;

/**
 * プレイヤーのマナ変更イベント。
 * 
 * @param player イベント発生源のPlayerModel
 * @param oldMana 変更前のマナ
 * @param newMana 変更後のマナ
 */
public record PlayerManaChangedEvent(
    PlayerModel player,
    int oldMana,
    int newMana
) implements PlayerPropertyChangeEvent {

    @Override
    public PlayerModel getPlayer() {
        return player;
    }

    /**
     * マナ変更量を取得する。
     * 
     * @return マナ変更量
     */
    public int getDelta() {
        return newMana - oldMana;
    }
}
