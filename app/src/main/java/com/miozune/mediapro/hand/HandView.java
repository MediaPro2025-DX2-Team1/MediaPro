package com.miozune.mediapro.hand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardView;
import com.miozune.mediapro.preview.Previewable;

public class HandView extends JPanel implements Previewable {

    public interface HandActionListener {
        void onCardLeftClick(CardModel card);
        void onCardRightClick(CardModel card);
    }

    private HandActionListener actionListener;
    private final List<CardView> cardViews = new ArrayList<>();

    // StageViewの下部パネル(高さ200px)に収まるようにカードのサイズを縮小
    private static final int CARD_WIDTH = 100;  // 140 -> 100
    private static final int CARD_HEIGHT = 140; // 200 -> 140
    
    // 配置調整
    private static final int BASE_Y = 35;       
    private static final int HOVER_SHIFT = 25;  
    
    private static final int CARD_GAP = 5;      
    private static final int SIDE_MARGIN = 20;

    public HandView() {
        setOpaque(false);
        setLayout(null);
        setPreferredSize(new Dimension(800, 200));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutCards();
            }
        });
    }

    public void setHandActionListener(HandActionListener listener) {
        this.actionListener = listener;
    }

    public void updateView(List<CardModel> cards) {
        removeAll();
        cardViews.clear();

        for (CardModel cardModel : cards) {
            CardView cardView = new CardView(cardModel);
            cardView.setSize(CARD_WIDTH, CARD_HEIGHT);
            setupMouseActions(cardView, cardModel);
            add(cardView);
            cardViews.add(cardView);
        }

        layoutCards();
        repaint();
    }

    private void layoutCards() {
        if (cardViews.isEmpty()) return;

        int panelWidth = getWidth();
        if (panelWidth == 0) panelWidth = getPreferredSize().width;

        int cardCount = cardViews.size();
        int availableWidth = panelWidth - (SIDE_MARGIN * 2);
        int totalWidthNeeded = (CARD_WIDTH * cardCount) + (CARD_GAP * (cardCount - 1));

        int startX;
        int stepX;

        if (totalWidthNeeded <= availableWidth) {
            // カードが手札パネルに収まる場合
            startX = (panelWidth - totalWidthNeeded) / 2;
            stepX = CARD_WIDTH + CARD_GAP;
        } else {
            // カードが手札パネルに収まらない場合は重ねて表示する
            startX = SIDE_MARGIN;
            if (cardCount > 1) {
                stepX = (availableWidth - CARD_WIDTH) / (cardCount - 1);
            } else {
                stepX = 0;
            }
        }

        for (int i = 0; i < cardCount; i++) {
            CardView cv = cardViews.get(i);
            int x = startX + (i * stepX);
            
            cv.setLocation(x, BASE_Y);
            setComponentZOrder(cv, cardCount - 1 - i);
        }
    }

    private void setupMouseActions(CardView cardView, CardModel cardModel) {
        cardView.addMouseListener(new MouseAdapter() {
            boolean isHovering = false;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (actionListener == null) return;
                if (SwingUtilities.isLeftMouseButton(e)) {
                    actionListener.onCardLeftClick(cardModel);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    actionListener.onCardRightClick(cardModel);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isHovering) {
                    isHovering = true;
                    cardView.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    cardView.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));

                    Point p = cardView.getLocation();
                    cardView.setLocation(p.x, BASE_Y - HOVER_SHIFT);

                    setComponentZOrder(cardView, 0);
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isHovering) {
                    isHovering = false;
                    cardView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    cardView.setBorder(null);

                    layoutCards();
                    repaint();
                }
            }
        });
    }

    @Override
    public String getPreviewDescription() {
        return "手札プレビュー";
    }

    @Override
    public void setupPreview() {
        HandModel model = new HandModel();
        for (int i = 0; i < 8; i++) {
            model.addCard(CardModel.createSample());
        }
        updateView(model.getCards());
    }
}