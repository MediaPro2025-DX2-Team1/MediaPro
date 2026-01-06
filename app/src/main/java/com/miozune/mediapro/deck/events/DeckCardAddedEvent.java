package com.miozune.mediapro.deck.events;

import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import com.miozune.mediapro.deck.DeckModel;

public record DeckCardAddedEvent(
        DeckModel deck,
        CardRecipeModel cardRecipe,
        int newCount) implements DeckPropertyChangeEvent {

    @Override
    public DeckModel getDeck() {
        return deck;
    }
}