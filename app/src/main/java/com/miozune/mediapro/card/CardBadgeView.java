package com.miozune.mediapro.card;

import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import com.miozune.mediapro.preview.Previewable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * 枚数バッジ付きのカード表示コンポーネント。
 */
public final class CardBadgeView extends JPanel implements Previewable {

    private static final Color BADGE_BACKGROUND = new Color(0, 0, 0, 200);
    private static final Color BADGE_FOREGROUND = Color.WHITE;
    private static final Font BADGE_FONT = new Font("SansSerif", Font.BOLD, 14);

    private final Dimension cardSize;
    private final JLabel badgeLabel;

    public CardBadgeView() {
        this(CardModel.createSample(), 1);
    }

    public CardBadgeView(CardRecipeModel recipe, int count) {
        this(new CardModel(recipe), count);
    }

    public CardBadgeView(CardModel cardModel, int count) {
        CardView cardView = new CardView(cardModel);
        Dimension size = cardView.getPreferredSize();
        this.cardSize = new Dimension(size);

        setLayout(new BorderLayout());
        setOpaque(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(cardSize));
        layeredPane.setMinimumSize(new Dimension(cardSize));

        cardView.setBounds(0, 0, cardSize.width, cardSize.height);
        layeredPane.add(cardView, JLayeredPane.DEFAULT_LAYER);

        badgeLabel = new JLabel();
        badgeLabel.setOpaque(true);
        badgeLabel.setBackground(BADGE_BACKGROUND);
        badgeLabel.setForeground(BADGE_FOREGROUND);
        badgeLabel.setFont(BADGE_FONT);
        badgeLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        applyCount(count);
        layeredPane.add(badgeLabel, JLayeredPane.PALETTE_LAYER);

        add(layeredPane, BorderLayout.CENTER);
    }

    public final void setCount(int count) {
        applyCount(count);
    }

    private void applyCount(int count) {
        badgeLabel.setText("x" + count);
        updateBadgeBounds();
    }

    private void updateBadgeBounds() {
        Dimension badgeSize = badgeLabel.getPreferredSize();
        badgeLabel.setBounds(cardSize.width - badgeSize.width - 6, 6, badgeSize.width, badgeSize.height);
    }

    @Override
    public String getPreviewDescription() {
        return "カードに枚数バッジを重ねるコンポーネント";
    }

    @Override
    public void setupPreview() {
        setCount(3);
    }
}
