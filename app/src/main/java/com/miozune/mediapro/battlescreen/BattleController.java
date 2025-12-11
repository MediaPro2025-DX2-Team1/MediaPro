package com.miozune.mediapro.battlescreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BattleController {

    private BattleScreen model;
    private BattleView view;

    public BattleController(BattleScreen model, BattleView view) {
        this.model = model;
        this.view = view;

        connectUI();
        updateView();
    }

    /* View のボタンやイベントを Model とつなぐ */
    private void connectUI() {

        view.getDrawButton().addActionListener(new ActionListener() {// ドロー
            @Override
            public void actionPerformed(ActionEvent e) {
                model.getDeck().draw(model.getHand());
                updateView();
            }
        });

        view.getDeckButton().addActionListener(new ActionListener() {// 山札確認
            @Override
            public void actionPerformed(ActionEvent e) {
                // ここに別画面 or ダイアログ表示
                System.out.println("山札確認");
            }
        });

        view.getDiscardButton().addActionListener(new ActionListener() {// 捨札確認
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("捨札確認");
            }
        });
    }

    /* Model の情報を View に反映 */
    private void updateView() {
        // HP の表示更新など
        view.updatePlayerHP(model.getPlayer().getHp());
        view.updateEnemyHP(model.getEnemy().get(0).getHp());

        // バトル終了チェック
        if (model.isBattleOver()) {
            handleBattleEnd();
        }
    }

    /* Model がバトル終了を通知したときに走る処理 */
    private void handleBattleEnd() {
        if (model.isPlayerWin()) {
            System.out.println("勝利画面へ…");
        } else {
            System.out.println("敗北画面へ…");
        }
    }
}