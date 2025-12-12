package com.miozune.mediapro.card;

/**
 * カードのデータを保持するイミュータブルなレコード。
 * カード名、コスト、画像ファイル名、説明文を持つ。
 *
 * @param name カード名
 * @param cost コスト
 * @param imageName 画像ファイル名（例: "card_001.png"）
 * @param description 説明文
 */
public record CardData(
    String name,
    int cost,
    String imageName,
    String description
) {
    
    /**
     * サンプルのCardDataを作成する。
     * プレビューやテスト用途に使用。
     *
     * @return サンプルのCardData
     */
    public static CardData createSample() {
        return new CardData(
            "サンプルカード",
            3,
            "sample.png",
            "これはサンプルカードの説明文です。カードの効果や特徴などを記載します。"
        );
    }
    
    /**
     * 空のCardDataを作成する。
     *
     * @return 空のCardData
     */
    public static CardData empty() {
        return new CardData("", 0, "", "");
    }
}
