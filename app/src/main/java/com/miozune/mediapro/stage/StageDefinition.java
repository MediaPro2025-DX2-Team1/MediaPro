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

    public static StageDefinition createSample(int index) {
        List<EnemyDefinition> enemyDefs = List.of(new EnemyDefinition("スライム" + index, 50, 50));
        return new StageDefinition("stage-" + index, "Stage " + index, enemyDefs);
    }

    public record EnemyDefinition(String name, int hp, int maxHp) { }
}
