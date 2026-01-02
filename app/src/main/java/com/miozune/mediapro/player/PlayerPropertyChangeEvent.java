package com.miozune.mediapro.player;

/**
 * プレイヤーのプロパティ変更を表すイベントの基底インターフェース。
 * sealed interfaceにより、許可されたイベントのみを定義できる。
 */
public sealed interface PlayerPropertyChangeEvent 
    permits PlayerHpChangedEvent, PlayerManaChangedEvent, PlayerNameChangedEvent {

    /**
     * イベントの発生源となるPlayerModelを取得する。
     * 
     * @return PlayerModelインスタンス
     */
    PlayerModel getPlayer();
}
