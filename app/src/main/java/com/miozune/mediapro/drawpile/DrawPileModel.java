package com.miozune.mediapro.drawpile;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.drawpile.events.DrawPilePropertyChangeEvent;
import com.miozune.mediapro.drawpile.events.DrawPileCardDrawnEvent;
import com.miozune.mediapro.drawpile.events.DrawPileShuffledEvent;
import com.miozune.mediapro.drawpile.events.DrawPileResetEvent;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DrawPileModel {
    private final List<CardModel> cards = new ArrayList<>();
    private final DeckModel deck;

    @FunctionalInterface
    public interface PropertyChangeListener {
        void onPropertyChanged(DrawPilePropertyChangeEvent event);
    }

    private final List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<>();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    private void fireEvent(DrawPilePropertyChangeEvent event) {
        for (PropertyChangeListener listener : listeners) {
            listener.onPropertyChanged(event);
        }
    }

    /* コンストラクタ */
    public DrawPileModel(DeckModel deck) {
        this.deck = deck;
    }

    /* Deckを参照してCardModelのlistを作成し、シャッフルする */
    public void initialize() {
        cards.clear();
        Map<CardRecipeModel, Integer> deckCards = deck.getCards();

        for (Map.Entry<CardRecipeModel, Integer> entry : deckCards.entrySet()) {
            CardRecipeModel recipe = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                cards.add(new CardModel(
                    recipe.name(),
                    recipe.cost(),
                    recipe.imageName(),
                    recipe.description()
                ));
            }
        }

        shuffle();
    }

    /* 山札をシャッフルする */
    public void shuffle() {
        Collections.shuffle(cards);
        fireEvent(new DrawPileShuffledEvent(this, cards.size()));
    }

    /* 山札からカードを1枚引く */
    public CardModel drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        CardModel card = cards.remove(cards.size() - 1);
        fireEvent(new DrawPileCardDrawnEvent(this, cards.size()));
        return card;
    }

    /* 山札から複数枚引く */
    public List<CardModel> drawCards(int count) {
        List<CardModel> drawnCards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            CardModel card = drawCard();
            if (card != null) {
                drawnCards.add(card);
            }
        }
        return drawnCards;
    }

    /* 山札をリセットする（Deckから再度初期化） */
    public void reset() {
        cards.clear();
        initialize();
        fireEvent(new DrawPileResetEvent(this, cards.size()));
    }

    /* 山札の残り枚数を取得する */
    public int getRemainingCount() {
        return cards.size();
    }

    /* 山札の全カードを取得する */
    public List<CardModel> getCards() {
        return Collections.unmodifiableList(cards);
    }

    /* 参照しているDeckを取得する */
    public DeckModel getDeck() {
        return deck;
    }

    /* 山札が5枚以下かどうかをチェック */
    public boolean isLowOnCards(int threshold) {
        return cards.size() <= threshold;
    }

    /* デフォルトインスタンスを作成（プレビュー用） */
    public static DrawPileModel createDefault() {
        DeckModel defaultDeck = new DeckModel("Default Deck");
        return new DrawPileModel(defaultDeck);
    }
}
