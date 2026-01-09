package com.miozune.mediapro.stage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.miozune.mediapro.hand.HandController;
import com.miozune.mediapro.hand.HandView;

public class StageController {

    private StageModel model;
    private StageView view;

    public StageController(StageModel model, StageView view) {
        this.model = model;
        this.view = view;

        // ★追加: Hand機能の初期化と統合
        HandView handView = new HandView();
        new HandController(model.getHand(), handView);
        this.view.setHandView(handView);

        // バトル終了リスナー登録
        model.setBattleListener(playerWon -> handleBattleEnd(playerWon));

        connectUI();
        updateView();
    }

    /* View のボタンやイベントを Model とつなぐ */
    private void connectUI() {

        view.getDrawButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.getDeck().drawCard();
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