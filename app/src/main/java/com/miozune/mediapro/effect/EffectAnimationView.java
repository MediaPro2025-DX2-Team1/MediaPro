package com.miozune.mediapro.effect;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * 連番画像を使ったエフェクトアニメーションを再生するView。
 * javax.swing.Timerを使用して非同期に再生する。
 */
public class EffectAnimationView extends JComponent {

    private final EffectAnimationModel model;
    private final Point position;
    private final Runnable onComplete;

    private int currentFrame = 0;
    private Timer timer;

    /**
     * エフェクトアニメーションViewを作成。
     *
     * @param model アニメーションModel
     * @param position 再生位置（画像の中心座標）
     * @param onComplete 再生完了時のコールバック（nullでも可）
     */
    public EffectAnimationView(EffectAnimationModel model, Point position, Runnable onComplete) {
        this.model = model;
        this.position = position;
        this.onComplete = onComplete;

        // 透明な背景
        setOpaque(false);

        // Timerのセットアップ
        setupTimer();
    }

    private void setupTimer() {
        timer = new Timer(model.getFrameDelayMs(), e -> {
            currentFrame++;
            if (currentFrame >= model.getFrameCount()) {
                stop();
                if (onComplete != null) {
                    onComplete.run();
                }
            } else {
                repaint();
            }
        });
    }

    /**
     * アニメーションを開始。
     */
    public void start() {
        currentFrame = 0;
        timer.start();
        repaint();
    }

    /**
     * アニメーションを停止。
     */
    public void stop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    /**
     * アニメーションが再生中かどうか。
     */
    public boolean isPlaying() {
        return timer != null && timer.isRunning();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (currentFrame >= 0 && currentFrame < model.getFrameCount()) {
            Graphics2D g2d = (Graphics2D) g.create();

            // アンチエイリアス設定
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // 現在のフレームを取得
            var frame = model.getFrame(currentFrame);
            int width = frame.getWidth(null);
            int height = frame.getHeight(null);

            // 中心座標に画像を描画（positionが画像の中心になるよう調整）
            int x = position.x - width / 2;
            int y = position.y - height / 2;

            g2d.drawImage(frame, x, y, null);
            g2d.dispose();
        }
    }

    /**
     * リソースのクリーンアップ。使用後は必ず呼び出すこと。
     */
    public void dispose() {
        stop();
        if (timer != null) {
            timer = null;
        }
    }
}
