package com.miozune.mediapro.hand.events;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.hand.HandModel;
import java.util.List;

public record HandCardChangedEvent (
    HandModel hand,
    List<CardModel> oldcards,
    List<CardModel> newcards
) implements HandPropertyChangeEvent {

    @Override
    public HandModel getHand() {
        return hand;
    }
}
