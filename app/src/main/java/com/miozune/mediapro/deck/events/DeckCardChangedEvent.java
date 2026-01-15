package com.miozune.mediapro.deck.events;

import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import com.miozune.mediapro.deck.DeckModel;

public record DeckCardChangedEvent(
        DeckModel deck,
        CardRecipeModel cardRecipe,
        int oldCount,
        int newCount) implements DeckPropertyChangeEvent {

    @Override
    public DeckModel getDeck() {
        return deck;
    }

    public int getDelta() {
        return newCount - oldCount;
    }
}