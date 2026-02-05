package com.miozune.mediapro.drawpile.events;

import com.miozune.mediapro.drawpile.DrawPileModel;

public sealed interface DrawPilePropertyChangeEvent
        permits DrawPileCardDrawnEvent,
                DrawPileShuffledEvent,
                DrawPileRefreshedFromDiscardEvent {

    DrawPileModel getDrawPile();
}
