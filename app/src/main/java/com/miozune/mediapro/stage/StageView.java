package com.miozune.mediapro.stage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.miozune.mediapro.preview.Previewable;

@Previewable(description = "戦闘画面のテストビューです。")
public class StageView extends JPanel {

    private JPanel topPanel;
    private JPanel bottomPanel;

    private JPanel playerPanel;
    private JPanel enemyPanel;

    private JPanel handPanel;
    private JPanel actionPanel;

    private JButton drawButton;
    private JButton deckButton;
    private JButton discardButton;

    private JLabel playerHpLabel;
    private JLabel enemyHpLabel;

    private JProgressBar playerHpBar;
    private JProgressBar enemyHpBar;

    public StageView() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 700));
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        /* --- 上部：戦闘画面エリア --- */
        topPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        topPanel.setOpaque(false);

        playerPanel = createCharacterPanel("PLAYER", true);
        enemyPanel = createCharacterPanel("ENEMY", false);

        topPanel.add(playerPanel);
        topPanel.add(enemyPanel);

        /* --- 下部：操作エリア --- */
        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(0, 200));
        bottomPanel.setOpaque(false);

        // 左側：手札（仮）
        handPanel = new JPanel();
        handPanel.setBackground(new Color(45, 45, 45));
        handPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        handPanel.add(new JLabel("<html><font color='white'>手札パネル（未実装）</font></html>"));

        // 右側：アクションボタン（3分割して埋める）
        actionPanel = new JPanel(new GridLayout(3, 1, 0, 5)); // 縦に3つ並べる
        actionPanel.setPreferredSize(new Dimension(200, 0));
        actionPanel.setBackground(new Color(50, 50, 50));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        drawButton = new JButton("ドロー");
        deckButton = new JButton("山札確認");
        discardButton = new JButton("捨札確認");

        // ボタンのフォントと見た目の微調整
        Font btnFont = new Font("SansSerif", Font.BOLD, 16);
        for (JButton btn : new JButton[] { drawButton, deckButton, discardButton }) {
            btn.setFont(btnFont);
            btn.setFocusPainted(false);
            actionPanel.add(btn);
        }

        bottomPanel.add(handPanel, BorderLayout.CENTER);
        bottomPanel.add(actionPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createCharacterPanel(String name, boolean isPlayer) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(45, 45, 45));
        p.setOpaque(true);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // HPバーの設定
        JProgressBar hpBar = new JProgressBar(0, 100);
        hpBar.setValue(100);
        hpBar.setPreferredSize(new Dimension(250, 25));
        hpBar.setMaximumSize(new Dimension(250, 25)); // BoxLayoutで広がらないように
        hpBar.setForeground(isPlayer ? new Color(46, 204, 113) : new Color(231, 76, 60));
        hpBar.setBackground(new Color(30, 30, 30));
        hpBar.setBorderPainted(false);
        hpBar.setStringPainted(true); // バーの中に%を表示
        hpBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hpLabel = new JLabel("HP: ---");
        hpLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        hpLabel.setForeground(Color.LIGHT_GRAY);
        hpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (isPlayer) {
            playerHpLabel = hpLabel;
            playerHpBar = hpBar;
        } else {
            enemyHpLabel = hpLabel;
            enemyHpBar = hpBar;
        }

        p.add(Box.createVerticalGlue());
        p.add(nameLabel);
        p.add(Box.createVerticalStrut(20));
        p.add(hpBar);
        p.add(Box.createVerticalStrut(10));
        p.add(hpLabel);
        p.add(Box.createVerticalGlue());

        return p;
    }

    public void updatePlayerHP(int hp) {
        playerHpLabel.setText("HP: " + hp);
        playerHpBar.setValue(hp);
    }

    public void updateEnemyHP(int hp) {
        enemyHpLabel.setText("HP: " + hp);
        enemyHpBar.setValue(hp);
    }

    /**
     * プレビュー用のインスタンスを生成する。
     * ダミーのHPを設定して表示確認ができるようにする。
     *
     * @return プレビュー用のStageViewインスタンス
     */
    public static StageView createPreview() {
        StageView view = new StageView();
        view.updatePlayerHP(80);
        view.updateEnemyHP(50);
        return view;
    }

    // Getter
    public JButton getDrawButton() {
        return drawButton;
    }

    public JButton getDeckButton() {
        return deckButton;
    }

    public JButton getDiscardButton() {
        return discardButton;
    }
}