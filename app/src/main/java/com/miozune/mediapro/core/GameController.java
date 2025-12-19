package com.miozune.mediapro.core;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ゲームの入力処理とゲームループの管理を行うコントローラークラス。
 */
public class GameController {
    
    private final GameModel model;
    private final GameWindow window;
    private final GameLoop gameLoop;
    
    /**
     * GameControllerを初期化する。
     *
     * @param model ゲームモデル
     * @param window ゲームウィンドウ
     */
    public GameController(GameModel model, GameWindow window) {
        this.model = model;
        this.window = window;
        this.gameLoop = new GameLoop(this::update);
        
        setupInputHandlers();
        initializeGame();
    }
    
    /**
     * 入力ハンドラーをセットアップする。
     */
    private void setupInputHandlers() {
        GamePanel panel = window.getPanel();
        
        // キーボード入力
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyReleased(e);
            }
        });
        
        // マウス入力
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });
        
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMoved(e);
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
        
        // パネルがフォーカスを受け取れるようにする
        panel.requestFocusInWindow();
    }
    
    /**
     * ゲームを初期化する。
     */
    private void initializeGame() {
        model.setState(GameState.TITLE);
        gameLoop.start();
    }
    
    /**
     * ゲームループの更新処理。
     */
    private void update() {
        // 状態に応じた更新処理
        switch (model.getState()) {
            case PLAYING -> updatePlaying();
            case PAUSED -> updatePaused();
            default -> {
                // その他の状態では特に更新なし
            }
        }
        
        // 描画更新
        window.getPanel().repaint();
    }
    
    /**
     * プレイ中の更新処理。
     */
    private void updatePlaying() {
        // ゲームロジックの更新（サブクラスでオーバーライド）
    }
    
    /**
     * 一時停止中の更新処理。
     */
    private void updatePaused() {
        // 一時停止中の処理（必要に応じて実装）
    }
    
    /**
     * キー押下イベントを処理する。
     *
     * @param e キーイベント
     */
    protected void handleKeyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> {
                if (model.getState() == GameState.PLAYING) {
                    model.setState(GameState.PAUSED);
                } else if (model.getState() == GameState.PAUSED) {
                    model.setState(GameState.PLAYING);
                }
            }
            case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
                if (model.getState() == GameState.TITLE) {
                    model.setState(GameState.PLAYING);
                } else if (model.getState() == GameState.GAME_OVER) {
                    model.reset();
                    model.setState(GameState.TITLE);
                }
            }
        }
    }
    
    /**
     * キーリリースイベントを処理する。
     *
     * @param e キーイベント
     */
    protected void handleKeyReleased(KeyEvent e) {
        // サブクラスでオーバーライドして実装
    }
    
    /**
     * マウス押下イベントを処理する。
     *
     * @param e マウスイベント
     */
    protected void handleMousePressed(MouseEvent e) {
        // サブクラスでオーバーライドして実装
    }
    
    /**
     * マウスリリースイベントを処理する。
     *
     * @param e マウスイベント
     */
    protected void handleMouseReleased(MouseEvent e) {
        // サブクラスでオーバーライドして実装
    }
    
    /**
     * マウス移動イベントを処理する。
     *
     * @param e マウスイベント
     */
    protected void handleMouseMoved(MouseEvent e) {
        // サブクラスでオーバーライドして実装
    }
    
    /**
     * マウスドラッグイベントを処理する。
     *
     * @param e マウスイベント
     */
    protected void handleMouseDragged(MouseEvent e) {
        // サブクラスでオーバーライドして実装
    }
    
    /**
     * GameModelを取得する。
     *
     * @return ゲームモデル
     */
    public GameModel getModel() {
        return model;
    }
    
    /**
     * GameWindowを取得する。
     *
     * @return ゲームウィンドウ
     */
    public GameWindow getWindow() {
        return window;
    }
    
    /**
     * GameLoopを取得する。
     *
     * @return ゲームループ
     */
    public GameLoop getGameLoop() {
        return gameLoop;
    }
}
