package com.miozune.mediapro.discard;

import com.miozune.mediapro.card.CardModel;

public class DiscardController {
    private final DiscardModel model;
    private final DiscardView view;

    public DiscardController(DiscardModel model, DiscardView view) {
        this.model = model;
        this.view = view;

        // 閉じるボタンの処理
        this.view.setCloseButtonListener(e -> closeDiscardView());

        // カードクリック時の処理
        this.view.setCardClickListener(event -> onCardClick(event.card()));
    }

    private void closeDiscardView() {
        System.out.println("捨て札一覧を閉じる");
        // 親コンテナからの削除等の処理
    }

    public void onCardClick(CardModel cardModel) {
        view.showCardDetail(cardModel);
    }

    public DiscardModel getModel() {
        return model;
    }

    public DiscardView getView() {
        return view;
    }
}
