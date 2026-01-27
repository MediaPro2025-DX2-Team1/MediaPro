package com.miozune.mediapro.decklist;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.game.GameModel;

public class DeckListModel {

    @FunctionalInterface
    public interface PropertyChangeListener {
        void onChanged();
    }

    private final GameModel gameModel;
    private final List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<>();
    private DeckModel selected;

    public DeckListModel(GameModel gameModel) {
        this.gameModel = gameModel;
        this.selected = gameModel.getActiveDeck();
    }

    public List<DeckModel> getDecks() {
        return gameModel.getDecks();
    }

    public DeckModel getSelected() {
        return selected;
    }

    public void select(DeckModel deck) {
        if (deck == null || deck == selected) {
            return;
        }
        selected = deck;
        fireChanged();
    }

    public DeckModel createDeck(String name) {
        DeckModel deck = gameModel.createDeck(name);
        selected = deck;
        fireChanged();
        return deck;
    }

    public void deleteSelected() {
        if (selected == null) {
            return;
        }
        gameModel.removeDeck(selected);
        selected = gameModel.getActiveDeck();
        fireChanged();
    }

    public void addListener(PropertyChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    private void fireChanged() {
        for (PropertyChangeListener listener : listeners) {
            listener.onChanged();
        }
    }
}
