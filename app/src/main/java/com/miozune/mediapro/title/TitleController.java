package com.miozune.mediapro.title;

import java.util.Objects;

import javax.swing.JButton;

import com.miozune.mediapro.game.GameModel;

public class TitleController {

    private final TitleView view;

    private final GameModel gameModel;

    public TitleController(TitleView view, GameModel gameModel) {
        this.view = Objects.requireNonNull(view, "view");
        this.gameModel = Objects.requireNonNull(gameModel, "gameModel");
        JButton startButton = this.view.getStartButton();
        startButton.addActionListener(e -> this.gameModel.goToWorld());
    }
}
