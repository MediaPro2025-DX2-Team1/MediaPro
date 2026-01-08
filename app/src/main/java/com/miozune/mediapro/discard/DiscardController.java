package com.miozune.mediapro.discard;

import com.miozune.mediapro.card.CardModel;

public class DiscardController {
    private DiscardModel model;
    private DiscardView view;

    public DiscardController(DiscardModel model, DiscardView view) {
        this.model = model;
        this.view = view;
        
        // 閉じるボタンの処理
        this.view.setCloseButtonListener(e -> closeDiscardView());
        
        // カードクリック時の処理
        this.view.setCardClickListener(e -> {
            Object source = e.getSource();
            if (source instanceof CardModel) {
                onCardClick((CardModel) source);
            }
        });
        
        // 初期表示更新
        refreshView();
    }

    public void refreshView() {
        view.updateView(model.getCards());
    }

    private void closeDiscardView() {
        System.out.println("捨て札一覧を閉じる");
        // 親コンテナからの削除等の処理
    }

    public void onCardClick(CardModel cardModel) {
        System.out.println("カード拡大表示: " + cardModel.name());
        view.showCardDetail(cardModel);
    }
}