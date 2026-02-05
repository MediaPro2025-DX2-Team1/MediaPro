package com.miozune.mediapro.stage.events;

import com.miozune.mediapro.actor.AbstractActorModel;
import com.miozune.mediapro.effect.EffectType;
import com.miozune.mediapro.stage.StageModel;

/**
 * ビジュアルエフェクトの再生がトリガーされた際に発行されるイベント。
 *
 * @param stage イベントの発生元となったStageModel
 * @param effectType エフェクトの種類
 * @param target エフェクトの対象（座標解決に使用）。nullの場合はデフォルト位置
 */
public record EffectTriggeredEvent(
        StageModel stage, EffectType effectType, AbstractActorModel<?> target)
        implements StagePropertyChangeEvent {

    @Override
    public StageModel getStage() {
        return stage;
    }
}
