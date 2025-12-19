package com.miozune.mediapro.hand;

import java.util.List;

import com.miozune.mediapro.card.CardModel;

public class HandModel {
    private List<CardModel> cards;

    public HandModel() {
        
    }
    
    public void addCard(CardModel card) {
        throw new UnsupportedOperationException();
    }

    public void removeCard(CardModel card) {
        throw new UnsupportedOperationException();
    }

    public List<CardModel> getCards() {
        throw new UnsupportedOperationException();  
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public int getHandValue() {
        throw new UnsupportedOperationException();
    }
}
