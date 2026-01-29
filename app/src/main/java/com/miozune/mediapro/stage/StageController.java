package com.miozune.mediapro.stage;

import com.miozune.mediapro.card.CardView;
import com.miozune.mediapro.game.GameModel;
import com.miozune.mediapro.hand.events.HandCardChangedEvent;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class StageController {

    private final GameModel gameModel;
    private final StageModel model;
    private final StageView view;

    public StageController(GameModel gameModel, StageModel model, StageView view) {
        this.gameModel = gameModel;
        this.model = model;
        this.view = view;

        // バトル終了リスナー登録
        model.setBattleListener(playerWon -> handleBattleEnd(playerWon));

        model.getHand().addPropertyChangeListener(event -> {
            if (event instanceof HandCardChangedEvent handEvent) {
                view.updateHand(handEvent.newcards(), this::handleCardClick);
            }
        });

        model.getPlayer().addPropertyChangeListener(event -> model.checkBattleState());
        model.getEnemies().forEach(enemy -> enemy.addPropertyChangeListener(event -> model.checkBattleState()));

        // プレイヤー/敵ビューをセット
        view.setActors(model.getPlayer(), model.getEnemies().isEmpty() ? null : model.getEnemies().get(0));

        connectUI();
        updateView();
    }

    /* View のボタンやイベントを Model とつなぐ */
    private void connectUI() {
        view.getDeckButton().addActionListener(e -> System.out.println("山札確認"));

        view.getDiscardButton().addActionListener(e -> System.out.println("捨札確認"));

        view.getEndTurnButton().addActionListener(e -> {
            model.nextTurn();
            updateView();
        });
    }

    /* Model の情報を View に反映 */
    private void updateView() {
        view.updateHand(model.getHand().getCards(), this::handleCardClick);
    }

    /* バトル終了時の処理 */
    private void handleBattleEnd(boolean playerWon) {
        view.getDeckButton().setEnabled(false);
        view.getDiscardButton().setEnabled(false);
        view.getEndTurnButton().setEnabled(false);
        String message = playerWon ? "勝利しました" : "敗北しました";
        if (!playerWon) {
            model.getPlayer().resetAfterDefeat();
        }
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(view, message, "戦闘結果", JOptionPane.INFORMATION_MESSAGE);
            gameModel.goToWorld();
        });
    }

    @SuppressWarnings("unused")
    private void handleCardClick(CardView cardView, MouseEvent ignoredEvent) {
        if (model.isBattleOver() || model.getTurn() != StageModel.Turn.PLAYER) {
            return;
        }
        var target = model.firstAliveEnemy();
        if (target == null) {
            return;
        }
        boolean played = model.playCard(cardView.getCardModel(), target);
        if (!played) {
            System.out.println("カードを使用できませんでした");
        }
        updateView();
    }
}
