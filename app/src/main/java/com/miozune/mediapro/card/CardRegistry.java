package com.miozune.mediapro.card;

import com.miozune.mediapro.action.AddShieldActionEffect;
import com.miozune.mediapro.action.AllOutStrikeActionEffect;
import com.miozune.mediapro.action.ApplyStrengthActionEffect;
import com.miozune.mediapro.action.ApplyWeaknessActionEffect;
import com.miozune.mediapro.action.ApplyWeaknessToAllEnemiesActionEffect;
import com.miozune.mediapro.action.CompositeActionEffect;
import com.miozune.mediapro.action.DamageAllEnemiesActionEffect;
import com.miozune.mediapro.action.DamageSingleEnemyActionEffect;
import com.miozune.mediapro.action.DrainActionEffect;
import com.miozune.mediapro.action.DrawCardsActionEffect;
import com.miozune.mediapro.action.HealSelfActionEffect;
import com.miozune.mediapro.action.MultiHitSingleEnemyActionEffect;
import com.miozune.mediapro.action.RestoreManaActionEffect;
import com.miozune.mediapro.action.SelfDamageActionEffect;
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

    public static CardRegistry getInstance() {
        return INSTANCE;
    }

    private CardRegistry() {
        // TODO: 画像ファイル名はダミー。実画像が揃い次第差し替え。
        register(new CardRecipeModel(
            "スラッシュ",
            1,
            "slash.jpg",
            "敵1体に5ダメージを与える。",
            new DamageSingleEnemyActionEffect(5),
            CardTargetType.SINGLE_ENEMY));

        register(new CardRecipeModel(
            "ブロック",
            1,
            "block.jpg",
            "自分は5シールドを得る。",
            new AddShieldActionEffect(5),
            CardTargetType.SELF));

        register(new CardRecipeModel(
            "鎧砕き",
            2,
            "armor_break.jpg",
            "敵1体に8ダメージ。弱体2ターンを与える。",
            CompositeActionEffect.of(
                new DamageSingleEnemyActionEffect(8),
                new ApplyWeaknessActionEffect(2)),
            CardTargetType.SINGLE_ENEMY));

        register(new CardRecipeModel(
            "補給",
            1,
            "supply.jpg",
            "山札からカードを2枚引く。",
            new DrawCardsActionEffect(2),
            CardTargetType.NONE));

        register(new CardRecipeModel(
            "リカバリー",
            1,
            "recovery.jpg",
            "自分のHPを3回復する。",
            new HealSelfActionEffect(3),
            CardTargetType.SELF));

        register(new CardRecipeModel(
            "激昂",
            1,
            "rage.jpg",
            "自分は筋力2を得る。",
            new ApplyStrengthActionEffect(2),
            CardTargetType.SELF));

        register(new CardRecipeModel(
            "ツインスラッシュ",
            1,
            "twin_slash.jpg",
            "敵1体に5ダメージを2回与える。",
            new MultiHitSingleEnemyActionEffect(5, 2),
            CardTargetType.SINGLE_ENEMY));

        register(new CardRecipeModel(
            "なぎ払い",
            1,
            "cleave.jpg",
            "すべての敵に4ダメージを与える。",
            new DamageAllEnemiesActionEffect(4),
            CardTargetType.ALL_ENEMIES));

        register(new CardRecipeModel(
            "諸刃の剣",
            1,
            "double_edge.jpg",
            "自分は2ダメージを受ける。敵1体に12ダメージを与える。",
            CompositeActionEffect.of(
                new SelfDamageActionEffect(2),
                new DamageSingleEnemyActionEffect(12)),
            CardTargetType.SINGLE_ENEMY));

        register(new CardRecipeModel(
            "渾身の一刀",
            2,
            "all_out_strike.jpg",
            "手札をすべて捨てる。敵1体に捨てた枚数×7ダメージを与える。",
            new AllOutStrikeActionEffect(7),
            CardTargetType.SINGLE_ENEMY));

        register(new CardRecipeModel(
            "火事場の馬鹿力",
            2,
            "desperation.jpg",
            "自分は4ダメージを受ける。すべての敵に20ダメージを与える。",
            CompositeActionEffect.of(
                new SelfDamageActionEffect(4),
                new DamageAllEnemiesActionEffect(20)),
            CardTargetType.ALL_ENEMIES));

        register(new CardRecipeModel(
            "牽制",
            1,
            "feint.jpg",
            "敵1体に6ダメージを与える。山札からカードを1枚引く。",
            CompositeActionEffect.of(
                new DamageSingleEnemyActionEffect(6),
                new DrawCardsActionEffect(1)),
            CardTargetType.SINGLE_ENEMY));

        register(new CardRecipeModel(
            "サルベージ",
            1,
            "salvage.jpg",
            "自分は7シールドを得る。山札からカードを1枚引く。",
            CompositeActionEffect.of(
                new AddShieldActionEffect(7),
                new DrawCardsActionEffect(1)),
            CardTargetType.NONE));

        /*
        register(new CardRecipeModel(
            "旋風刃",
            X,
            "whirlwind_blade.jpg",
            "エナジーをすべて消費する。すべての敵に5ダメージをX回与える。",
            new WhirlwindBladeActionEffect()));
        */

        register(new CardRecipeModel(
            "ドレイン",
            1,
            "drain.jpg",
            "敵1体に6ダメージを与える。この攻撃で敵を倒した場合、自分のHPを2回復する。",
            new DrainActionEffect(6, 2),
            CardTargetType.SINGLE_ENEMY));

        register(new CardRecipeModel(
            "精神統一",
            0,
            "meditation.jpg",
            "エナジーを1回復する。",
            new RestoreManaActionEffect(1),
            CardTargetType.SELF));

        register(new CardRecipeModel(
            "威嚇",
            2,
            "intimidate.jpg",
            "自分は12シールドを得る。すべての敵に弱体3ターンを与える。",
            CompositeActionEffect.of(
                new AddShieldActionEffect(12),
                new ApplyWeaknessToAllEnemiesActionEffect(3)),
            CardTargetType.ALL_ENEMIES));
    }

    /**
     * カードを登録する。名前で上書きしないよう存在チェックを行う。
     */
    private void register(CardRecipeModel recipe) {
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
