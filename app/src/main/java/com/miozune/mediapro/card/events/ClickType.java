package com.miozune.mediapro.card.events;

import java.awt.event.MouseEvent;

/**
 * クリック種別を表す列挙型。
 */
public enum ClickType {
    LEFT,
    RIGHT,
    MIDDLE,
    OTHER;

    /**
     * MouseEvent から対応するクリック種別を判定する。
     * @param event マウスイベント
     * @return 判定されたクリック種別
     */
    public static ClickType fromMouseEvent(MouseEvent event) {
        if (event == null) {
            return OTHER;
        }
        if (MouseEvent.BUTTON1 == event.getButton()) {
            return LEFT;
        }
        if (MouseEvent.BUTTON2 == event.getButton()) {
            return MIDDLE;
        }
        if (MouseEvent.BUTTON3 == event.getButton()) {
            return RIGHT;
        }
        return OTHER;
    }
}
