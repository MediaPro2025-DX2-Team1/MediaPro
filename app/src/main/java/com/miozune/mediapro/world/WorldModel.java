package com.miozune.mediapro.world;

import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.player.PlayerModel;
import com.miozune.mediapro.progress.ProgressModel;
import com.miozune.mediapro.stage.StageDefinition;
import com.miozune.mediapro.stage.StageFactory;
import com.miozune.mediapro.stage.StageModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class WorldModel {
    private final List<StageDefinition> stageDefinitions;
    private final StageFactory stageFactory;
    private final ProgressModel progressModel;
    private StageDefinition selectedStageDefinition;
    private StageModel currentStage;

    public WorldModel(List<StageDefinition> stageDefinitions, StageFactory stageFactory, ProgressModel progressModel) {
        this.stageDefinitions = Collections.unmodifiableList(new ArrayList<>(stageDefinitions));
        this.stageFactory = Objects.requireNonNull(stageFactory, "stageFactory");
        this.progressModel = Objects.requireNonNull(progressModel, "progressModel");

        if (this.stageDefinitions.isEmpty()) {
            throw new IllegalArgumentException("Stage definitions must not be empty");
        }
    }

    public static WorldModel createDefault(StageFactory stageFactory, ProgressModel progressModel) {
        List<StageDefinition> defaults = List.of(
            StageDefinition.createStage1(),
            StageDefinition.createStage2(),
            StageDefinition.createStage3()
        );
        return new WorldModel(defaults, stageFactory, progressModel);
    }

    public List<StageDefinition> getDefinitions() {
        return stageDefinitions;
    }

    public int getStageCount() {
        return stageDefinitions.size();
    }

    public StageDefinition getDefinitionByIndex(int indexOneBased) {
        int idx = Math.max(1, indexOneBased) - 1;
        if (idx >= stageDefinitions.size()) {
            idx = stageDefinitions.size() - 1;
        }
        return stageDefinitions.get(idx);
    }

    public StageModel createStageFor(PlayerModel player, DeckModel deck, int stageIndex) {
        selectedStageDefinition = getDefinitionByIndex(stageIndex);
        currentStage = stageFactory.create(selectedStageDefinition, player, deck);
        return currentStage;
    }

    public StageModel getCurrentStage() {
        return currentStage;
    }

    public void clearCurrentStage() {
        currentStage = null;
    }

    /**
     * 指定されたステージがアンロック済みかどうかを判定します。
     *
     * @param stageIndex ステージのインデックス（1-based）
     * @return アンロック済みの場合true
     */
    public boolean isStageUnlocked(int stageIndex) {
        String stageId = "stage" + stageIndex;
        return progressModel.isUnlocked(stageId);
    }

    /**
     * 指定されたステージがクリア済みかどうかを判定します。
     *
     * @param stageIndex ステージのインデックス（1-based）
     * @return クリア済みの場合true
     */
    public boolean isStageCleared(int stageIndex) {
        String stageId = "stage" + stageIndex;
        return progressModel.isCleared(stageId);
    }

    /**
     * ProgressModelを取得します。
     *
     * @return ProgressModel
     */
    public ProgressModel getProgressModel() {
        return progressModel;
    }
}
