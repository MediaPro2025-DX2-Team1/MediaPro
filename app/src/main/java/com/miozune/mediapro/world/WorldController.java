package com.miozune.mediapro.world;

import java.util.Objects;

import javax.swing.JButton;

import com.miozune.mediapro.game.GameModel;

public class WorldController {

    private final WorldModel worldModel;
    private final WorldView view;
    private final GameModel gameModel;

    public WorldController(WorldModel worldModel, WorldView view, GameModel gameModel) {
        this.worldModel = Objects.requireNonNull(worldModel, "worldModel");
        this.view = Objects.requireNonNull(view, "view");
        this.gameModel = Objects.requireNonNull(gameModel, "gameModel");

        wireStageButton(view.getStageButton1(), 1);
        wireStageButton(view.getStageButton2(), 2);
        wireStageButton(view.getStageButton3(), 3);
        view.getDeckListButton().addActionListener(e -> gameModel.goToDeckList());
    }

    private void wireStageButton(JButton button, int stageNumber) {
        button.addActionListener(e -> gameModel.startStage(stageNumber));
    }

    public WorldModel getModel() {
        return worldModel;
    }
}
