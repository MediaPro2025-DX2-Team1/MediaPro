package com.miozune.mediapro.drawpile;

import java.util.List;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.game.GameConfig;

public class DrawPileController {

    private final DrawPileModel model;
    private final DrawPileView view;

    public DrawPileController(DrawPileModel model, DrawPileView view) {
        this.model = model;
        this.view = view;
    }

    /* ゲーム開始時にデッキから山札を初期化する */
    public void initializeFromDeck() {
        model.initialize();
    }

    /* ターン初めに5枚引く */
    public List<CardModel> drawTurnCards() {
        return model.drawCards(GameConfig.HAND_SIZE);
    }

    /* 任意の枚数引く */
    public List<CardModel> drawSpecifiedCards(int count) {
        return model.drawCards(count);
    }

    /* 山札が5枚以下かチェック */
    public boolean checkIfNeedsReset() {
        return model.isLowOnCards(5);
    }

    /* 山札をリセット（hand, discard, deckのリセットはゲーム側で処理） */
    public void resetDrawPile() {
        model.reset();
    }

    public DrawPileModel getModel() {
        return model;
    }

    public DrawPileView getView() {
        return view;
    }
}
