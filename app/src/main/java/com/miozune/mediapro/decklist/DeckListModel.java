package com.miozune.mediapro.decklist;

import com.miozune.mediapro.card.CardRegistry;
import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.decklist.events.DeckListChangedEvent;
import com.miozune.mediapro.decklist.events.DeckListPropertyChangeEvent;
import com.miozune.mediapro.decklist.events.DeckListSelectionChangedEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DeckListModel {

    @FunctionalInterface
    public interface PropertyChangeListener {
        void onPropertyChanged(DeckListPropertyChangeEvent event);
    }

    private final List<DeckModel> decks = new CopyOnWriteArrayList<>();
    private final List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<>();
    private DeckModel selected;

    public DeckListModel() {
        ensureActiveDeck();
    }

    public List<DeckModel> getDecks() {
        return List.copyOf(decks);
    }

    public DeckModel getSelected() {
        return selected;
    }

    public DeckModel getActiveDeck() {
        return selected;
    }

    public void setActiveDeck(DeckModel deck) {
        if (deck == null) {
            return;
        }
        if (!decks.contains(deck)) {
            decks.add(deck);
        }
        DeckModel previous = selected;
        if (previous == deck) {
            return;
        }
        selected = deck;
        fireEvent(new DeckListSelectionChangedEvent(getDecks(), previous, selected));
    }

    public DeckModel createDeck(String name) {
        DeckModel deck = new DeckModel(name);
        decks.add(deck);
        setActiveDeck(deck);
        fireEvent(new DeckListChangedEvent(getDecks(), selected));
        return deck;
    }

    public void removeDeck(DeckModel deck) {
        if (deck == null) {
            return;
        }
        boolean removed = decks.remove(deck);
        if (!removed) {
            return;
        }
        DeckModel previous = selected;
        if (deck == selected) {
            selected = decks.isEmpty() ? null : decks.get(0);
            fireEvent(new DeckListSelectionChangedEvent(getDecks(), previous, selected));
        }
        fireEvent(new DeckListChangedEvent(getDecks(), selected));
    }

    public void deleteSelected() {
        removeDeck(selected);
    }

    public void ensureActiveDeck() {
        if (selected != null) {
            return;
        }
        if (decks.isEmpty()) {
            DeckModel defaultDeck = createDefaultDeck();
            decks.add(defaultDeck);
            selected = defaultDeck;
            return;
        }
        selected = decks.get(0);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    private void fireEvent(DeckListPropertyChangeEvent event) {
        for (PropertyChangeListener listener : listeners) {
            listener.onPropertyChanged(event);
        }
    }

    private DeckModel createDefaultDeck() {
        DeckModel deck = new DeckModel("スターターデッキ");
        CardRegistry registry = CardRegistry.getInstance();
        CardRecipeModel slash = registry.find("スラッシュ");
        CardRecipeModel block = registry.find("ブロック");
        CardRecipeModel armorBreak = registry.find("鎧砕き");

        if (slash != null) {
            for (int i = 0; i < 4; i++) {
                deck.addCard(slash);
            }
        }
        if (block != null) {
            for (int i = 0; i < 5; i++) {
                deck.addCard(block);
            }
        }
        if (armorBreak != null) {
            deck.addCard(armorBreak);
        }
        return deck;
    }
}
