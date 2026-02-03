package com.miozune.mediapro.world;

import com.miozune.mediapro.game.GameModel;
import java.util.Objects;
import javax.swing.JButton;

public class WorldController {

    private final WorldModel worldModel;
    private final WorldView view;
    private final GameModel gameModel;

    public WorldController(WorldModel worldModel, WorldView view, GameModel gameModel) {
        this.worldModel = Objects.requireNonNull(worldModel, "worldModel");
        this.view = Objects.requireNonNull(view, "view");
        this.gameModel = Objects.requireNonNull(gameModel, "gameModel");

        wireStageButton(view.getStageButton1(), "stage1");
        wireStageButton(view.getStageButton2(), "stage2");
        wireStageButton(view.getStageButton3(), "stage3");
        view.getDeckListButton().addActionListener(e -> gameModel.goToDeckList());
    }

    private void wireStageButton(JButton button, String stageId) {
        button.addActionListener(e -> gameModel.startStage(stageId));
    }

    public WorldModel getModel() {
        return worldModel;
    }
}
