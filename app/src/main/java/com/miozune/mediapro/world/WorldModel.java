package com.miozune.mediapro.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.miozune.mediapro.player.PlayerModel;
import com.miozune.mediapro.stage.StageDefinition;
import com.miozune.mediapro.stage.StageFactory;
import com.miozune.mediapro.stage.StageModel;

public class WorldModel {
    private final List<StageDefinition> definitions;
    private final StageFactory stageFactory;
    private StageDefinition selectedDefinition;
    private StageModel currentStage;

    public WorldModel(List<StageDefinition> definitions, StageFactory stageFactory) {
        this.definitions = Collections.unmodifiableList(new ArrayList<>(definitions));
        this.stageFactory = Objects.requireNonNull(stageFactory, "stageFactory");
    }

    public static WorldModel createDefault(StageFactory stageFactory) {
        List<StageDefinition> defaults = List.of(
            StageDefinition.createSample(1),
            StageDefinition.createSample(2),
            StageDefinition.createSample(3)
        );
        return new WorldModel(defaults, stageFactory);
    }

    public List<StageDefinition> getDefinitions() {
        return definitions;
    }

    public int getStageCount() {
        return definitions.size();
    }

    public StageDefinition getDefinitionByIndex(int indexOneBased) {
        int idx = Math.max(1, indexOneBased) - 1;
        if (idx >= definitions.size()) {
            idx = definitions.size() - 1;
        }
        return definitions.get(idx);
    }

    public StageModel createStageFor(PlayerModel player, com.miozune.mediapro.deck.DeckModel deck, int stageIndex) {
        selectedDefinition = getDefinitionByIndex(stageIndex);
        currentStage = stageFactory.create(selectedDefinition, player, deck);
        return currentStage;
    }

    public StageModel getCurrentStage() {
        return currentStage;
    }

    public void clearCurrentStage() {
        currentStage = null;
    }
}
