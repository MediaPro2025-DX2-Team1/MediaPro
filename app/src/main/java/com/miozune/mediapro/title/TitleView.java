package com.miozune.mediapro.title;

import javax.swing.*;
import java.awt.*;
import com.miozune.mediapro.preview.Previewable;

public class TitleView extends JPanel implements Previewable {

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

    @Override
    public String getPreviewDescription() {
        return "タイトル画面のプレビュー";
    }

    @Override
    public void setupPreview() {
        startButton.addActionListener(e -> System.out.println("Start clicked"));
    }
}