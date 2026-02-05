package com.miozune.mediapro.decklist;

import com.miozune.mediapro.action.AddShieldActionEffect;
import com.miozune.mediapro.action.DamageSingleEnemyActionEffect;
import com.miozune.mediapro.card.CardBadgeView;
import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardRecipeModel;
import com.miozune.mediapro.card.CardTargetType;
import com.miozune.mediapro.card.overlay.CardDetailOverlay;
import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.preview.Previewable;
import com.miozune.mediapro.ui.overlay.OverlayLayer;
import com.miozune.mediapro.ui.overlay.OverlayPanels;
import com.miozune.mediapro.util.ButtonStyler;
import com.miozune.mediapro.util.ImageLoader;
import com.miozune.mediapro.util.ImageUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class DeckListView extends JPanel implements Previewable {

    private final BufferedImage backgroundImage;
    private final DefaultListModel<DeckModel> deckListModel = new DefaultListModel<>();
    private final JList<DeckModel> deckList = new JList<>(deckListModel);
    private final JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12)) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
            super.paintComponent(g);
        }
    };

    private final JButton addDeckButton = new JButton("デッキ作成");
    private final JButton deleteDeckButton = new JButton("デッキ削除");
    private final JButton editDeckButton = new JButton("編集");
    private final JButton backButton = new JButton("戻る");

    private final JLayeredPane layeredPane;
    private final JPanel mainContentPanel;
    private final OverlayLayer overlayLayer;

    public DeckListView() {
        this.backgroundImage = ImageLoader.loadBackgroundImage("deck.png");

        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 25));

        layeredPane = new JLayeredPane();
        add(layeredPane, BorderLayout.CENTER);

        mainContentPanel = new JPanel(new BorderLayout(12, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageUtils.drawBackgroundImage(g, backgroundImage, getWidth(), getHeight());
            }
        };
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        overlayLayer = new OverlayLayer();

        layeredPane.add(mainContentPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(overlayLayer, JLayeredPane.PALETTE_LAYER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                layeredPane.setBounds(0, 0, w, h);
                mainContentPanel.setBounds(0, 0, w, h);
                overlayLayer.setBounds(0, 0, w, h);
            }
        });

        deckList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        deckList.setOpaque(false);
        deckList.setBackground(new Color(40, 40, 40, 190));
        deckList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getName()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };
            label.setOpaque(false);
            label.setFont(new Font("SansSerif", Font.BOLD, 16));
            label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            label.setBackground(isSelected ? new Color(70, 130, 180, 190) : new Color(40, 40, 40, 190));
            label.setForeground(isSelected ? Color.WHITE : Color.LIGHT_GRAY);
            return label;
        });

        JScrollPane listScroll = new JScrollPane(deckList) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        listScroll.setOpaque(false);
        listScroll.setBackground(new Color(40, 40, 40, 190));
        listScroll.getViewport().setOpaque(false);
        listScroll.getViewport().setBackground(new Color(40, 40, 40, 190));
        listScroll.setPreferredSize(new Dimension(220, 0));
        listScroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        cardsPanel.setOpaque(false);
        cardsPanel.setBackground(new Color(35, 35, 35, 190));

        JPanel center = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        center.setOpaque(false);
        center.setBackground(new Color(35, 35, 35, 190));
        center.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        JScrollPane cardsScroll = new JScrollPane(cardsPanel);
        cardsScroll.setOpaque(false);
        cardsScroll.setBorder(BorderFactory.createEmptyBorder());
        cardsScroll.getViewport().setOpaque(false);
        cardsScroll.getViewport().setBackground(new Color(35, 35, 35, 190));

        center.add(cardsScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        buttonPanel.setOpaque(false);
        buttonPanel.setBackground(new Color(25, 25, 25, 190));
        for (JButton btn : new JButton[] { addDeckButton, deleteDeckButton, editDeckButton, backButton }) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            ButtonStyler.applyStyle(btn);
            buttonPanel.add(btn);
        }

        mainContentPanel.add(listScroll, BorderLayout.WEST);
        mainContentPanel.add(center, BorderLayout.CENTER);
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setupKeyBindings();
    }

    public void setDecks(List<DeckModel> decks, DeckModel selected) {
        deckListModel.clear();
        for (DeckModel deck : decks) {
            deckListModel.addElement(deck);
        }
        deckList.setSelectedValue(selected, true);
    }

    public void showDeckCards(DeckModel deck) {
        cardsPanel.removeAll();
        if (deck != null) {
            List<CardRecipeModel> recipes = new ArrayList<>(deck.getCards().keySet());
            recipes.sort(Comparator.comparingInt(CardRecipeModel::cost)
                    .thenComparing(CardRecipeModel::name));
            for (CardRecipeModel recipe : recipes) {
                int count = deck.getCount(recipe);
                CardBadgeView badge = new CardBadgeView(recipe, count);
                badge.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            showCardDetail(recipe);
                        }
                    }
                });
                cardsPanel.add(badge);
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    public void showCardDetail(CardRecipeModel recipe) {
        CardModel cardModel = new CardModel(recipe);
        CardDetailOverlay content = new CardDetailOverlay(cardModel);
        overlayLayer.push(OverlayPanels.backdrop(content, this::hideOverlay));
    }

    public void hideOverlay() {
        overlayLayer.pop();
    }

    public JList<DeckModel> getDeckList() {
        return deckList;
    }

    public JButton getAddDeckButton() {
        return addDeckButton;
    }

    public JButton getDeleteDeckButton() {
        return deleteDeckButton;
    }

    public JButton getEditDeckButton() {
        return editDeckButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    private void setupKeyBindings() {
        setFocusable(true);
        ActionMap actionMap = getActionMap();
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancelAction");
        actionMap.put("cancelAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (overlayLayer.isVisible() && overlayLayer.isTopCloseableByEsc()) {
                    hideOverlay();
                }
            }
        });
    }

    @Override
    public String getPreviewDescription() {
        return "デッキ一覧画面";
    }

    @Override
    public void setupPreview() {
        DeckModel fireDeck = new DeckModel("Fire Deck");
        CardRecipeModel blaze = new CardRecipeModel(
            "Blaze",
            2,
            "blaze.png",
            "炎の一撃",
            new DamageSingleEnemyActionEffect(8),
            CardTargetType.SINGLE_ENEMY);
        fireDeck.addCard(blaze);
        fireDeck.addCard(blaze);

        DeckModel frostDeck = new DeckModel("Frost Deck");
        CardRecipeModel iceShard = new CardRecipeModel(
            "Ice Shard",
            1,
            "ice.png",
            "冷気の矢",
            new DamageSingleEnemyActionEffect(4),
            CardTargetType.SINGLE_ENEMY);
        CardRecipeModel barrier = new CardRecipeModel(
            "Barrier",
            1,
            "barrier.png",
            "氷の防壁",
            new AddShieldActionEffect(3),
            CardTargetType.SELF);
        frostDeck.addCard(iceShard);
        frostDeck.addCard(iceShard);
        frostDeck.addCard(barrier);

        List<DeckModel> decks = List.of(fireDeck, frostDeck);
        setDecks(decks, fireDeck);
        showDeckCards(fireDeck);
    }
}
