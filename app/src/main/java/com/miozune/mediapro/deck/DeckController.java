package com.miozune.mediapro.deck;

import com.miozune.mediapro.card.CardRegistry;
import com.miozune.mediapro.game.GameModel;

public class DeckController {
    private final DeckModel model;
    private final DeckView view;
    private final GameModel gameModel;

    public DeckController(GameModel gameModel, DeckModel model, DeckView view) {
        this.gameModel = gameModel;
        this.model = model;
        this.view = view;
        setupViewListeners();
    }

    private void setupViewListeners() {
        view.setOnDeckCardClick(model::removeCard);
        view.setOnAvailableCardClick(model::addCard);

        view.setAvailableCards(CardRegistry.getInstance().listAll());

        view.getBackButton().addActionListener(e -> gameModel.goToDeckList());
    }

    // 他のViewへの遷移はログ出力（未実装）
    public void navigateToOtherView() {
        System.out.println("他のViewへの遷移: 未実装");
    }
}
