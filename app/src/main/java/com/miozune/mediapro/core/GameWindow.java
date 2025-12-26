package com.miozune.mediapro.core;

import javax.swing.JFrame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * ゲームのメインウィンドウ。
 * GameModelの変更を監視してウィンドウタイトルを更新する。
 * 描画処理はGamePanelに委譲する。
 */
public class GameWindow extends JFrame implements PropertyChangeListener {
    
    private static final String TITLE = "MediaPro Game";
    
    private final GameModel model;
    private final GamePanel panel;
    
    /**
     * GameWindowを初期化する。
     *
     * @param model ゲームモデル
     */
    public GameWindow(GameModel model) {
        this.model = model;
        this.panel = new GamePanel(model);
        
        initializeWindow();
        setupComponents();
        registerListeners();
    }
    
    /**
     * ウィンドウの初期設定を行う。
     */
    private void initializeWindow() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }
    
    /**
     * コンポーネントを配置する。
     */
    private void setupComponents() {
        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
    }
    
    /**
     * リスナーを登録する。
     */
    private void registerListeners() {
        model.addPropertyChangeListener(this);
    }
    
    /**
     * 描画パネルを取得する。
     *
     * @return 描画パネル
     */
    public GamePanel getPanel() {
        return panel;
    }
    
    /**
     * GameModelを取得する。
     *
     * @return ゲームモデル
     */
    public GameModel getModel() {
        return model;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case GameModel.PROP_STATE -> handleStateChange((GameState) evt.getNewValue());
            case GameModel.PROP_SCORE -> handleScoreChange((int) evt.getNewValue());
        }
        // 変更があったら再描画
        panel.repaint();
    }
    
    /**
     * ゲーム状態の変更を処理する。
     *
     * @param newState 新しいゲーム状態
     */
    private void handleStateChange(GameState newState) {
        // 状態に応じてタイトルを更新
        String stateText = switch (newState) {
            case TITLE -> "Title";
            case PLAYING -> "Playing";
            case PAUSED -> "Paused";
            case GAME_OVER -> "Game Over";
            default -> "";
        };
        
        if (!stateText.isEmpty()) {
            setTitle(TITLE + " - " + stateText);
        } else {
            setTitle(TITLE);
        }
    }
    
    /**
     * スコアの変更を処理する。
     *
     * @param newScore 新しいスコア
     */
    private void handleScoreChange(int newScore) {
        // スコア変更時の処理（必要に応じて実装）
    }
}
