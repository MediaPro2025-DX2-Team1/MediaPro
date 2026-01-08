package com.miozune.mediapro.discard;

import java.util.ArrayList;
import java.util.List;

import com.miozune.mediapro.card.CardModel;

public class DiscardModel {
    private final List<CardModel> cards;

    public DiscardModel() {
        this.cards = new ArrayList<>();
    }

    public List<CardModel> getCards() {
        return this.cards;
    }

    public void addCard(CardModel card) {
        this.cards.add(card);
    }

    public void check() {
        throw new UnsupportedOperationException();
    }   

    public int getSize() {
        throw new UnsupportedOperationException();
    }

    public void reset() {
        throw new UnsupportedOperationException();
    }
}
