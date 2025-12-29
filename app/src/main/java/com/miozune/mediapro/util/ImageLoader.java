package com.miozune.mediapro.util;

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
