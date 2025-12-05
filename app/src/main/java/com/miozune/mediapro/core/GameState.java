package com.miozune.mediapro.core;

/**
 * ゲームの状態を表す列挙型。
 */
public enum GameState {
    /** 初期化中 */
    INITIALIZING,
    /** タイトル画面 */
    TITLE,
    /** ゲームプレイ中 */
    PLAYING,
    /** 一時停止中 */
    PAUSED,
    /** ゲームオーバー */
    GAME_OVER
}
