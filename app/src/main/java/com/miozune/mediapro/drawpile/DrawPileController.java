package com.miozune.mediapro.drawpile;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.game.GameConfig;
import java.util.List;

public class DrawPileController {

    private final DrawPileModel model;
    private final DrawPileView view;

    public DrawPileController(DrawPileModel model, DrawPileView view) {
        this.model = model;
        this.view = view;
    }

    /* ターン初めに5枚引く */
    public List<CardModel> drawTurnCards() {
        return model.drawCards(GameConfig.HAND_SIZE);
    }

    /* 任意の枚数引く */
    public List<CardModel> drawSpecifiedCards(int count) {
        return model.drawCards(count);
    }

    public DrawPileModel getModel() {
        return model;
    }

    public DrawPileView getView() {
        return view;
    }
}
