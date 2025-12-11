package com.miozune.mediapro.battlescreen;

import javax.swing.*;
import com.miozune.mediapro.preview.Previewable;

import java.awt.*;

public class BattleView extends JPanel implements Previewable {

    private JPanel topPanel;
    private JPanel bottomPanel;

    private JPanel playerPanel;
    private JPanel enemyPanel;

    private JPanel handPanel;
    private JPanel actionPanel;

    // Controllerが要求するボタン
    private JButton drawButton;
    private JButton deckButton;
    private JButton discardButton;

    private JLabel playerHpLabel;
    private JLabel enemyHpLabel;

    public BattleView() {
        setLayout(new BorderLayout());

        /* 上部構成 */
        topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.setPreferredSize(new Dimension(getWidth(), (int) (getHeight() * 0.66)));

        playerPanel = createCharacterPanel(true);
        enemyPanel = createCharacterPanel(false);

        topPanel.add(playerPanel);
        topPanel.add(enemyPanel);

        /* 下部構成 */
        bottomPanel = new JPanel(new GridLayout(1, 2));

        handPanel = new JPanel();
        handPanel.setBackground(new Color(240, 240, 240));
        handPanel.add(new JLabel("手札パネル（未実装）"));

        actionPanel = new JPanel();
        actionPanel.setBackground(new Color(240, 240, 240));
        actionPanel.setLayout(new GridLayout(3, 1, 10, 10));

        drawButton = new JButton("ドロー");
        deckButton = new JButton("山札確認");
        discardButton = new JButton("捨札確認");

        actionPanel.add(drawButton);
        actionPanel.add(deckButton);
        actionPanel.add(discardButton);

        JSplitPane bottomSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                handPanel,
                actionPanel);
        bottomSplit.setResizeWeight(0.6);
        bottomSplit.setDividerSize(5);

        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(bottomSplit);

        add(topPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createCharacterPanel(boolean isPlayer) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel(isPlayer ? "Player" : "Enemy");
        nameLabel.setFont(new Font("Serif", Font.BOLD, 24));

        JLabel hpLabel = new JLabel("HP: ---");
        hpLabel.setFont(new Font("Serif", Font.PLAIN, 20));

        if (isPlayer)
            playerHpLabel = hpLabel;
        else
            enemyHpLabel = hpLabel;

        p.add(nameLabel);
        p.add(Box.createVerticalStrut(15));
        p.add(hpLabel);

        return p;
    }

    // Controllerが必要とするgetter
    public JButton getDrawButton() {
        return drawButton;
    }

    public JButton getDeckButton() {
        return deckButton;
    }

    public JButton getDiscardButton() {
        return discardButton;
    }

    // HPを更新するメソッド
    public void updatePlayerHP(int hp) {
        playerHpLabel.setText("HP: " + hp);
    }

    public void updateEnemyHP(int hp) {
        enemyHpLabel.setText("HP: " + hp);
    }

    @Override
    public String getPreviewName() {
        return "BattleView";
    }

    @Override
    public String getPreviewDescription() {
        return "戦闘画面のレイアウト確認用プレビューです。";
    }

    @Override
    public void setupPreview() {
        // プレビュー用の最低限の初期化
        // ここでダミーのPlayer/Enemy/Handなどを設定してもOK
    }
}
