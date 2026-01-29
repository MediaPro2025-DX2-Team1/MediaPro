package com.miozune.mediapro.deck;

import com.miozune.mediapro.card.CardRegistry;
import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import com.miozune.mediapro.game.GameModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DeckController {
    private final DeckModel model;
    private final DeckView view;
    private final GameModel gameModel;

    public DeckController(GameModel gameModel, DeckModel model, DeckView view) {
        this.gameModel = gameModel;
        this.model = model;
        this.view = view;
        setupViewListeners();
    }

    private void setupViewListeners() {
        view.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ダミーカードをレジストリから追加（実際は選択UIを後続で実装）
                CardRecipeModel dummyCard = CardRegistry.getInstance()
                        .find("Fireball");
                if (dummyCard == null) {
                    dummyCard = new CardRecipeModel("Fireball", 2, "fireball.png", "火の玉を投げる", CardRecipeModel.EffectType.DAMAGE, 10);
                    CardRegistry.getInstance().register(dummyCard);
                }
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

        view.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameModel.goToDeckList();
            }
        });
    }

    // 他のViewへの遷移はログ出力（未実装）
    public void navigateToOtherView() {
        System.out.println("他のViewへの遷移: 未実装");
    }
}
