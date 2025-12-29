package com.miozune.mediapro.world;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.miozune.mediapro.preview.Previewable;

@Previewable(description = "ステージ選択画面のプレビュー")
public class WorldView extends JPanel {

    private JButton stageButton1;
    private JButton stageButton2;
    private JButton stageButton3;

    public WorldView() { 
        setupPanel();
        initComponents();
        layoutComponents();
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(600, 400));
        setOpaque(true);
        setBackground(new Color(240, 240, 240));
    }

    private void initComponents() {
        stageButton1 = new JButton("Stage 1");
        stageButton1.setPreferredSize(new Dimension(200, 60));
        stageButton1.setFont(new Font("Arial", Font.BOLD, 24));

        stageButton2 = new JButton("Stage 2");
        stageButton2.setPreferredSize(new Dimension(200, 60));
        stageButton2.setFont(new Font("Arial", Font.BOLD, 24));

        stageButton3 = new JButton("Stage 3");
        stageButton3.setPreferredSize(new Dimension(200, 60));
        stageButton3.setFont(new Font("Arial", Font.BOLD, 24));
    }

    private void layoutComponents() {
        JLabel titleLabel = new JLabel("STAGE SELECT");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 56));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // ボタン間の余白

        gbc.gridx = 0; 
        gbc.gridy = 0;
        buttonPanel.add(stageButton1, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        buttonPanel.add(stageButton2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        buttonPanel.add(stageButton3, gbc);

        add(titlePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    public JButton getStageButton1() { return stageButton1; }
    public JButton getStageButton2() { return stageButton2; }
    public JButton getStageButton3() { return stageButton3; }

    /**
     * プレビュー用のインスタンスを生成する。
     * ダミーのイベントリスナーを追加して動作確認ができるようにする。
     *
     * @return プレビュー用のWorldViewインスタンス
     */
    public static WorldView createPreview() {
        WorldView view = new WorldView();
        view.stageButton1.addActionListener(e -> System.out.println("[Preview] Stage 1 clicked"));
        view.stageButton2.addActionListener(e -> System.out.println("[Preview] Stage 2 clicked"));
        view.stageButton3.addActionListener(e -> System.out.println("[Preview] Stage 3 clicked"));
        return view;
    }
}