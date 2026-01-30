package com.miozune.mediapro.card.events;

/**
 * カードクリック時に通知するリスナー。
 */
@FunctionalInterface
public interface CardClickListener {
    void onCardClicked(CardClickedEvent event);
}
