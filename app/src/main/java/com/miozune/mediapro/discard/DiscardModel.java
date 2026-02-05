package com.miozune.mediapro.discard;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.discard.events.DiscardCardChangedEvent;
import com.miozune.mediapro.discard.events.DiscardPropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
        return List.copyOf(cards);
    }

    public void addCard(CardModel card) {
        if (card == null) {
            return;
        }
        List<CardModel> oldCards = List.copyOf(cards);
        cards.add(card);
        fireEvent(new DiscardCardChangedEvent(this, oldCards, List.copyOf(cards)));
    }

    public void removeCard(CardModel card) {
        if (card == null) {
            return;
        }
        List<CardModel> oldCards = List.copyOf(cards);
        cards.remove(card);
        fireEvent(new DiscardCardChangedEvent(this, oldCards, List.copyOf(cards)));
    }

    /**
     * 捨て札から全カードを取り出して返します。
     * 捨て札は空になります。山札の再構築に使用します。
     *
     * @return 取り出された全カードのリスト
     */
    public List<CardModel> removeAllCards() {
        List<CardModel> oldCards = List.copyOf(cards);
        List<CardModel> removed = new ArrayList<>(cards);
        cards.clear();
        fireEvent(new DiscardCardChangedEvent(this, oldCards, List.copyOf(cards)));
        return removed;
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
