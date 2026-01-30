package com.miozune.mediapro.decklist;

import com.miozune.mediapro.card.CardBadgeView;
import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.effect.CardAction;
import com.miozune.mediapro.effect.action.AddShieldActionEffect;
import com.miozune.mediapro.effect.action.DamageSingleEnemyActionEffect;
import com.miozune.mediapro.preview.Previewable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class DeckListView extends JPanel implements Previewable {

    private final DefaultListModel<DeckModel> deckListModel = new DefaultListModel<>();
    private final JList<DeckModel> deckList = new JList<>(deckListModel);
    private final JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));

    private final JButton addDeckButton = new JButton("デッキ作成");
    private final JButton deleteDeckButton = new JButton("デッキ削除");
    private final JButton editDeckButton = new JButton("編集");
    private final JButton backButton = new JButton("戻る");

    public DeckListView() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(25, 25, 25));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        deckList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        deckList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getName());
            label.setOpaque(true);
            label.setFont(new Font("SansSerif", Font.BOLD, 16));
            label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            label.setBackground(isSelected ? new Color(70, 130, 180) : new Color(40, 40, 40));
            label.setForeground(isSelected ? Color.WHITE : Color.LIGHT_GRAY);
            return label;
        });

        JScrollPane listScroll = new JScrollPane(deckList);
        listScroll.setPreferredSize(new Dimension(220, 0));
        listScroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        cardsPanel.setBackground(new Color(35, 35, 35));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(35, 35, 35));
        center.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        JScrollPane cardsScroll = new JScrollPane(cardsPanel);
        cardsScroll.setBorder(BorderFactory.createEmptyBorder());
        cardsScroll.getViewport().setBackground(new Color(35, 35, 35));

        center.add(cardsScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(25, 25, 25));
        for (JButton btn : new JButton[] { addDeckButton, deleteDeckButton, editDeckButton, backButton }) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setFocusPainted(false);
            buttonPanel.add(btn);
        }

        add(listScroll, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
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
                cardsPanel.add(new CardBadgeView(recipe, count));
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
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
            CardAction.of(new DamageSingleEnemyActionEffect(8)));
        fireDeck.addCard(blaze);
        fireDeck.addCard(blaze);

        DeckModel frostDeck = new DeckModel("Frost Deck");
        CardRecipeModel iceShard = new CardRecipeModel(
            "Ice Shard",
            1,
            "ice.png",
            "冷気の矢",
            CardAction.of(new DamageSingleEnemyActionEffect(4)));
        CardRecipeModel barrier = new CardRecipeModel(
            "Barrier",
            1,
            "barrier.png",
            "氷の防壁",
            CardAction.of(new AddShieldActionEffect(3)));
        frostDeck.addCard(iceShard);
        frostDeck.addCard(iceShard);
        frostDeck.addCard(barrier);

        List<DeckModel> decks = List.of(fireDeck, frostDeck);
        setDecks(decks, fireDeck);
        showDeckCards(fireDeck);
    }
}
