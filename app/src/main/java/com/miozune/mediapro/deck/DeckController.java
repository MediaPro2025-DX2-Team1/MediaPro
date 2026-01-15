package com.miozune.mediapro.deck;

import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class DeckController {
    private final DeckModel model;
    private final DeckView view;

    public DeckController(DeckModel model, DeckView view) {
        this.model = model;
        this.view = view;
        setupViewListeners();
    }

    private void setupViewListeners() {
        view.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ダミーのカード追加（実際は選択ダイアログ等から）
                CardRecipeModel dummyCard = new CardRecipeModel("新規カード", 1, "new.png", "新規カードの説明");
                model.addCard(dummyCard);
            }
        });

        view.getRemoveButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 選択されたカードを削除（実際はリスト選択から）
                List<CardRecipeModel> cards = new ArrayList<>(model.getCards().keySet());
                cards.sort(Comparator.comparingInt(CardRecipeModel::cost).thenComparing(CardRecipeModel::name));
                if (!cards.isEmpty()) {
                    model.removeCard(cards.get(0)); // 最初のカードを削除
                }
            }
        });
    }

    // 他のViewへの遷移はログ出力（未実装）
    public void navigateToOtherView() {
        System.out.println("他のViewへの遷移: 未実装");
    }
}