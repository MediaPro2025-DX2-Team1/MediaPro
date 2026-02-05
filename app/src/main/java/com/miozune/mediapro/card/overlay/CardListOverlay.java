package com.miozune.mediapro.card.overlay;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardView;
import com.miozune.mediapro.card.events.ClickType;
import com.miozune.mediapro.util.ButtonStyler;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;

public class CardListOverlay extends JPanel {

    public CardListOverlay(String title, List<CardModel> cards, Runnable onClose, Consumer<CardModel> onDetail) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                var g2 = g.create();
                if (g2 instanceof Graphics2D g2d) {
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(30, 30, 30, 240));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                }
                g2.dispose();
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Meiryo", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        JButton closeButton = new JButton("× 閉じる");
        closeButton.setFont(new Font("Meiryo", Font.BOLD, 14));
        ButtonStyler.applyStyle(closeButton);
        closeButton.addActionListener(e -> {
            if (onClose != null) {
                onClose.run();
            }
        });
        topBar.add(closeButton, BorderLayout.EAST);
        topBar.add(titleLabel, BorderLayout.CENTER);
        contentPanel.add(topBar, BorderLayout.NORTH);

        JPanel cardGrid = new JPanel(new GridLayout(0, 4, 15, 15));
        cardGrid.setOpaque(false);
        cardGrid.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        if (cards != null) {
            for (CardModel card : cards) {
                CardView cardView = new CardView(card);
                cardView.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (ClickType.fromMouseEvent(e) == ClickType.RIGHT && onDetail != null) {
                            onDetail.accept(card);
                        }
                    }
                });
                cardGrid.add(cardView);
            }
        }
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(cardGrid, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }
}
