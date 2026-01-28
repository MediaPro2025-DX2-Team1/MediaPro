package com.miozune.mediapro.decklist.events;

import com.miozune.mediapro.deck.DeckModel;
import java.util.List;

public record DeckListSelectionChangedEvent(List<DeckModel> decks, DeckModel previous, DeckModel selected)
        implements DeckListPropertyChangeEvent {

    public DeckListSelectionChangedEvent {
        decks = List.copyOf(decks);
    }
}
