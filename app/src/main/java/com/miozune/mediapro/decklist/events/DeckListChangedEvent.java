package com.miozune.mediapro.decklist.events;

import com.miozune.mediapro.deck.DeckModel;
import java.util.List;

public record DeckListChangedEvent(List<DeckModel> decks, DeckModel selected) implements DeckListPropertyChangeEvent {
    public DeckListChangedEvent {
        decks = List.copyOf(decks);
    }
}
