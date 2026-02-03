package com.miozune.mediapro.stage.events;

/**
 * オーバーレイ操作に関連するイベントの基底インターフェース。
 */
public sealed interface OverlayEvent permits BattleResultOkEvent, OverlayClosedEvent {}
