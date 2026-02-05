package com.miozune.mediapro.drawpile;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardRecipeModel;
import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.drawpile.events.DrawPileCardDrawnEvent;
import com.miozune.mediapro.drawpile.events.DrawPilePropertyChangeEvent;
import com.miozune.mediapro.drawpile.events.DrawPileRefreshedFromDiscardEvent;
import com.miozune.mediapro.drawpile.events.DrawPileShuffledEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class DrawPileModel {
    private final List<CardModel> cards = new ArrayList<>();

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
        rebuildFromDeck(deck);
    }

    /* Deckを参照してCardModelのlistを作成し、シャッフルする */
    private void rebuildFromDeck(DeckModel deck) {
        cards.clear();
        Map<CardRecipeModel, Integer> deckCards = deck.getCards();

        for (Map.Entry<CardRecipeModel, Integer> entry : deckCards.entrySet()) {
            CardRecipeModel recipe = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                cards.add(new CardModel(recipe));
            }
        }

        shuffle();
    }

    /* 山札をシャッフルする */
    public void shuffle() {
        Collections.shuffle(cards);
        fireEvent(new DrawPileShuffledEvent(this, cards.size()));
    }

    /**
     * 捨て札から山札を再構築します。
     * 捨て札のカードを山札に追加し、シャッフルして、イベントを発行します。
     *
     * @param discardCards 捨て札から取り出されたカードのリスト
     */
    public void addCardsFromDiscard(List<CardModel> discardCards) {
        if (discardCards == null || discardCards.isEmpty()) {
            return;
        }
        cards.addAll(discardCards);
        shuffle();
        fireEvent(new DrawPileRefreshedFromDiscardEvent(this, cards.size()));
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

    /* 山札の残り枚数を取得する */
    public int getRemainingCount() {
        return cards.size();
    }

    /* 山札の全カードを取得する */
    public List<CardModel> getCards() {
        return Collections.unmodifiableList(cards);
    }

    /* デフォルトインスタンスを作成（プレビュー用） */
    public static DrawPileModel createDefault() {
        DeckModel defaultDeck = new DeckModel("Default Deck");
        return new DrawPileModel(defaultDeck);
    }
}
