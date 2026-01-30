package com.miozune.mediapro.card.events;

/**
 * カードに対するユーザー操作イベントの基底インターフェース。
 */
public sealed interface CardInteractionEvent permits CardClickedEvent {
}
