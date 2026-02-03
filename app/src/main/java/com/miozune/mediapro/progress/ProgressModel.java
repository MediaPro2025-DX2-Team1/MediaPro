package com.miozune.mediapro.progress;

import com.miozune.mediapro.progress.events.ProgressPropertyChangeEvent;
import com.miozune.mediapro.progress.events.StageClearedEvent;
import com.miozune.mediapro.progress.events.StageUnlockedEvent;
import com.miozune.mediapro.stage.StageDefinition;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ゲームの進行状況を管理するモデル。
 * ステージのクリア状態とアンロック状態を保持します。
 */
public class ProgressModel {

    /**
     * プロパティ変更イベントのリスナーインターフェース。
     */
    @FunctionalInterface
    public interface PropertyChangeListener {
        /**
         * プロパティが変更された際に呼び出されます。
         *
         * @param event プロパティ変更イベント
         */
        void onPropertyChanged(ProgressPropertyChangeEvent event);
    }

    private final List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<>();
    private final Set<String> clearedStages = new HashSet<>();
    private final Set<String> unlockedStages = new HashSet<>();

    /**
     * 新しいProgressModelを作成します。
     * デフォルトでは最初のステージ（stage1）のみがアンロックされています。
     */
    public ProgressModel() {
        // 最初のステージは最初からアンロック
        unlockedStages.add("stage1");
    }

    /**
     * プロパティ変更リスナーを追加します。
     *
     * @param listener リスナー
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * プロパティ変更リスナーを削除します。
     *
     * @param listener リスナー
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * イベントを発火します。
     *
     * @param event イベント
     */
    private void fireEvent(ProgressPropertyChangeEvent event) {
        for (PropertyChangeListener listener : listeners) {
            listener.onPropertyChanged(event);
        }
    }

    /**
     * 指定されたステージがクリア済みかどうかを判定します。
     *
     * @param stageId ステージID
     * @return クリア済みの場合true
     */
    public boolean isCleared(String stageId) {
        return clearedStages.contains(stageId);
    }

    /**
     * 指定されたステージがアンロック済みかどうかを判定します。
     *
     * @param stageId ステージID
     * @return アンロック済みの場合true
     */
    public boolean isUnlocked(String stageId) {
        return unlockedStages.contains(stageId);
    }

    /**
     * クリア済みステージのIDセットを取得します（変更不可）。
     *
     * @return クリア済みステージのIDセット
     */
    public Set<String> getClearedStages() {
        return Set.copyOf(clearedStages);
    }

    /**
     * アンロック済みステージのIDセットを取得します（変更不可）。
     *
     * @return アンロック済みステージのIDセット
     */
    public Set<String> getUnlockedStages() {
        return Set.copyOf(unlockedStages);
    }

    /**
     * ステージをクリア済みとしてマークし、次のステージをアンロックします。
     * すでにクリア済みの場合は、アンロック処理のみ実行します。
     *
     * @param stageId クリアしたステージのID
     */
    public void clearStage(String stageId) {
        boolean alreadyCleared = clearedStages.contains(stageId);
        clearedStages.add(stageId);
        fireEvent(new StageClearedEvent(this, stageId, alreadyCleared));
    }

    /**
     * ステージをクリア済みとしてマークし、次のステージをアンロックします。
     * StageDefinitionの情報を使用して次のステージを自動的にアンロックします。
     * すでにクリア済みの場合は、アンロック処理のみ実行します。
     *
     * @param stageDefinition クリアしたステージの定義
     */
    public void clearStage(StageDefinition stageDefinition) {
        String stageId = stageDefinition.id();
        boolean alreadyCleared = clearedStages.contains(stageId);
        clearedStages.add(stageId);
        fireEvent(new StageClearedEvent(this, stageId, alreadyCleared));

        // 次のステージをアンロック
        String nextStageId = stageDefinition.nextStageId();
        if (nextStageId != null) {
            unlockStage(nextStageId);
        }
    }



    /**
     * ステージをアンロックします。
     * すでにアンロック済みの場合は何もしません。
     *
     * @param stageId アンロックするステージのID
     */
    public void unlockStage(String stageId) {
        if (!unlockedStages.contains(stageId)) {
            unlockedStages.add(stageId);
            fireEvent(new StageUnlockedEvent(this, stageId));
        }
    }

    /**
     * 進行状況を指定されたデータで復元します。
     * このメソッドはセーブデータのロード時に使用されます。
     *
     * @param clearedStageIds クリア済みステージのIDセット
     * @param unlockedStageIds アンロック済みステージのIDセット
     */
    public void restore(Set<String> clearedStageIds, Set<String> unlockedStageIds) {
        clearedStages.clear();
        clearedStages.addAll(clearedStageIds);

        unlockedStages.clear();
        unlockedStages.addAll(unlockedStageIds);

        // stage1は常にアンロックされている
        if (!unlockedStages.contains("stage1")) {
            unlockedStages.add("stage1");
        }
    }

    /**
     * 進行状況をリセットします。
     * すべてのクリア状態とアンロック状態がクリアされ、stage1のみがアンロックされた状態に戻ります。
     */
    public void reset() {
        clearedStages.clear();
        unlockedStages.clear();
        unlockedStages.add("stage1");
    }
}
