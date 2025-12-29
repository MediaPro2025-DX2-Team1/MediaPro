package com.miozune.mediapro.card;

/**
 * カードのデータを保持するイミュータブルなレコード。
 * カード名、コスト、画像ファイル名、説明文を持つ。
 *
 * @param name カード名
 * @param cost コスト
 * @param imageName 画像ファイル名（例: "card_001.jpg"）
 * @param description 説明文
 */
public record CardModel(
    String name,
    int cost,
    String imageName,
    String description
) {

    /**
     * サンプルのCardModelを作成する。
     * プレビューやテスト用途に使用。
     *
     * @return サンプルのCardModel
     */
    public static CardModel createSample() {
        return new CardModel(
            "サンプルカード",
            3,
            "sample.jpg",
            "これはサンプルカードの説明文です。カードの効果や特徴などを記載します。"
        );
    }
}
