package com.miozune.mediapro.drawpile.events;

import com.miozune.mediapro.drawpile.DrawPileModel;

/**
 * 捨て札から山札が再構築された際のイベント。
 *
 * @param drawPile 山札モデル
 * @param newCount 再構築後の山札の枚数
 */
public record DrawPileRefreshedFromDiscardEvent(DrawPileModel drawPile, int newCount)
        implements DrawPilePropertyChangeEvent {

    @Override
    public DrawPileModel getDrawPile() {
        return drawPile;
    }
}
