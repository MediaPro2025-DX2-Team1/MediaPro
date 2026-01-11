package com.miozune.mediapro.discard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.discard.events.DiscardPropertyChangeEvent;
import com.miozune.mediapro.discard.events.DiscardCardChangedEvent;

public class DiscardModel {
    private final List<CardModel> cards;

    public DiscardModel() {
        this.cards = new ArrayList<>();
    }
    
    // リスナー管理用のインターフェースとメソッド
    @FunctionalInterface
    public interface PropertyChangeListener{
        void onPropertyChanged(DiscardPropertyChangeEvent event);
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

    private void fireEvent(DiscardPropertyChangeEvent event) {
        for (PropertyChangeListener listener : listeners) {
            listener.onPropertyChanged(event);
        }
    }
    
    // カード操作メソッド
    public List<CardModel> getCards() {
        return this.cards;
    }

    public void addCard(CardModel card) {
        List<CardModel> oldCards = new ArrayList<>(this.cards);
        this.cards.add(card);
        fireEvent(new DiscardCardChangedEvent(this, oldCards, new ArrayList<>(this.cards)));
    }

    public void removeCard(CardModel card) {
        List<CardModel> oldCards = new ArrayList<>(this.cards); 
        this.cards.remove(card);
        fireEvent(new DiscardCardChangedEvent(this, oldCards, new ArrayList<>(this.cards)));
    }

    public static DiscardModel createDefaultDiscard() {
        DiscardModel model = new DiscardModel();
        for(int i = 0; i < 14; i++) {
            CardModel sample = CardModel.createSample();
            model.addCard(sample);
        }
        return model;
    }
}
