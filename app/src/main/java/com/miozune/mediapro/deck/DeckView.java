package com.miozune.mediapro.deck;

import com.miozune.mediapro.action.CardAction;
import com.miozune.mediapro.action.DamageSingleEnemyActionEffect;
import com.miozune.mediapro.action.HealSelfActionEffect;
import com.miozune.mediapro.card.CardBadgeView;
import com.miozune.mediapro.card.CardRegistry;
import com.miozune.mediapro.card.CardTargetType;
import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import com.miozune.mediapro.deck.events.DeckCardChangedEvent;
import com.miozune.mediapro.deck.events.DeckNameChangedEvent;
import com.miozune.mediapro.preview.Previewable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

public class DeckView extends JPanel implements Previewable {
    private final DeckModel model;
    private DeckModel.PropertyChangeListener propertyChangeListener;

    private Consumer<CardRecipeModel> deckCardClickHandler;
    private Consumer<CardRecipeModel> availableCardClickHandler;
    private final List<CardRecipeModel> availableCards = new ArrayList<>();

    // UIコンポーネント
    private JLabel nameLabel;
    private JPanel cardsPanel;
    private JPanel availableCardsPanel;
    private JButton backButton;

    // コンストラクタ（Previewable要件）
    public DeckView() {
        this(new DeckModel("デフォルトデッキ"));
    }

    // Modelを受け取るコンストラクタ
    public DeckView(DeckModel model) {
        this.model = model;
        setupPanel();
        initComponents();
        layoutComponents();
        setupModelListener();
        updateAllDisplays();
    }

    private void setupPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
    }

    private void initComponents() {
        nameLabel = new JLabel();
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        cardsPanel.setBackground(new Color(45, 45, 45));

        availableCardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        availableCardsPanel.setBackground(new Color(45, 45, 45));

        backButton = new JButton("戻る");

        Font btnFont = new Font("SansSerif", Font.BOLD, 16);
        backButton.setFont(btnFont);
        backButton.setFocusPainted(false);
    }

    private void layoutComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(nameLabel, BorderLayout.WEST);
        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerButtons.setOpaque(false);
        headerButtons.add(backButton);
        header.add(headerButtons, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        JScrollPane deckScrollPane = createScrollPane(cardsPanel);
        JScrollPane availableScrollPane = createScrollPane(availableCardsPanel);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(createSectionPanel("デッキ内のカード", deckScrollPane));
        center.add(Box.createVerticalStrut(16));
        center.add(createSectionPanel("所持カード一覧", availableScrollPane));

        add(center, BorderLayout.CENTER);
    }

    private JScrollPane createScrollPane(JPanel content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBackground(new Color(45, 45, 45));
        scrollPane.getViewport().setBackground(new Color(45, 45, 45));
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
            availableCardsPanel.add(createHoverableBadge(card, 1, availableCardClickHandler));
        }
        availableCardsPanel.revalidate();
        availableCardsPanel.repaint();
    }

    private CardBadgeView createHoverableBadge(CardRecipeModel card, int count, Consumer<CardRecipeModel> clickHandler) {
        CardBadgeView badge = new CardBadgeView(card, count);
        badge.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Border normalBorder = BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 2); // 厚みを保った透明枠
        Border hoverBorder = BorderFactory.createLineBorder(new Color(200, 160, 60), 2);
        badge.setBorder(normalBorder);
        badge.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                badge.setBorder(hoverBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                badge.setBorder(normalBorder);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (clickHandler != null) {
                    clickHandler.accept(card);
                }
            }
        });
        return badge;
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
            CardAction.of(new DamageSingleEnemyActionEffect(10)),
            CardTargetType.SINGLE_ENEMY);
        CardRecipeModel dummyCard2 = new CardRecipeModel(
            "ヒール",
            2,
            "heal.png",
            "HPを回復する",
            CardAction.of(new HealSelfActionEffect(6)),
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
