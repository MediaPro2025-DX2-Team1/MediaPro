package com.miozune.mediapro.decklist;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JOptionPane;

import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.game.GameModel;

public class DeckListController {

    private final GameModel gameModel;
    private final DeckListModel model;
    private final DeckListView view;
    private final AtomicInteger deckCounter = new AtomicInteger(1);

    public DeckListController(GameModel gameModel, DeckListModel model, DeckListView view) {
        this.gameModel = gameModel;
        this.model = model;
        this.view = view;

        model.addListener(this::refresh);
        wireView();
        refresh();
    }

    private void wireView() {
        view.getDeckList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                DeckModel selected = view.getDeckList().getSelectedValue();
                model.select(selected);
            }
        });

        view.getAddDeckButton().addActionListener(e -> {
            String name = JOptionPane.showInputDialog(view, "デッキ名を入力", "新規デッキ",
                    JOptionPane.PLAIN_MESSAGE);
            if (name == null || name.isBlank()) {
                name = "New Deck " + deckCounter.getAndIncrement();
            }
            DeckModel deck = model.createDeck(name.trim());
            gameModel.setActiveDeck(deck);
            refresh();
        });

        view.getDeleteDeckButton().addActionListener(e -> {
            DeckModel target = model.getSelected();
            if (target != null) {
                model.deleteSelected();
            }
        });

        view.getEditDeckButton().addActionListener(e -> {
            DeckModel target = model.getSelected();
            if (target != null) {
                gameModel.goToDeckEdit(target);
            }
        });

        view.getBackButton().addActionListener(e -> gameModel.goToWorld());
    }

    private void refresh() {
        List<DeckModel> decks = model.getDecks();
        DeckModel selected = model.getSelected();
        view.setDecks(decks, selected);
        view.showDeckCards(selected);
    }
}
