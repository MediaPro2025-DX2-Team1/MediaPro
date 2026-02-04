package com.miozune.mediapro.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * 画像描画に関するユーティリティクラス。
 */
public final class ImageUtils {

    private ImageUtils() {}

    /**
     * 背景画像をコンポーネントのサイズに合わせて描画する。
     * アスペクト比を維持し、中央配置する。
     *
     * @param g 描画先のGraphicsコンテキスト
     * @param image 描画する画像（nullの場合は何も描画しない）
     * @param width コンポーネントの幅
     * @param height コンポーネントの高さ
     */
    public static void drawBackgroundImage(
            Graphics g, BufferedImage image, int width, int height) {
        if (image == null) {
            return;
        }

        double imgAspect = (double) image.getWidth() / image.getHeight();
        double panelAspect = (double) width / height;

        int drawWidth;
        int drawHeight;
        int drawX;
        int drawY;

        if (imgAspect > panelAspect) {
            // 画像が横長 → 高さを基準に
            drawHeight = height;
            drawWidth = (int) (height * imgAspect);
            drawX = (width - drawWidth) / 2;
            drawY = 0;
        } else {
            // 画像が縦長 → 幅を基準に
            drawWidth = width;
            drawHeight = (int) (width / imgAspect);
            drawX = 0;
            drawY = (height - drawHeight) / 2;
        }

        g.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
    }
}
