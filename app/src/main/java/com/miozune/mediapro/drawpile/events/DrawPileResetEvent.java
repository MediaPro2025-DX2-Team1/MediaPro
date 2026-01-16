package com.miozune.mediapro.drawpile.events;

import com.miozune.mediapro.drawpile.DrawPileModel;

public record DrawPileResetEvent(
        DrawPileModel drawPile,
        int cardsRemaining
) implements DrawPilePropertyChangeEvent {

    @Override
    public DrawPileModel getDrawPile() {
        return drawPile;
    }
}
