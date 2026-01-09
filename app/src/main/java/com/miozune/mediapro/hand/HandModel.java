package com.miozune.mediapro.hand;

import java.util.ArrayList;
import java.util.List;

import com.miozune.mediapro.card.CardModel;

public class HandModel {
    private List<CardModel> cards = new ArrayList<>();

    public HandModel() {

    }
    
    /** 手札にカードを追加する */
    public void addCard(CardModel card) {
        if (card != null) {
            cards.add(card);
        }
    }

    /** 手札から特定のカードを削除する */
    public void removeCard(CardModel card) {
        cards.remove(card);
    }

    /** 手札のリストを取得する */
    public List<CardModel> getCards() {
        return cards; 
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public int getHandValue() {
        throw new UnsupportedOperationException();
    }
}
