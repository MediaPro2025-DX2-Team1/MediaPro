package com.miozune.mediapro.drawpile;

import com.miozune.mediapro.drawpile.events.DrawPileCardDrawnEvent;
import com.miozune.mediapro.drawpile.events.DrawPileShuffledEvent;
import com.miozune.mediapro.drawpile.events.DrawPileResetEvent;
import com.miozune.mediapro.preview.Previewable;

import javax.swing.*;
import java.awt.*;

public class DrawPileView extends JPanel implements Previewable {

    private final DrawPileModel model;
    private int remainingCards;

    /* no-argコンストラクタ（Previewable要件） */
    public DrawPileView() {
        this(DrawPileModel.createDefault());
    }

    /* Modelを受け取るコンストラクタ */
    public DrawPileView(DrawPileModel model) {
        this.model = model;
        this.remainingCards = model.getRemainingCount();

        setupPanel();
        setupModelListener();
    }

    private void setupPanel() {
        setBackground(new Color(45, 45, 45));
        setPreferredSize(new Dimension(120, 160));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    }

    private void setupModelListener() {
        model.addPropertyChangeListener(event -> {
            switch (event) {
                case DrawPileCardDrawnEvent e -> {
                    remainingCards = e.cardsRemaining();
                    repaint();
                }
                case DrawPileShuffledEvent e -> {
                    remainingCards = e.cardsRemaining();
                    repaint();
                }
                case DrawPileResetEvent e -> {
                    remainingCards = e.cardsRemaining();
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // タイトル
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        g2d.drawString("DRAW PILE", 15, 25);

        // 残り枚数表示
        g2d.setFont(new Font("Monospaced", Font.BOLD, 28));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString(String.valueOf(remainingCards), 30, 80);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2d.setColor(Color.GRAY);
        g2d.drawString("cards", 20, 110);
    }

    @Override
    public String getPreviewDescription() {
        return "Draw Pile - 山札の表示";
    }

    @Override
    public void setupPreview() {
        // デッキにダミーカードを追加してプレビュー用にシャッフル
        model.initialize();
    }

    public DrawPileModel getModel() {
        return model;
    }
}
