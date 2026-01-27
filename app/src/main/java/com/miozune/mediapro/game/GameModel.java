package com.miozune.mediapro.game;

import com.miozune.mediapro.card.CardRegistry;
import com.miozune.mediapro.cardrecipe.CardRecipeModel;
import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.game.events.GamePropertyChangeEvent;
import com.miozune.mediapro.game.events.GameSceneChangedEvent;
import com.miozune.mediapro.player.PlayerModel;
import com.miozune.mediapro.stage.StageFactory;
import com.miozune.mediapro.stage.StageModel;
import com.miozune.mediapro.world.WorldModel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameModel {

    @FunctionalInterface
    public interface PropertyChangeListener {
        void onPropertyChanged(GamePropertyChangeEvent event);
    }

    private final List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<>();

    private final PlayerModel player;
    private final WorldModel world;
    private final StageFactory stageFactory;
    private final List<DeckModel> decks = new CopyOnWriteArrayList<>();
    private DeckModel activeDeck;
    private GameScene scene;

    public GameModel() {
        this.stageFactory = new StageFactory();
        this.player = PlayerModel.createDefaultPlayer();
        this.world = WorldModel.createDefault(stageFactory);
        this.scene = GameScene.TITLE;

        ensureActiveDeck();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    private void fireEvent(GamePropertyChangeEvent event) {
        for (PropertyChangeListener listener : listeners) {
            listener.onPropertyChanged(event);
        }
    }

    public PlayerModel getPlayer() {
        return player;
    }

    public WorldModel getWorld() {
        return world;
    }

    public DeckModel getActiveDeck() {
        return activeDeck;
    }

    public void setActiveDeck(DeckModel deck) {
        if (deck == null || deck == this.activeDeck) {
            return;
        }
        if (!decks.contains(deck)) {
            decks.add(deck);
        }
        this.activeDeck = deck;
    }

    public List<DeckModel> getDecks() {
        return List.copyOf(decks);
    }

    public GameScene getScene() {
        return scene;
    }

    public void goToTitle() {
        setScene(GameScene.TITLE);
    }

    public void goToWorld() {
        world.clearCurrentStage();
        setScene(GameScene.WORLD);
    }

    public void goToDeckList() {
        setScene(GameScene.DECK_LIST);
    }

    public void goToDeckEdit(DeckModel deck) {
        ensureActiveDeck();
        if (deck != null) {
            setActiveDeck(deck);
        }
        setScene(GameScene.DECK_EDIT);
    }

    public StageModel startStage(int stageIndex) {
        ensureActiveDeck();
        StageModel stage = world.createStageFor(player, activeDeck, stageIndex);
        stage.setBattleListener(playerWon -> goToWorld());
        setScene(GameScene.STAGE);
        return stage;
    }

    private void setScene(GameScene nextScene) {
        GameScene previous = this.scene;
        this.scene = nextScene;
        if (previous != nextScene) {
            fireEvent(new GameSceneChangedEvent(this, previous, nextScene));
        }
    }

    private void ensureActiveDeck() {
        if (activeDeck == null) {
            if (decks.isEmpty()) {
                System.out.println("デッキが存在しません。デフォルトデッキを作成します。");
                decks.add(createDefaultDeck());
            }
            activeDeck = decks.get(0);
        }
    }

    private DeckModel createDefaultDeck() {
        DeckModel deck = new DeckModel("Starter Deck");
        CardRegistry registry = CardRegistry.getInstance();
        CardRecipeModel attack = registry.find("Attack");
        CardRecipeModel guard = registry.find("Guard");
        if (attack == null) {
            attack = new CardRecipeModel("Attack", 1, "attack.jpg", "シンプルな攻撃カード。");
            registry.register(attack);
        }
        if (guard == null) {
            guard = new CardRecipeModel("Guard", 1, "guard.jpg", "防御カード。");
            registry.register(guard);
        }
        for (int i = 0; i < 6; i++) {
            deck.addCard(attack);
        }
        for (int i = 0; i < 4; i++) {
            deck.addCard(guard);
        }
        return deck;
    }

    public DeckModel createDeck(String name) {
        DeckModel deck = new DeckModel(name);
        decks.add(deck);
        return deck;
    }

    public void removeDeck(DeckModel deck) {
        if (deck == null) {
            return;
        }
        decks.remove(deck);
        if (deck == activeDeck) {
            activeDeck = decks.isEmpty() ? null : decks.get(0);
        }
    }

}
