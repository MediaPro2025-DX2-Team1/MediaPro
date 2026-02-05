package com.miozune.mediapro.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * 画像リソースの読み込みを行うユーティリティクラス。
 * 読み込んだ画像はキャッシュされ、同じ画像の再読み込みを防ぐ。
 */
public final class ImageLoader {

    /** 画像キャッシュ */
    private static final Map<String, BufferedImage> IMAGE_CACHE = new HashMap<>();

    /** カード画像のベースパス */
    public static final String CARDS_PATH = "/images/cards/";

    /** 背景画像のベースパス */
    public static final String BACKGROUNDS_PATH = "/images/backgrounds/";

    /** エンティティ画像のベースパス */
    public static final String ENTITIES_PATH = "/images/entities/";

    /** ボタン画像のベースパス */
    public static final String BUTTONS_PATH = "/images/buttons/";

    /** UI画像のベースパス */
    public static final String UI_PATH = "/images/ui/";

    private ImageLoader() {}

    /**
     * 指定されたパスから画像を読み込む。
     * 読み込んだ画像はキャッシュされる。
     *
     * @param path リソースパス（例: "/images/cards/card.jpg"）
     * @return 読み込んだ画像、読み込みに失敗した場合はnull
     */
    public static BufferedImage loadImage(String path) {
        // キャッシュにあればそれを返す
        if (IMAGE_CACHE.containsKey(path)) {
            return IMAGE_CACHE.get(path);
        }

        try (InputStream is = ImageLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Warning: Image not found: " + path);
                return null;
            }

            BufferedImage image = ImageIO.read(is);
            IMAGE_CACHE.put(path, image);
            return image;

        } catch (IOException e) {
            System.err.println("Error loading image: " + path + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * カード画像を読み込む。
     * "/images/cards/" をベースパスとして、指定されたファイル名の画像を読み込む。
     *
     * @param fileName カード画像のファイル名（例: "card_001.jpg"）
     * @return 読み込んだ画像、読み込みに失敗した場合はnull
     */
    public static BufferedImage loadCardImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        return loadImage(CARDS_PATH + fileName);
    }

    /**
     * 背景画像を読み込む。
     * "/images/backgrounds/" をベースパスとして、指定されたファイル名の画像を読み込む。
     *
     * @param fileName 背景画像のファイル名（例: "title_bg.png"）
     * @return 読み込んだ画像、読み込みに失敗した場合はnull
     */
    public static BufferedImage loadBackgroundImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        return loadImage(BACKGROUNDS_PATH + fileName);
    }

    /**
     * UI画像を読み込む。
     * "/images/ui/" をベースパスとして、指定されたファイル名の画像を読み込む。
     *
     * @param fileName UI画像のファイル名（例: "title.png"）
     * @return 読み込んだ画像
     */
    public static BufferedImage loadUiImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        return loadImage(UI_PATH + fileName);
    }

    /**
     * エンティティ画像を読み込む。
     * "/images/entities/" をベースパスとして、指定されたファイル名の画像を読み込む。
     *
     * @param fileName エンティティ画像のファイル名（例: "player.png"）
     * @return 読み込んだ画像、読み込みに失敗した場合はnull
     */
    public static BufferedImage loadEntityImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        return loadImage(ENTITIES_PATH + fileName);
    }

    /**
     * ボタン画像を読み込む。
     * "/images/buttons/" をベースパスとして、指定されたファイル名の画像を読み込む。
     *
     * @param fileName ボタン画像のファイル名（例: "stone.png"）
     * @return 読み込んだ画像、読み込みに失敗した場合はnull
     */
    public static BufferedImage loadButtonImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        return loadImage(BUTTONS_PATH + fileName);
    }

    /**
     * 画像を指定されたサイズにスケーリングする。
     * アスペクト比を保持し、指定されたサイズに収まるようにスケーリングする。
     *
     * @param original 元の画像
     * @param maxWidth 最大幅
     * @param maxHeight 最大高さ
     * @return スケーリングされた画像、元の画像がnullの場合はnull
     */
    public static BufferedImage getScaledImage(BufferedImage original, int maxWidth, int maxHeight) {
        if (original == null) {
            return null;
        }

        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        // アスペクト比を保持して計算
        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int scaledWidth = (int) (originalWidth * ratio);
        int scaledHeight = (int) (originalHeight * ratio);

        // 既に同じサイズの場合はそのまま返す
        if (scaledWidth == originalWidth && scaledHeight == originalHeight) {
            return original;
        }

        Image scaledImage = original.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        BufferedImage bufferedScaled = new BufferedImage(
            scaledWidth,
            scaledHeight,
            BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = bufferedScaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return bufferedScaled;
    }

    /**
     * キャッシュをクリアする。
     */
    public static void clearCache() {
        IMAGE_CACHE.clear();
    }

    /**
     * 指定されたパスの画像をキャッシュから削除する。
     *
     * @param path リソースパス
     */
    public static void removeFromCache(String path) {
        IMAGE_CACHE.remove(path);
    }
}
