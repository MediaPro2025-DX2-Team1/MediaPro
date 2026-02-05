package com.miozune.mediapro.card;

import com.miozune.mediapro.action.ActionEffect;
import com.miozune.mediapro.action.DamageSingleEnemyActionEffect;

/**
 * カードのデータを保持するイミュータブルなレコード。
 * カード名、コスト、画像ファイル名、説明文を持つ。
 *
 * @param name        カード名
 * @param cost        コスト
 * @param imageName   画像ファイル名（例: "card_001.jpg"）
 * @param description 説明文
 * @param cardAction  カード効果
 * @param targetType  必要なターゲットの種別
 */
public record CardRecipeModel(
    String name,
    int cost,
    String imageName,
    String description,
     ActionEffect cardAction,
     CardTargetType targetType) {

    /**
     * サンプルのCardModelを作成する。
     * プレビューやテスト用途に使用。
     *
     * @return サンプルのCardModel
     */
    public static CardRecipeModel createSample() {
        return new CardRecipeModel(
                "サンプルカード",
                3,
                "sample.jpg",
                "これはサンプルカードの説明文です。カードの効果や特徴などを記載します。",
            new DamageSingleEnemyActionEffect(5),
            CardTargetType.SINGLE_ENEMY);
    }
}
