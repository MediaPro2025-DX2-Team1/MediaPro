package com.miozune.mediapro.stage.events;

import com.miozune.mediapro.stage.StageModel;

/**
 * バトルが終了した際に発行されるイベント。
 *
 * @param stage イベントの発生元となったStageModel
 * @param playerWon プレイヤーが勝利した場合true、敗北した場合false
 */
public record BattleEndedEvent(
        StageModel stage,
        boolean playerWon
) implements StagePropertyChangeEvent {

    @Override
    public StageModel getStage() {
        return stage;
    }
}
