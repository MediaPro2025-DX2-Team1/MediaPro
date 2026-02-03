package com.miozune.mediapro.progress.events;

import com.miozune.mediapro.progress.ProgressModel;

/**
 * ステージがアンロックされた際に発行されるイベント。
 *
 * @param progress イベントの発生元となったProgressModel
 * @param stageId アンロックされたステージのID
 */
public record StageUnlockedEvent(
        ProgressModel progress,
        String stageId
) implements ProgressPropertyChangeEvent {

    @Override
    public ProgressModel getProgress() {
        return progress;
    }
}
