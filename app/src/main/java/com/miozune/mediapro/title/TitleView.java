package com.miozune.mediapro.title;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.miozune.mediapro.preview.Previewable;

@Previewable(description = "タイトル画面のプレビュー")
public class TitleView extends JPanel {

    private JButton startButton;

    public TitleView() {
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
        startButton = new JButton("START");
        startButton.setFont(new Font("Arial", Font.BOLD, 32));
        startButton.setPreferredSize(new Dimension(180, 60));
        startButton.setForeground(Color.BLACK);
        startButton.setBackground(Color.WHITE);
        startButton.setOpaque(true);
        startButton.setBorderPainted(true);
        startButton.setContentAreaFilled(true);
        startButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        startButton.setFocusPainted(false);
    }

    private void layoutComponents() {
        JLabel titleLabel = new JLabel("TITLE NAME");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 56));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        add(titleLabel, BorderLayout.CENTER);

        JPanel buttonpanel = new JPanel(new GridBagLayout());
        buttonpanel.setBackground(new Color(240, 240, 240)); 
        buttonpanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 60, 0));
        buttonpanel.add(startButton);

        add(buttonpanel, BorderLayout.SOUTH);
    }

    public JButton getStartButton() {
        return startButton;
    }

    /**
     * プレビュー用のインスタンスを生成する。
     * ダミーのイベントリスナーを追加して動作確認ができるようにする。
     *
     * @return プレビュー用のTitleViewインスタンス
     */
    public static TitleView createPreview() {
        TitleView view = new TitleView();
        view.startButton.addActionListener(e -> System.out.println("[Preview] Start clicked"));
        return view;
    }
}