package com.miozune.mediapro.stage.events;

import com.miozune.mediapro.enemy.EnemyModel;
import com.miozune.mediapro.stage.StageModel;

/**
 * 敵がプレイヤーを攻撃した際に発行されるイベント。
 *
 * @param stage イベントの発生元となったStageModel
 * @param attacker 攻撃を行った敵
 * @param damage 実際に与えたダメージ量
 */
public record EnemyAttackedPlayerEvent(StageModel stage, EnemyModel attacker, int damage)
        implements StagePropertyChangeEvent {

    @Override
    public StageModel getStage() {
        return stage;
    }
}
