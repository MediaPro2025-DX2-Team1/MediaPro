package com.miozune.mediapro.enemy;

import com.miozune.mediapro.enemy.behavior.DevilBehavior;
import com.miozune.mediapro.enemy.behavior.DragonBehavior;
import com.miozune.mediapro.enemy.behavior.EnemyBehavior;
import com.miozune.mediapro.enemy.behavior.GoblinBehavior;
import com.miozune.mediapro.enemy.behavior.GoblinLeaderBehavior;
import com.miozune.mediapro.enemy.behavior.SlimeBehavior;
import com.miozune.mediapro.enemy.behavior.ZombieBehavior;
import com.miozune.mediapro.stage.StageDefinition;
import java.util.Objects;

/** 敵モデルと行動ビヘイビアの組み立てを担うファクトリ。 */
public class EnemyFactory {

    public EnemyInstance create(StageDefinition.EnemyDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        EnemyBehavior behavior = createBehavior(definition.type());
        EnemyModel model = new EnemyModel(definition.type(), definition.name(), definition.hp(), definition.maxHp());
        return new EnemyInstance(model, behavior);
    }

    public EnemyInstance create(EnemyType type) {
        StageDefinition.EnemyDefinition def = defaultDefinition(type);
        return create(def);
    }

    private EnemyBehavior createBehavior(EnemyType type) {
        return switch (type) {
            case SLIME -> new SlimeBehavior();
            case ZOMBIE -> new ZombieBehavior();
            case DEVIL -> new DevilBehavior();
            case GOBLIN -> new GoblinBehavior();
            case GOBLIN_LEADER -> new GoblinLeaderBehavior();
            case DRAGON -> new DragonBehavior();
        };
    }

    private StageDefinition.EnemyDefinition defaultDefinition(EnemyType type) {
        return switch (type) {
            case SLIME -> new StageDefinition.EnemyDefinition(EnemyType.SLIME, "スライム", 12, 12);
            case ZOMBIE -> new StageDefinition.EnemyDefinition(EnemyType.ZOMBIE, "ゾンビ", 20, 20);
            case DEVIL -> new StageDefinition.EnemyDefinition(EnemyType.DEVIL, "デビル", 16, 16);
            case GOBLIN -> new StageDefinition.EnemyDefinition(EnemyType.GOBLIN, "ゴブリン", 14, 14);
            case GOBLIN_LEADER -> new StageDefinition.EnemyDefinition(EnemyType.GOBLIN_LEADER, "ゴブリンリーダー", 60, 60);
            case DRAGON -> new StageDefinition.EnemyDefinition(EnemyType.DRAGON, "ドラゴン", 120, 120);
        };
    }

    /** モデルとビヘイビアのペア。 */
    public record EnemyInstance(EnemyModel model, EnemyBehavior behavior) {}
}
