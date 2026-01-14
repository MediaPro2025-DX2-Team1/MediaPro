package com.miozune.mediapro.deck;

import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import com.miozune.mediapro.deck.events.DeckPropertyChangeEvent;
import com.miozune.mediapro.deck.events.DeckCardChangedEvent;
import com.miozune.mediapro.deck.events.DeckNameChangedEvent;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DeckModel {
    private String name; // デッキの名前
    private final Map<CardRecipeModel, Integer> cards = new LinkedHashMap<>(); // カードの種類、枚数、追加順を保持

    @FunctionalInterface
    public interface PropertyChangeListener {
        void onPropertyChanged(DeckPropertyChangeEvent event);
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

    private void fireEvent(DeckPropertyChangeEvent event) {
        for (PropertyChangeListener listener : listeners) {
            listener.onPropertyChanged(event);
        }
    }

    // コンストラクタ
    public DeckModel(String name) {
        this.name = name;
    }

    /* カードを1枚追加する */
    public void addCard(CardRecipeModel card) {
        int current = cards.getOrDefault(card, 0);
        cards.put(card, current + 1);
        fireEvent(new DeckCardChangedEvent(this, card, current, current + 1));
    }

    /* カードを1枚減らす */
    public void removeCard(CardRecipeModel card) {
        if (!cards.containsKey(card))
            return;

        int current = cards.get(card);
        if (current <= 1) {
            cards.remove(card); // 0枚になるなら行ごと消す
            fireEvent(new DeckCardChangedEvent(this, card, current, 0));
        } else {
            cards.put(card, current - 1);
            fireEvent(new DeckCardChangedEvent(this, card, current, current - 1));
        }
    }

    /* 特定のカードが今何枚あるかを取得する */
    public int getCount(CardRecipeModel card) {
        return cards.getOrDefault(card, 0);
    }

    /* デッキの総枚数を計算する */
    public int getTotalCount() {
        int total = 0;
        for (int count : cards.values()) {
            total += count;
        }

        return total;
    }

    // getter/setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        fireEvent(new DeckNameChangedEvent(this, oldName, name));
    }

    /* 生のMapデータを取得する */
    public Map<CardRecipeModel, Integer> getCards() {
        return Collections.unmodifiableMap(cards);
    }
}