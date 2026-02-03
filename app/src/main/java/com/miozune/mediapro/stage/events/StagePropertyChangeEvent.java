package com.miozune.mediapro.stage.events;

import com.miozune.mediapro.stage.StageModel;

/**
 * StageModelのプロパティ変更イベントの基底インターフェース。
 */
public sealed interface StagePropertyChangeEvent
        permits BattleEndedEvent {

    /**
     * イベントの発生元となったStageModelを取得します。
     *
     * @return StageModel
     */
    StageModel getStage();
}
