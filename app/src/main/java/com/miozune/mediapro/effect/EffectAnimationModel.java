package com.miozune.mediapro.effect;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * 連番PNG画像を管理し、アニメーションフレームを提供するModel。
 */
public class EffectAnimationModel {

    private final EffectType type;
    private final List<Image> frames;
    private final int frameCount;
    private final int frameDelayMs;

    /**
     * 指定されたEffectTypeの連番画像を読み込む。
     *
     * @param type エフェクトタイプ
     * @throws IOException 画像の読み込みに失敗した場合
     */
    public EffectAnimationModel(EffectType type) throws IOException {
        this.type = type;
        this.frameCount = type.getFrameCount();
        this.frameDelayMs = type.getFrameDelayMs();
        this.frames = new ArrayList<>(frameCount);

        // 連番画像を読み込み、スケーリング
        double scale = type.getScale();
        for (int i = 0; i < frameCount; i++) {
            String path = String.format("/images/%s/frame_%d.png", type.getResourcePath(), i);
            BufferedImage image = ImageIO.read(getClass().getResource(path));
            if (image == null) {
                throw new IOException("エフェクト画像の読み込みに失敗: " + path);
            }

            // スケーリング（元のサイズより小さくする場合）
            if (scale != 1.0) {
                int scaledWidth = (int) (image.getWidth() * scale);
                int scaledHeight = (int) (image.getHeight() * scale);

                // getScaledInstanceの遅延評価を回避するため、BufferedImageに直接描画
                BufferedImage scaledImage = new BufferedImage(
                        scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledImage.createGraphics();
                try {
                    // 高品質なスケーリング設定
                    g2d.setRenderingHint(
                            RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(
                            RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
                } finally {
                    g2d.dispose();
                }
                frames.add(scaledImage);
            } else {
                frames.add(image);
            }
        }
    }

    public EffectType getType() {
        return type;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int getFrameDelayMs() {
        return frameDelayMs;
    }

    /**
     * 指定されたインデックスのフレーム画像を取得。
     *
     * @param index フレームインデックス（0始まり）
     * @return フレーム画像
     */
    public Image getFrame(int index) {
        if (index < 0 || index >= frameCount) {
            throw new IndexOutOfBoundsException("フレームインデックスが範囲外: " + index);
        }
        return frames.get(index);
    }

    /**
     * アニメーションの総再生時間をミリ秒で取得。
     */
    public int getTotalDurationMs() {
        return frameCount * frameDelayMs;
    }
}
