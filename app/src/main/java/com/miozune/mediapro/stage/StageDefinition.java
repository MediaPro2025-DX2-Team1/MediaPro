package com.miozune.mediapro.stage;

import java.util.List;

/**
 * Immutable blueprint of a stage. Contains enemy setups.
 */
public record StageDefinition(
    String id,
    String name,
    List<EnemyDefinition> enemies
) {
    public StageDefinition {
        enemies = List.copyOf(enemies);
    }

    public static StageDefinition createStage1() {
        List<EnemyDefinition> enemyDefs = List.of(
            new EnemyDefinition("スライム", 12, 12),
            new EnemyDefinition("デビル", 16, 16),
            new EnemyDefinition("ゾンビ", 20, 20));
        return new StageDefinition("stage-1", "Stage 1", enemyDefs);
    }

    public static StageDefinition createStage2() {
        List<EnemyDefinition> enemyDefs = List.of(
            new EnemyDefinition("ゴブリンリーダー", 60, 60));
        return new StageDefinition("stage-2", "Stage 2", enemyDefs);
    }

    public static StageDefinition createStage3() {
        List<EnemyDefinition> enemyDefs = List.of(
            new EnemyDefinition("ドラゴン", 120, 120));
        return new StageDefinition("stage-3", "Stage 3", enemyDefs);
    }

    public record EnemyDefinition(String name, int hp, int maxHp) { }
}
