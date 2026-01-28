package com.miozune.mediapro.drawpile.events;

import com.miozune.mediapro.drawpile.DrawPileModel;

public sealed interface DrawPilePropertyChangeEvent
        permits DrawPileCardDrawnEvent,
                DrawPileShuffledEvent {

    DrawPileModel getDrawPile();
}
