package com.miozune.mediapro.save;

import java.util.HashSet;
import java.util.Set;

/**
 * セーブデータを表すクラス。
 * JSON形式でのシリアライズ/デシリアライズに使用されます。
 */
public class SaveData {

    private Set<String> clearedStages = new HashSet<>();
    private Set<String> unlockedStages = new HashSet<>();

    /**
     * デフォルトコンストラクタ（JSON デシリアライズ用）。
     */
    public SaveData() {
    }

    /**
     * セーブデータを作成します。
     *
     * @param clearedStages クリア済みステージのIDセット
     * @param unlockedStages アンロック済みステージのIDセット
     */
    public SaveData(Set<String> clearedStages, Set<String> unlockedStages) {
        this.clearedStages = new HashSet<>(clearedStages);
        this.unlockedStages = new HashSet<>(unlockedStages);
    }

    /**
     * クリア済みステージのIDセットを取得します。
     *
     * @return クリア済みステージのIDセット
     */
    public Set<String> getClearedStages() {
        return clearedStages;
    }

    /**
     * クリア済みステージのIDセットを設定します。
     *
     * @param clearedStages クリア済みステージのIDセット
     */
    public void setClearedStages(Set<String> clearedStages) {
        this.clearedStages = clearedStages;
    }

    /**
     * アンロック済みステージのIDセットを取得します。
     *
     * @return アンロック済みステージのIDセット
     */
    public Set<String> getUnlockedStages() {
        return unlockedStages;
    }

    /**
     * アンロック済みステージのIDセットを設定します。
     *
     * @param unlockedStages アンロック済みステージのIDセット
     */
    public void setUnlockedStages(Set<String> unlockedStages) {
        this.unlockedStages = unlockedStages;
    }
}
