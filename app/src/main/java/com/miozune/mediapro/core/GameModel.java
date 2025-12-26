package com.miozune.mediapro.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ゲームの状態を管理するモデルクラス。
 * PropertyChangeSupportを使用してビューに変更を通知する。
 */
public class GameModel {
    
    public static final String PROP_STATE = "state";
    public static final String PROP_SCORE = "score";
    
    private final PropertyChangeSupport pcs;
    
    private GameState state;
    private int score;
    
    /**
     * GameModelを初期化する。
     */
    public GameModel() {
        this.pcs = new PropertyChangeSupport(this);
        this.state = GameState.INITIALIZING;
        this.score = 0;
    }
    
    /**
     * プロパティ変更リスナーを追加する。
     *
     * @param listener リスナー
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    /**
     * プロパティ変更リスナーを削除する。
     *
     * @param listener リスナー
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
    /**
     * ゲームの状態を取得する。
     *
     * @return 現在のゲーム状態
     */
    public GameState getState() {
        return state;
    }
    
    /**
     * ゲームの状態を設定する。
     *
     * @param state 新しいゲーム状態
     */
    public void setState(GameState state) {
        GameState oldState = this.state;
        this.state = state;
        pcs.firePropertyChange(PROP_STATE, oldState, state);
    }
    
    /**
     * スコアを取得する。
     *
     * @return 現在のスコア
     */
    public int getScore() {
        return score;
    }
    
    /**
     * スコアを設定する。
     *
     * @param score 新しいスコア
     */
    public void setScore(int score) {
        int oldScore = this.score;
        this.score = score;
        pcs.firePropertyChange(PROP_SCORE, oldScore, score);
    }
    
    /**
     * ゲームをリセットする。
     */
    public void reset() {
        setScore(0);
        setState(GameState.INITIALIZING);
    }
}
