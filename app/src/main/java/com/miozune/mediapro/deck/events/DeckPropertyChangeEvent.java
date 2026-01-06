package com.miozune.mediapro.deck.events;

import com.miozune.mediapro.deck.DeckModel;

public sealed interface DeckPropertyChangeEvent
        permits DeckNameChangedEvent, DeckCardAddedEvent, DeckCardRemovedEvent {

    DeckModel getDeck();
}