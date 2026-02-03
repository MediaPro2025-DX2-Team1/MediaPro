package com.miozune.mediapro.stage;

import com.miozune.mediapro.enemy.EnemyType;
import java.util.List;

/**
 * Immutable blueprint of a stage. Contains enemy setups.
 * ステージ定義の不変オブジェクト。次のステージIDを保持することで、進行順序をデータとして管理します。
 */
public record StageDefinition(
    String id,
    String name,
    List<EnemyDefinition> enemies,
    String nextStageId
) {
    public StageDefinition {
        enemies = List.copyOf(enemies);
    }

    public static StageDefinition createStage1() {
        List<EnemyDefinition> enemyDefs = List.of(
            new EnemyDefinition(EnemyType.SLIME, "スライム", 12, 12),
            new EnemyDefinition(EnemyType.DEVIL, "デビル", 16, 16),
            new EnemyDefinition(EnemyType.ZOMBIE, "ゾンビ", 20, 20));
        return new StageDefinition("stage1", "Stage 1", enemyDefs, "stage2");
    }

    public static StageDefinition createStage2() {
        List<EnemyDefinition> enemyDefs = List.of(
            new EnemyDefinition(EnemyType.GOBLIN_LEADER, "ゴブリンリーダー", 60, 60));
        return new StageDefinition("stage2", "Stage 2", enemyDefs, "stage3");
    }

    public static StageDefinition createStage3() {
        List<EnemyDefinition> enemyDefs = List.of(
            new EnemyDefinition(EnemyType.DRAGON, "ドラゴン", 120, 120));
        return new StageDefinition("stage3", "Stage 3", enemyDefs, null);
    }

    public record EnemyDefinition(EnemyType type, String name, int hp, int maxHp) { }
}
