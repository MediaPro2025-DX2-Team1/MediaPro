package com.miozune.mediapro.card;

import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import com.miozune.mediapro.effect.CardAction;
import com.miozune.mediapro.effect.action.AddShieldActionEffect;
import com.miozune.mediapro.effect.action.ApplyStrengthActionEffect;
import com.miozune.mediapro.effect.action.ApplyWeaknessActionEffect;
import com.miozune.mediapro.effect.action.DamageAllEnemiesActionEffect;
import com.miozune.mediapro.effect.action.DamageSingleEnemyActionEffect;
import com.miozune.mediapro.effect.action.DrawCardsActionEffect;
import com.miozune.mediapro.effect.action.HealSelfActionEffect;
import com.miozune.mediapro.effect.action.MultiHitSingleEnemyActionEffect;
import com.miozune.mediapro.effect.action.SelfDamageActionEffect;
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
    // TODO: 画像ファイル名はダミー。実画像が揃い次第差し替え。
    register(new CardRecipeModel(
        "スラッシュ",
        1,
        "slash.jpg",
        "敵単体に5ダメージ。",
                CardAction.of(new DamageSingleEnemyActionEffect(5))));

    register(new CardRecipeModel(
        "ブロック",
        1,
        "block.jpg",
        "5シールドを得る。",
                CardAction.of(new AddShieldActionEffect(5))));

    register(new CardRecipeModel(
        "鎧砕き",
        2,
        "armor_break.jpg",
        "敵単体に8ダメージし、弱体2ターン付与。",
        CardAction.of(new DamageSingleEnemyActionEffect(8),
            new ApplyWeaknessActionEffect(2))));

    register(new CardRecipeModel(
        "補給",
        1,
        "supply.jpg",
        "山札から2枚ドロー。",
                CardAction.of(new DrawCardsActionEffect(2))));

    register(new CardRecipeModel(
        "リカバリー",
        1,
        "recovery.jpg",
        "自分のHPを3回復。",
                CardAction.of(new HealSelfActionEffect(3))));

    register(new CardRecipeModel(
        "激昂",
        1,
        "rage.jpg",
        "筋力2を2ターン得る。",
                CardAction.of(new ApplyStrengthActionEffect(2, 2))));

    register(new CardRecipeModel(
        "ツインスラッシュ",
        1,
        "twin_slash.jpg",
        "敵単体に5ダメージを2回。",
                CardAction.of(new MultiHitSingleEnemyActionEffect(5, 2))));

    register(new CardRecipeModel(
        "なぎ払い",
        1,
        "cleave.jpg",
        "全ての敵に4ダメージ。",
                CardAction.of(new DamageAllEnemiesActionEffect(4))));

    register(new CardRecipeModel(
        "諸刃の剣",
        1,
        "double_edge.jpg",
        "自分に2ダメージ。敵単体に12ダメージ。",
        CardAction.of(
            new SelfDamageActionEffect(2),
            new DamageSingleEnemyActionEffect(12))));
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
