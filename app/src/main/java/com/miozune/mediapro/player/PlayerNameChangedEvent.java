package com.miozune.mediapro.player;

/**
 * プレイヤー名変更イベント。
 * 
 * @param player イベント発生源のPlayerModel
 * @param oldName 変更前の名前
 * @param newName 変更後の名前
 */
public record PlayerNameChangedEvent(
    PlayerModel player,
    String oldName,
    String newName
) implements PlayerPropertyChangeEvent {

    @Override
    public PlayerModel getPlayer() {
        return player;
    }
}
