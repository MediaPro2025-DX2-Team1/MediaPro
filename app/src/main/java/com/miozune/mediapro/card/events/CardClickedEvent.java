package com.miozune.mediapro.card.events;

import com.miozune.mediapro.card.CardModel;

/**
 * カードクリックを表すイベント。
 */
public record CardClickedEvent(CardModel card, ClickType clickType) implements CardInteractionEvent {

    public boolean isLeftClick() {
        return clickType == ClickType.LEFT;
    }

    public boolean isRightClick() {
        return clickType == ClickType.RIGHT;
    }
}
