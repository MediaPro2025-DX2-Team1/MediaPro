package com.miozune.mediapro.stage.events;

/**
 * オーバーレイ操作に関連するイベントの基底インターフェース。
 * sealed interfaceにより、許可されたイベントのみを定義できる。
 */
public sealed interface OverlayEvent permits BattleResultOkEvent, OverlayClosedEvent {
    // 共通メソッドは必要に応じて追加
}
