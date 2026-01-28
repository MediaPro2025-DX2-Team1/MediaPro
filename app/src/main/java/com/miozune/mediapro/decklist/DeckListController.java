package com.miozune.mediapro.decklist;

import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.decklist.events.DeckListPropertyChangeEvent;
import com.miozune.mediapro.game.GameModel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JOptionPane;

public class DeckListController {

    private final GameModel gameModel;
    private final DeckListModel model;
    private final DeckListView view;
    private final AtomicInteger deckCounter = new AtomicInteger(1);
    private final Set<DeckModel> observedDecks = new HashSet<>();

    private final DeckModel.PropertyChangeListener deckChangeListener = event -> refresh();

    public DeckListController(GameModel gameModel, DeckListModel model, DeckListView view) {
        this.gameModel = gameModel;
        this.model = model;
        this.view = view;

        model.addPropertyChangeListener(this::handleModelEvent);
        wireView();
        refresh();
    }

    private void wireView() {
        view.getDeckList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                DeckModel selected = view.getDeckList().getSelectedValue();
                model.setActiveDeck(selected);
            }
        });

        view.getAddDeckButton().addActionListener(e -> {
            String name = JOptionPane.showInputDialog(view, "デッキ名を入力", "新規デッキ",
                    JOptionPane.PLAIN_MESSAGE);
            if (name == null || name.isBlank()) {
                name = "New Deck " + deckCounter.getAndIncrement();
            }
            model.createDeck(name.trim());
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

    private void handleModelEvent(DeckListPropertyChangeEvent event) {
        if (event == null) {
            return;
        }
        refresh();
    }

    private void refresh() {
        attachDeckListeners();
        List<DeckModel> decks = model.getDecks();
        DeckModel selected = model.getSelected();
        view.setDecks(decks, selected);
        view.showDeckCards(selected);
    }

    private void attachDeckListeners() {
        Set<DeckModel> toRemove = new HashSet<>(observedDecks);
        for (DeckModel deck : model.getDecks()) {
            if (observedDecks.add(deck)) {
                deck.addPropertyChangeListener(deckChangeListener);
            }
            toRemove.remove(deck);
        }

        for (DeckModel stale : toRemove) {
            stale.removePropertyChangeListener(deckChangeListener);
            observedDecks.remove(stale);
        }
    }
}
