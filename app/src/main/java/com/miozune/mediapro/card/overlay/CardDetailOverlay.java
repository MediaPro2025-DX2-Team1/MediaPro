package com.miozune.mediapro.card.overlay;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardView;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class CardDetailOverlay extends JPanel {

    public CardDetailOverlay(CardModel card) {
        setOpaque(false);
        setLayout(new GridBagLayout());

        CardView bigCardView = new CardView(card);
        bigCardView.setPreferredSize(new Dimension(300, 420));
        bigCardView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });
        add(bigCardView);
    }
}
