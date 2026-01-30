package com.miozune.mediapro.stage;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardView;
import com.miozune.mediapro.card.events.CardClickListener;
import com.miozune.mediapro.card.events.ClickType;
import com.miozune.mediapro.enemy.EnemyModel;
import com.miozune.mediapro.enemy.EnemyView;
import com.miozune.mediapro.hand.HandView;
import com.miozune.mediapro.player.PlayerModel;
import com.miozune.mediapro.player.PlayerView;
import com.miozune.mediapro.player.events.PlayerManaChangedEvent;
import com.miozune.mediapro.preview.Previewable;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class StageView extends JPanel implements Previewable {

    private static final int HAND_MIN_HEIGHT = CardView.DEFAULT_HEIGHT + 20;

    private final JPanel topPanel;
    private final JPanel bottomPanel;

    private final HandView handView;
    private final JPanel handWrapper;
    private final JPanel actionPanel;
    private final JPanel handContainer;
    private final JPanel manaWrapper;
    private final ManaBadge manaBadge;

    private final JButton deckButton;
    private final JButton discardButton;
    private final JButton endTurnButton;

    private PlayerView playerView;
    private EnemyView enemyView;
    private PlayerModel playerModel;
    private PlayerModel.PropertyChangeListener playerListener;

    public StageView() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 700));
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        /* --- 上部：戦闘画面エリア --- */
        topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        /* --- 下部：操作エリア --- */
        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        // 左側：手札
        handView = new HandView();
        handWrapper = new JPanel(new BorderLayout());
        handWrapper.setBackground(new Color(45, 45, 45));
        handWrapper.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        handWrapper.setOpaque(true);
        handWrapper.setMinimumSize(new Dimension(0, HAND_MIN_HEIGHT));
        handWrapper.setPreferredSize(new Dimension(0, HAND_MIN_HEIGHT));
        handWrapper.add(handView, BorderLayout.CENTER);
        manaBadge = new ManaBadge();
        manaWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        manaWrapper.setOpaque(false);
        manaWrapper.add(manaBadge);

        // 右側：アクションボタン（3分割して埋める）
        actionPanel = new JPanel(new GridLayout(3, 1, 0, 5)); // 縦に3つ並べる
        actionPanel.setPreferredSize(new Dimension(200, 0));
        actionPanel.setBackground(new Color(50, 50, 50));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        deckButton = new JButton("山札確認");
        discardButton = new JButton("捨札確認");
        endTurnButton = new JButton("ターン終了");

        // ボタンのフォントと見た目の微調整
        Font btnFont = new Font("SansSerif", Font.BOLD, 16);
        for (JButton btn : new JButton[] { deckButton, discardButton, endTurnButton }) {
            btn.setFont(btnFont);
            btn.setFocusPainted(false);
            actionPanel.add(btn);
        }

        handContainer = new JPanel(new BorderLayout());
        handContainer.setOpaque(false);
        handContainer.add(manaWrapper, BorderLayout.NORTH);
        handContainer.add(handWrapper, BorderLayout.CENTER);

        bottomPanel.add(handContainer, BorderLayout.CENTER);
        bottomPanel.add(actionPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }


    public void setActors(PlayerModel player, EnemyModel enemy) {
        if (playerListener != null && playerModel != null) {
            playerModel.removePropertyChangeListener(playerListener);
        }
        playerModel = player;
        topPanel.removeAll();
        if (player == null) {
            throw new IllegalArgumentException("PlayerModel cannot be null");
        }
        playerView = new PlayerView(player);
        playerView.setAlignmentY(Component.TOP_ALIGNMENT);
        addActorView(playerView);
        setupPlayerListener();
        updateManaDisplay(player.getMana());

        if (enemy != null) {
            enemyView = new EnemyView(enemy);
            topPanel.add(Box.createHorizontalStrut(30));
            enemyView.setAlignmentY(Component.TOP_ALIGNMENT);
            addActorView(enemyView);
        }
        topPanel.revalidate();
        topPanel.repaint();
    }

    private void addActorView(JComponent view) {
        topPanel.add(view);
    }

    private void setupPlayerListener() {
        if (playerModel == null) {
            return;
        }
        playerListener = event -> {
            if (event instanceof PlayerManaChangedEvent manaEvent) {
                updateManaDisplay(manaEvent.newMana());
            }
        };
        playerModel.addPropertyChangeListener(playerListener);
    }

    private void updateManaDisplay(int mana) {
        if (playerModel == null) {
            return;
        }
        int maxMana = playerModel.getMaxMana();
        manaBadge.setValues(mana, maxMana);
    }

    private static class ManaBadge extends JComponent {
        private int mana;
        private int maxMana = 1;

        private static final int SIZE = 64;
        private static final Color FILL = new Color(50, 150, 220);
        private static final Color BORDER = new Color(20, 70, 140);
        private static final Color TEXT = Color.WHITE;

        ManaBadge() {
            setPreferredSize(new Dimension(SIZE, SIZE));
            setMinimumSize(new Dimension(SIZE, SIZE));
            setMaximumSize(new Dimension(SIZE, SIZE));
            setOpaque(false);
        }

        void setValues(int mana, int maxMana) {
            this.mana = mana;
            this.maxMana = Math.max(1, maxMana);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(FILL);
            g2.fillOval(x, y, size, size);

            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x, y, size, size);

            String text = String.format("%d/%d", mana, maxMana);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int tx = getWidth() / 2 - textWidth / 2;
            int ty = getHeight() / 2 + textHeight / 2 - 4;
            g2.setColor(TEXT);
            g2.drawString(text, tx, ty);

            g2.dispose();
        }
    }

    public void updateHand(List<CardModel> cards, CardClickListener clickListener) {
        CardClickListener bridgeListener = null;
        if (clickListener != null) {
            bridgeListener = event -> {
                if (event.clickType() == ClickType.RIGHT) {
                    handView.showCardDetail(event.card());
                    return;
                }
                clickListener.onCardClicked(event);
            };
        }
        handView.setCardClickListener(bridgeListener);
        handView.updateHand(cards);
    }

    /* Previewableインターフェースの実装 */
    @Override
    public String getPreviewDescription() {
        return "戦闘画面のテストビューです。";
    }

    @Override
    public void setupPreview() {
        PlayerModel player = PlayerModel.createDefaultPlayer();
        player.setHp(80);
        player.setMana(3);
        EnemyModel enemy = EnemyModel.createDefault();
        enemy.setHp(50);
        setActors(player, enemy);
    }

    // Getter
    public JButton getDeckButton() {
        return deckButton;
    }

    public JButton getDiscardButton() {
        return discardButton;
    }

    public JButton getEndTurnButton() {
        return endTurnButton;
    }
}
