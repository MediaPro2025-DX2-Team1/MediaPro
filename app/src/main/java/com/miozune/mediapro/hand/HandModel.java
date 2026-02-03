package com.miozune.mediapro.hand;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.hand.events.HandCardChangedEvent;
import com.miozune.mediapro.hand.events.HandPropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
        return List.copyOf(cards);
    }

    public void addCard(CardModel card) {
        if (card == null) {
            return;
        }
        List<CardModel> oldcards = List.copyOf(cards);
        cards.add(card);
        fireEvent(new HandCardChangedEvent(this, oldcards, List.copyOf(cards)));
    }

    public void removeCard(CardModel card) {
        if (card == null) {
            return;
        }
        List<CardModel> oldcards = List.copyOf(cards);
        cards.remove(card);
        fireEvent(new HandCardChangedEvent(this, oldcards, List.copyOf(cards)));
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
