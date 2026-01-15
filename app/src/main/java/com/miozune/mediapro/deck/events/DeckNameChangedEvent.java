package com.miozune.mediapro.deck.events;

import com.miozune.mediapro.deck.DeckModel;

public record DeckNameChangedEvent(
        DeckModel deck,
        String oldName,
        String newName) implements DeckPropertyChangeEvent {

    @Override
    public DeckModel getDeck() {
        return deck;
    }
}