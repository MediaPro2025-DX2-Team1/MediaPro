package com.miozune.mediapro.card;

import com.miozune.mediapro.card.events.CardClickListener;
import com.miozune.mediapro.card.events.CardClickedEvent;
import com.miozune.mediapro.card.events.ClickType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * カードのユーザー入力を処理するController。
 * クリック、ホバーなどのイベントを処理する。
 */
public class CardController {

    /** 制御対象のView */
    private final CardView view;

    /** カードがホバー中かどうか */
    private boolean hovering;

    /** カードクリック時のリスナー */
    private CardClickListener clickListener;

    /**
     * CardControllerを作成する。
     *
     * @param view 制御対象のCardView
     */
    public CardController(CardView view) {
        this.view = view;
        this.hovering = false;

        setupInputHandlers();
    }

    /**
     * 入力ハンドラーを設定する。
     */
    private void setupInputHandlers() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                handleMouseEnter();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                handleMouseExit();
            }
        });
    }

    /**
     * クリックイベントを処理する。
     *
     * @param e マウスイベント
     */
    private void handleClick(MouseEvent e) {
        System.out.println("Card clicked: " + view.getCardModel().name());

        if (clickListener != null) {
            CardClickedEvent event = new CardClickedEvent(view.getCardModel(), ClickType.fromMouseEvent(e));
            clickListener.onCardClicked(event);
        }
    }

    /**
     * マウスエンターイベントを処理する。
     *
     * @param e マウスイベント
     */
    private void handleMouseEnter() {
        hovering = true;
        // TODO: ホバーエフェクト（拡大、ハイライトなど）を実装
        System.out.println("Mouse entered card: " + view.getCardModel().name());
    }

    /**
     * マウスエグジットイベントを処理する。
     *
     * @param e マウスイベント
     */
    private void handleMouseExit() {
        hovering = false;
        // TODO: ホバーエフェクト解除
        System.out.println("Mouse exited card: " + view.getCardModel().name());
    }

    /**
     * カードがホバー中かどうかを取得する。
     *
     * @return ホバー中の場合はtrue
     */
    public boolean isHovering() {
        return hovering;
    }

    /**
     * カードクリック時のリスナーを設定する。
     *
     * @param listener クリックリスナー
     */
    public void setClickListener(CardClickListener listener) {
        this.clickListener = listener;
    }

    /**
     * 制御対象のViewを取得する。
     *
     * @return CardView
     */
    public CardView getView() {
        return view;
    }
}
