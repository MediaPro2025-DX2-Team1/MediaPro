package com.miozune.mediapro.discard.events;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.discard.DiscardModel;
import java.util.List;

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
