package com.miozune.mediapro.deck;

import com.miozune.mediapro.card.CardBadgeView;
import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import com.miozune.mediapro.deck.events.DeckCardChangedEvent;
import com.miozune.mediapro.deck.events.DeckNameChangedEvent;
import com.miozune.mediapro.preview.Previewable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class DeckView extends JPanel implements Previewable {
    private final DeckModel model;
    private DeckModel.PropertyChangeListener propertyChangeListener;

    // UIコンポーネント
    private JLabel nameLabel;
    private JPanel cardsPanel;
    private JButton addButton;
    private JButton removeButton;
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

        addButton = new JButton("カード追加");
        removeButton = new JButton("カード削除");
        backButton = new JButton("戻る");

        Font btnFont = new Font("SansSerif", Font.BOLD, 16);
        for (JButton btn : new JButton[] { addButton, removeButton, backButton }) {
            btn.setFont(btnFont);
            btn.setFocusPainted(false);
        }
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
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBackground(new Color(45, 45, 45));
        scrollPane.getViewport().setBackground(new Color(45, 45, 45));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(50, 50, 50));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupModelListener() {
        propertyChangeListener = event -> {
            if (event instanceof DeckNameChangedEvent e) {
                updateNameDisplay(e.newName());
                return;
            }
            if (event instanceof DeckCardChangedEvent) {
                updateCardList();
            }
        };
        model.addPropertyChangeListener(propertyChangeListener);
    }

    private void updateAllDisplays() {
        updateNameDisplay(model.getName());
        updateCardList();
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
            cardsPanel.add(new CardBadgeView(card, count));
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    // getter for buttons (Controller access)
    public JButton getAddButton() {
        return addButton;
    }

    public JButton getRemoveButton() {
        return removeButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    @Override
    public String getPreviewDescription() {
        return "デッキ編集画面のプレビュー";
    }

    @Override
    public void setupPreview() {
        // プレビュー用のダミーデータ
        CardRecipeModel dummyCard1 = new CardRecipeModel("ファイアボール", 3, "fireball.png", "火の玉を投げる", CardRecipeModel.EffectType.DAMAGE, 10);
        CardRecipeModel dummyCard2 = new CardRecipeModel("ヒール", 2, "heal.png", "HPを回復する", CardRecipeModel.EffectType.HEAL, 6);
        model.addCard(dummyCard1);
        model.addCard(dummyCard1);
        model.addCard(dummyCard2);
        model.setName("プレビューデッキ");
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
