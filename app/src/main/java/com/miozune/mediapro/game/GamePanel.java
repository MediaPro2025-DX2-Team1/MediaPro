package com.miozune.mediapro.game;

import com.miozune.mediapro.preview.Previewable;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * ゲームの描画を行うパネルコンポーネント。
 * ダブルバッファリングを使用してちらつきを防止する。
 */
public class GamePanel extends JPanel implements Previewable {
    
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    
    private GameModel model;
    
    /**
     * GamePanelを初期化する。
     */
    public GamePanel() {
        this(null);
    }
    
    /**
     * GameModelを指定してGamePanelを初期化する。
     *
     * @param model ゲームモデル
     */
    public GamePanel(GameModel model) {
        this.model = model;
        
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
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
     * GameModelを設定する。
     *
     * @param model ゲームモデル
     */
    public void setModel(GameModel model) {
        this.model = model;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        // アンチエイリアシングを有効化
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );
        
        // ゲーム描画
        render(g2d);
    }
    
    /**
     * ゲームの描画処理を行う。
     * サブクラスでオーバーライドして具体的な描画を実装する。
     *
     * @param g2d Graphics2Dオブジェクト
     */
    protected void render(Graphics2D g2d) {
        // デフォルトでは何も描画しない（黒い背景のみ）
        // サブクラスでオーバーライドして描画処理を実装
    }
    
    /**
     * パネルの幅を取得する。
     *
     * @return パネルの幅
     */
    public int getPanelWidth() {
        return getWidth();
    }
    
    /**
     * パネルの高さを取得する。
     *
     * @return パネルの高さ
     */
    public int getPanelHeight() {
        return getHeight();
    }
    
    // Previewable インターフェースの実装
    
    @Override
    public String getPreviewDescription() {
        return "ゲームの描画を行う黒い背景のパネル";
    }
    
    @Override
    public void setupPreview() {
        // プレビュー用のセットアップ
        // 必要に応じてダミーデータを設定
        setModel(new GameModel());
    }
}
