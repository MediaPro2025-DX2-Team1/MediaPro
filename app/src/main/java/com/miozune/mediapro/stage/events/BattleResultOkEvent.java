package com.miozune.mediapro.stage.events;

/**
 * バトル結果画面でOKボタンが押されたことを示すイベント。
 *
 * @param isVictory プレイヤーが勝利したかどうか
 */
public record BattleResultOkEvent(boolean isVictory) implements OverlayEvent {}
