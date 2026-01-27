package com.miozune.mediapro.card;

import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * {@link CardRecipeModel} を管理するレジストリ。
 */
public final class CardRegistry {

    private static final CardRegistry INSTANCE = new CardRegistry();

    private final Map<String, CardRecipeModel> recipes = new LinkedHashMap<>();

    private CardRegistry() {
        // TODO: 実際のカードデータに差し替える
        register(new CardRecipeModel("Attack", 1, "attack.jpg", "シンプルな攻撃カード。"));
        register(new CardRecipeModel("Guard", 1, "guard.jpg", "防御カード。"));
        register(new CardRecipeModel("Fireball", 2, "fireball.png", "小さな火の玉を放つ。"));
        register(new CardRecipeModel("Heal", 2, "heal.png", "少量のHPを回復する。"));
    }

    public static CardRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * カードを登録する。名前で上書きしないよう存在チェックを行う。
     */
    public void register(CardRecipeModel recipe) {
        Objects.requireNonNull(recipe, "recipe");
        if (recipes.containsKey(recipe.name())) {
            throw new IllegalArgumentException("既に同名のカードが登録されています: " + recipe.name());
        }
        recipes.put(recipe.name(), recipe);
    }

    public CardRecipeModel find(String name) {
        return recipes.get(name);
    }

    public Collection<CardRecipeModel> listAll() {
        return Collections.unmodifiableCollection(recipes.values());
    }
}
