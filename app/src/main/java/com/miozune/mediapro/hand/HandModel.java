package com.miozune.mediapro.hand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.hand.events.HandPropertyChangeEvent;
import com.miozune.mediapro.hand.events.HandCardChangedEvent;

public class HandModel {
    private final List<CardModel> cards;

    public HandModel() {
        this.cards = new ArrayList<>(); 
    }

    // リスナー管理用のインターフェースとメソッド
    @FunctionalInterface
    public interface PropertyChangeListener {
        void onPropertyChanged(HandPropertyChangeEvent event);
    }

    private final List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<>();
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    private void fireEvent(HandPropertyChangeEvent event) {
        for (PropertyChangeListener listener : listeners) {
            listener.onPropertyChanged(event);
        }
    }

    // カード操作メソッド
    public List<CardModel> getCards() {
        return this.cards; 
    }
    
    public void addCard(CardModel card) {
        List<CardModel> oldcards = new ArrayList<>(this.cards);
        this.cards.add(card);
        fireEvent(new HandCardChangedEvent(this, oldcards, new ArrayList<>(this.cards)));
        
    }

    public void removeCard(CardModel card) {
        List<CardModel> oldcards = new ArrayList<>(this.cards);
        this.cards.remove(card);
        fireEvent(new HandCardChangedEvent(this, oldcards, new ArrayList<>(this.cards)));
    }

    public static HandModel createDefaultHand() {
        HandModel model = new HandModel();
        for(int i = 0; i < 8; i++) {
            CardModel sample = CardModel.createSample();
            model.addCard(sample);
        }
        return model;
    }
}
