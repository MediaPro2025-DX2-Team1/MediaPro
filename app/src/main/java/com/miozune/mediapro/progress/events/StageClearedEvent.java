package com.miozune.mediapro.progress.events;

import com.miozune.mediapro.progress.ProgressModel;

/**
 * ステージがクリアされた際に発行されるイベント。
 *
 * @param progress イベントの発生元となったProgressModel
 * @param stageId クリアされたステージのID
 * @param alreadyCleared すでにクリア済みだった場合true
 */
public record StageClearedEvent(
        ProgressModel progress,
        String stageId,
        boolean alreadyCleared
) implements ProgressPropertyChangeEvent {

    @Override
    public ProgressModel getProgress() {
        return progress;
    }
}
