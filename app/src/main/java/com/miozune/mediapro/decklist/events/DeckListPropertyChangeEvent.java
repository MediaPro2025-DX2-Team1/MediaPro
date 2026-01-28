package com.miozune.mediapro.decklist.events;

import com.miozune.mediapro.deck.DeckModel;
import java.util.List;

public sealed interface DeckListPropertyChangeEvent
        permits DeckListSelectionChangedEvent, DeckListChangedEvent {
    DeckModel selected();
    List<DeckModel> decks();
}
