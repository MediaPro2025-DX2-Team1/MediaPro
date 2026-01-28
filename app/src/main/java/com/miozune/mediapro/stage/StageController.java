package com.miozune.mediapro.stage;

import com.miozune.mediapro.hand.events.HandCardChangedEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StageController {

    private StageModel model;
    private StageView view;

    public StageController(StageModel model, StageView view) {
        this.model = model;
        this.view = view;

        // バトル終了リスナー登録
        model.setBattleListener(playerWon -> handleBattleEnd(playerWon));

        model.getHand().addPropertyChangeListener(event -> {
            if (event instanceof HandCardChangedEvent handEvent) {
                view.updateHand(handEvent.newcards());
            }
        });

        connectUI();
        updateView();
    }

    /* View のボタンやイベントを Model とつなぐ */
    private void connectUI() {

        view.getDrawButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.drawToHand();
                updateView();
            }
        });

        view.getDeckButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("山札確認");
            }
        });

        view.getDiscardButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("捨札確認");
            }
        });
    }

    /* Model の情報を View に反映 */
    private void updateView() {
        view.updatePlayerHP(model.getPlayer().getHp());
        view.updateEnemyHP(model.getEnemies().get(0).getHp());
        view.updateHand(model.getHand().getCards());
    }

    /* バトル終了時の処理 */
    private void handleBattleEnd(boolean playerWon) {
        if (playerWon) {
            System.out.println("勝利画面へ…");
        } else {
            System.out.println("敗北画面へ…");
        }
    }
}
