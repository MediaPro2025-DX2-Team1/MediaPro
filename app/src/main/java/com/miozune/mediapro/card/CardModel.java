package com.miozune.mediapro.card;

import com.miozune.mediapro.action.CardAction;

/**
 * バトル中に使用される具体的なカードインスタンス。
 * 元となる {@link CardRecipeModel} を参照する。
 */
public final class CardModel {

    private final CardRecipeModel recipe;

    public CardModel(CardRecipeModel recipe) {
        this.recipe = recipe;
    }

    public CardRecipeModel recipe() {
        return recipe;
    }

    public String name() {
        return recipe.name();
    }

    public int cost() {
        return recipe.cost();
    }

    public String imageName() {
        return recipe.imageName();
    }

    public String description() {
        return recipe.description();
    }

    public CardTargetType targetType() {
        return recipe.targetType();
    }

    public CardAction action() {
        return recipe.cardAction();
    }

    public static CardModel createSample() {
        return new CardModel(CardRecipeModel.createSample());
    }
}
