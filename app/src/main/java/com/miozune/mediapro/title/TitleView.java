package com.miozune.mediapro.title;

import com.miozune.mediapro.preview.Previewable;
import com.miozune.mediapro.util.ButtonStyler;
import com.miozune.mediapro.util.ImageLoader;
import com.miozune.mediapro.util.ImageUtils;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class TitleView extends JPanel implements Previewable {

    private JButton startButton;
    private final BufferedImage backgroundImage;

    public TitleView() {
        backgroundImage = ImageLoader.loadBackgroundImage("title_bg.png");
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
        startButton.setFont(new Font("Serif", Font.BOLD, 32));
        startButton.setPreferredSize(new Dimension(240, 90));

        // カスタムボタンスタイルを適用
        ButtonStyler.applyStyle(startButton);
    }

    private void layoutComponents() {
        JLabel titleLabel = new JLabel("TITLE NAME");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 56));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setOpaque(false);

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        add(titleLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 60, 0));
        buttonPanel.add(startButton);

        add(buttonPanel, BorderLayout.SOUTH);
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageUtils.drawBackgroundImage(g, backgroundImage, getWidth(), getHeight());
    }
}
