package com.miozune.mediapro.deck;

import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import com.miozune.mediapro.deck.events.DeckNameChangedEvent;
import com.miozune.mediapro.deck.events.DeckCardAddedEvent;
import com.miozune.mediapro.deck.events.DeckCardRemovedEvent;
import com.miozune.mediapro.preview.Previewable;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DeckView extends JPanel implements Previewable {
    private final DeckModel model;

    // UIコンポーネント
    private JLabel nameLabel;
    private JList<String> cardList;
    private DefaultListModel<String> listModel;
    private JButton addButton;
    private JButton removeButton;

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

        listModel = new DefaultListModel<>();
        cardList = new JList<>(listModel);
        cardList.setBackground(new Color(45, 45, 45));
        cardList.setForeground(Color.WHITE);
        cardList.setFont(new Font("SansSerif", Font.PLAIN, 14));

        addButton = new JButton("カード追加");
        removeButton = new JButton("カード削除");

        Font btnFont = new Font("SansSerif", Font.BOLD, 16);
        for (JButton btn : new JButton[] { addButton, removeButton }) {
            btn.setFont(btnFont);
            btn.setFocusPainted(false);
        }
    }

    private void layoutComponents() {
        add(nameLabel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(cardList);
        scrollPane.setBackground(new Color(45, 45, 45));
        scrollPane.getViewport().setBackground(new Color(45, 45, 45));
        add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(50, 50, 50));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupModelListener() {
        model.addPropertyChangeListener(event -> {
            switch (event) {
                case DeckNameChangedEvent e -> updateNameDisplay(e.newName());
                case DeckCardAddedEvent e -> updateCardList();
                case DeckCardRemovedEvent e -> updateCardList();
            }
        });
    }

    private void updateAllDisplays() {
        updateNameDisplay(model.getName());
        updateCardList();
    }

    private void updateNameDisplay(String name) {
        nameLabel.setText("デッキ名: " + name);
    }

    private void updateCardList() {
        listModel.clear();
        List<CardRecipeModel> sortedCards = model.getSortedCards();
        for (CardRecipeModel card : sortedCards) {
            int count = model.getCount(card);
            listModel.addElement(card.name() + " (コスト: " + card.cost() + ") x" + count);
        }
    }

    // getter for buttons (Controller access)
    public JButton getAddButton() {
        return addButton;
    }

    public JButton getRemoveButton() {
        return removeButton;
    }

    @Override
    public String getPreviewDescription() {
        return "デッキ編集画面のプレビュー";
    }

    @Override
    public void setupPreview() {
        // プレビュー用のダミーデータ
        CardRecipeModel dummyCard1 = new CardRecipeModel("ファイアボール", 3, "fireball.png", "火の玉を投げる");
        CardRecipeModel dummyCard2 = new CardRecipeModel("ヒール", 2, "heal.png", "HPを回復する");
        model.addCard(dummyCard1);
        model.addCard(dummyCard1);
        model.addCard(dummyCard2);
        model.setName("プレビューデッキ");
    }

    public DeckModel getModel() {
        return model;
    }
}