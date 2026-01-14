package com.miozune.mediapro.discard.events;

import java.util.List;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.discard.DiscardModel;

public record DiscardCardChangedEvent(
    DiscardModel discard,
    List<CardModel> oldcards,
    List<CardModel> newcards
) implements DiscardPropertyChangeEvent {
    
    @Override
    public DiscardModel getDiscard() {
        return discard;
    }
}
