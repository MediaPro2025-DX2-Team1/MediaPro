package com.miozune.mediapro.drawpile.events;

import com.miozune.mediapro.drawpile.DrawPileModel;

public sealed interface DrawPilePropertyChangeEvent
        permits DrawPileCardDrawnEvent,
                DrawPileResetEvent,
                DrawPileShuffledEvent {

    DrawPileModel getDrawPile();
}
