package com.miozune.mediapro.deck;

import com.miozune.mediapro.action.DamageSingleEnemyActionEffect;
import com.miozune.mediapro.action.HealSelfActionEffect;
import com.miozune.mediapro.card.CardBadgeView;
import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardRecipeModel;
import com.miozune.mediapro.card.CardRegistry;
import com.miozune.mediapro.card.CardTargetType;
import com.miozune.mediapro.card.CardView;
import com.miozune.mediapro.card.overlay.CardDetailOverlay;
import com.miozune.mediapro.deck.events.DeckCardChangedEvent;
import com.miozune.mediapro.deck.events.DeckNameChangedEvent;
import com.miozune.mediapro.preview.Previewable;
import com.miozune.mediapro.ui.overlay.OverlayLayer;
import com.miozune.mediapro.ui.overlay.OverlayPanels;
import com.miozune.mediapro.util.ButtonStyler;
import com.miozune.mediapro.util.ImageLoader;
import com.miozune.mediapro.util.ImageUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class DeckView extends JPanel implements Previewable {
    // ボーダー定数
    private static final Border NORMAL_BORDER = BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 2);
    private static final Border HOVER_BORDER = BorderFactory.createLineBorder(new Color(200, 160, 60), 2);

    private final DeckModel model;
    private final BufferedImage backgroundImage;
    private DeckModel.PropertyChangeListener propertyChangeListener;

    private Consumer<CardRecipeModel> deckCardClickHandler;
    private Consumer<CardRecipeModel> availableCardClickHandler;
    private final List<CardRecipeModel> availableCards = new ArrayList<>();

    // UIコンポーネント
    private JLabel nameLabel;
    private JPanel cardsPanel;
    private JPanel availableCardsPanel;
    private JButton backButton;

    private final JLayeredPane layeredPane;
    private final JPanel mainContentPanel;
    private final OverlayLayer overlayLayer;

    // コンストラクタ（Previewable要件）
    public DeckView() {
        this(new DeckModel("デフォルトデッキ"));
    }

    // Modelを受け取るコンストラクタ
    public DeckView(DeckModel model) {
        this.model = model;
        this.backgroundImage = ImageLoader.loadBackgroundImage("deck.png");

        layeredPane = new JLayeredPane();
        overlayLayer = new OverlayLayer();
        mainContentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageUtils.drawBackgroundImage(g, backgroundImage, getWidth(), getHeight());
            }
        };

        setupPanel();
        initComponents();
        layoutComponents();
        setupModelListener();
        setupKeyBindings();
        updateAllDisplays();
    }

    private void setupPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        add(layeredPane, BorderLayout.CENTER);

        // OverlayLayout を使用してレイヤーを重ねる
        layeredPane.setLayout(new OverlayLayout(layeredPane));

        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // レイヤーへの追加（OverlayLayoutでは追加順序も重要だが、JLayeredPaneの機能も併用可能）
        layeredPane.add(mainContentPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(overlayLayer, JLayeredPane.PALETTE_LAYER);
    }

    private void initComponents() {
        nameLabel = new JLabel();
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        cardsPanel.setOpaque(false);
        cardsPanel.setBackground(new Color(45, 45, 45, 190));

        availableCardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        availableCardsPanel.setOpaque(false);
        availableCardsPanel.setBackground(new Color(45, 45, 45, 190));

        backButton = new JButton("戻る");

        Font btnFont = new Font("SansSerif", Font.BOLD, 16);
        backButton.setFont(btnFont);
        ButtonStyler.applyStyle(backButton);
    }

    private void layoutComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(nameLabel, BorderLayout.WEST);
        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerButtons.setOpaque(false);
        headerButtons.add(backButton);
        header.add(headerButtons, BorderLayout.EAST);
        mainContentPanel.add(header, BorderLayout.NORTH);
        JScrollPane deckScrollPane = createScrollPane(cardsPanel);
        JScrollPane availableScrollPane = createScrollPane(availableCardsPanel);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(createSectionPanel("デッキ内のカード", deckScrollPane));
        center.add(Box.createVerticalStrut(16));
        center.add(createSectionPanel("所持カード一覧", availableScrollPane));

        mainContentPanel.add(center, BorderLayout.CENTER);
    }

    private JScrollPane createScrollPane(JPanel content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(new Color(45, 45, 45, 190));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(0, 260));
        return scrollPane;
    }

    private JPanel createSectionPanel(String title, JScrollPane scrollPane) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return wrapper;
    }

    private void setupModelListener() {
        propertyChangeListener = event -> {
            if (event instanceof DeckNameChangedEvent e) {
                updateNameDisplay(e.newName());
                return;
            }
            if (event instanceof DeckCardChangedEvent) {
                updateCardList();
                updateAvailableCardList();
            }
        };
        model.addPropertyChangeListener(propertyChangeListener);
    }

    private void updateAllDisplays() {
        updateNameDisplay(model.getName());
        updateCardList();
        updateAvailableCardList();
    }

    private void updateNameDisplay(String name) {
        nameLabel.setText("デッキ名: " + name);
    }

    private void updateCardList() {
        cardsPanel.removeAll();
        List<CardRecipeModel> cards = new ArrayList<>(model.getCards().keySet());
        cards.sort(Comparator.comparingInt(CardRecipeModel::cost).thenComparing(CardRecipeModel::name));
        for (CardRecipeModel card : cards) {
            int count = model.getCount(card);
            cardsPanel.add(createHoverableBadge(card, count, deckCardClickHandler));
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private void updateAvailableCardList() {
        availableCardsPanel.removeAll();
        for (CardRecipeModel card : availableCards) {
            availableCardsPanel.add(createHoverableCard(card, availableCardClickHandler));
        }
        availableCardsPanel.revalidate();
        availableCardsPanel.repaint();
    }

    private void addHoverAndClickBehavior(JComponent component, CardRecipeModel card, Consumer<CardRecipeModel> clickHandler) {
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        component.setBorder(NORMAL_BORDER);
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                component.setBorder(HOVER_BORDER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                component.setBorder(NORMAL_BORDER);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showCardDetail(card);
                    return;
                }
                if (clickHandler != null) {
                    clickHandler.accept(card);
                }
            }
        });
    }

    public void showCardDetail(CardRecipeModel recipe) {
        CardModel cardModel = new CardModel(recipe);
        CardDetailOverlay content = new CardDetailOverlay(cardModel);
        overlayLayer.push(OverlayPanels.backdrop(content, this::hideOverlay));
    }

    public void hideOverlay() {
        overlayLayer.pop();
    }

    private CardBadgeView createHoverableBadge(CardRecipeModel card, int count, Consumer<CardRecipeModel> clickHandler) {
        CardBadgeView badge = new CardBadgeView(card, count);
        addHoverAndClickBehavior(badge, card, clickHandler);
        return badge;
    }

    private CardView createHoverableCard(CardRecipeModel card, Consumer<CardRecipeModel> clickHandler) {
        CardView cardView = new CardView(new CardModel(card));
        addHoverAndClickBehavior(cardView, card, clickHandler);
        return cardView;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public void setOnDeckCardClick(Consumer<CardRecipeModel> handler) {
        this.deckCardClickHandler = handler;
        updateCardList();
    }

    public void setOnAvailableCardClick(Consumer<CardRecipeModel> handler) {
        this.availableCardClickHandler = handler;
        updateAvailableCardList();
    }

    public void setAvailableCards(Collection<CardRecipeModel> cards) {
        availableCards.clear();
        if (cards != null) {
            availableCards.addAll(cards);
        }
        availableCards.sort(Comparator.comparingInt(CardRecipeModel::cost).thenComparing(CardRecipeModel::name));
        updateAvailableCardList();
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
        return "デッキ編集画面のプレビュー";
    }

    @Override
    public void setupPreview() {
        // プレビュー用のダミーデータ
        CardRecipeModel dummyCard1 = new CardRecipeModel(
            "ファイアボール",
            3,
            "fireball.png",
            "火の玉を投げる",
            new DamageSingleEnemyActionEffect(10),
            CardTargetType.SINGLE_ENEMY);
        CardRecipeModel dummyCard2 = new CardRecipeModel(
            "ヒール",
            2,
            "heal.png",
            "HPを回復する",
            new HealSelfActionEffect(6),
            CardTargetType.SELF);
        model.addCard(dummyCard1);
        model.addCard(dummyCard1);
        model.addCard(dummyCard2);
        model.setName("プレビューデッキ");

        setAvailableCards(CardRegistry.getInstance().listAll());
    }

    public DeckModel getModel() {
        return model;
    }

    public void dispose() {
        if (propertyChangeListener != null) {
            model.removePropertyChangeListener(propertyChangeListener);
            propertyChangeListener = null;
        }
    }
}
