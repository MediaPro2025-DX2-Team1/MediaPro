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
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorldModel {
    private final List<StageDefinition> stageDefinitions;
    private final Map<String, StageDefinition> stageDefinitionMap;
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

        // IDからStageDefinitionへのマップを構築
        this.stageDefinitionMap = this.stageDefinitions.stream()
            .collect(Collectors.toUnmodifiableMap(
                StageDefinition::id,
                Function.identity(),
                (a, b) -> {
                    throw new IllegalArgumentException("Duplicate stage ID: " + a.id());
                }
            ));
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

    /**
     * ステージIDから定義を取得します。
     * このメソッドはStageDefinitionのidを直接使用するため、推奨される方法です。
     *
     * @param stageId ステージID（例: "stage1", "stage2"）
     * @return ステージ定義
     * @throws IllegalArgumentException 不明なIDの場合
     */
    public StageDefinition getDefinitionById(String stageId) {
        StageDefinition definition = stageDefinitionMap.get(stageId);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown stage ID: " + stageId);
        }
        return definition;
    }

    /**
     * ステージIDからステージを作成します。
     *
     * @param player プレイヤー
     * @param deck デッキ
     * @param stageId ステージID（例: "stage1"）
     * @return 作成されたステージ
     */
    public StageModel createStageFor(PlayerModel player, DeckModel deck, String stageId) {
        selectedStageDefinition = getDefinitionById(stageId);
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
     * @param stageId ステージID（例: "stage1"）
     * @return アンロック済みの場合true
     */
    public boolean isStageUnlocked(String stageId) {
        return progressModel.isUnlocked(stageId);
    }

    /**
     * 指定されたステージがクリア済みかどうかを判定します。
     *
     * @param stageId ステージID（例: "stage1"）
     * @return クリア済みの場合true
     */
    public boolean isStageCleared(String stageId) {
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
