package com.miozune.mediapro.progress.events;

import com.miozune.mediapro.progress.ProgressModel;

/**
 * ProgressModelのプロパティ変更イベントの基底インターフェース。
 */
public sealed interface ProgressPropertyChangeEvent
        permits StageUnlockedEvent, StageClearedEvent {

    /**
     * イベントの発生元となったProgressModelを取得します。
     *
     * @return ProgressModel
     */
    ProgressModel getProgress();
}
