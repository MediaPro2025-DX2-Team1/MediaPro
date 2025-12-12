package com.miozune.mediapro.card;

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
                handleMouseEnter(e);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                handleMouseExit(e);
            }
        });
    }
    
    /**
     * クリックイベントを処理する。
     *
     * @param e マウスイベント
     */
    private void handleClick(MouseEvent e) {
        System.out.println("Card clicked: " + view.getCardData().name());
        
        if (clickListener != null) {
            clickListener.onCardClicked(view, e);
        }
    }
    
    /**
     * マウスエンターイベントを処理する。
     *
     * @param e マウスイベント
     */
    private void handleMouseEnter(MouseEvent e) {
        hovering = true;
        // TODO: ホバーエフェクト（拡大、ハイライトなど）を実装
        System.out.println("Mouse entered card: " + view.getCardData().name());
    }
    
    /**
     * マウスエグジットイベントを処理する。
     *
     * @param e マウスイベント
     */
    private void handleMouseExit(MouseEvent e) {
        hovering = false;
        // TODO: ホバーエフェクト解除
        System.out.println("Mouse exited card: " + view.getCardData().name());
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
    
    /**
     * カードクリック時のリスナーインターフェース。
     */
    @FunctionalInterface
    public interface CardClickListener {
        /**
         * カードがクリックされた時に呼び出される。
         *
         * @param cardView クリックされたカード
         * @param e マウスイベント
         */
        void onCardClicked(CardView cardView, MouseEvent e);
    }
}
