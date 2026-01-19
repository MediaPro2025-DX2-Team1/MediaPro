package com.miozune.mediapro.hand;

import com.miozune.mediapro.card.CardModel;

public class HandController {
    
    private HandModel model;
    private HandView view;

    public HandController(HandModel model, HandView view) {
        this.model = model;
        this.view = view;

        this.view.setHandActionListener(new HandView.HandActionListener() {
            @Override
            public void onCardLeftClick(CardModel card) {
                useCard(card);
            }

            @Override
            public void onCardRightClick(CardModel card) {
                showCard(card);
            }
        });
    } 

    public void useCard(CardModel card) {
        System.out.println("カードを使用する");
        // カードの効果を適用するコードをここに追加
    }
    
    public void showCard(CardModel card) {
        this.view.showCardDetail(card);
    }
}