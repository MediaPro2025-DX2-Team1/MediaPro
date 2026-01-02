package com.miozune.mediapro.player;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * PlayerModelとPlayerViewを仲介するController。
 * ユーザー入力を処理し、Modelを更新する。
 */
public class PlayerController {

    private final PlayerModel model;
    private final PlayerView view;

    /**
     * コンストラクタ。
     * 
     * @param model PlayerModel
     * @param view PlayerView
     */
    public PlayerController(PlayerModel model, PlayerView view) {
        this.model = model;
        this.view = view;

        // ViewのmodelとControllerのmodelが一致しているか確認
        if (view.getPlayerModel() != model) {
            throw new IllegalArgumentException(
                "PlayerView must be initialized with the same PlayerModel instance. " +
                "Use: new PlayerView(model) or ensure the view's model matches."
            );
        }
}
    
    /**
     * 制御対象のModelを取得する。
     * 
     * @return PlayerModel
     */
    public PlayerModel getModel() {
        return model;
    }

    /**
     * 制御対象のViewを取得する。
     * 
     * @return PlayerView
     */
    public PlayerView getView() {
        return view;
    }

    /**
     * デバッグ用のテストパネルを作成する。
     * HPやマナを操作するボタンを配置したパネルを返す。
     * 
     * @return テスト用パネル
     */
    public JPanel createDebugPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Debug Controls"));

        // HP操作ボタン
        JButton damageBtn = new JButton("-10 HP");
        damageBtn.addActionListener(e -> model.takeDamage(10));

        JButton healBtn = new JButton("+20 HP");
        healBtn.addActionListener(e -> model.heal(20));

        // マナ操作ボタン
        JButton addManaBtn = new JButton("+1 Mana");
        addManaBtn.addActionListener(e -> model.addMana());

        JButton consumeManaBtn = new JButton("-2 Mana");
        consumeManaBtn.addActionListener(e -> {
            if (!model.consumeMana(2)) {
                System.out.println("マナ不足！");
            }
        });

        JButton resetManaBtn = new JButton("Reset Mana");
        resetManaBtn.addActionListener(e -> model.resetMana());

        // 名前変更ボタン
        JButton changeNameBtn = new JButton("Change Name");
        changeNameBtn.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(view, "新しい名前:", model.getName());
            if (newName != null && !newName.trim().isEmpty()) {
                model.setName(newName);
            }
        });

        panel.add(damageBtn);
        panel.add(healBtn);
        panel.add(addManaBtn);
        panel.add(consumeManaBtn);
        panel.add(resetManaBtn);
        panel.add(changeNameBtn);

        return panel;
    }
}
