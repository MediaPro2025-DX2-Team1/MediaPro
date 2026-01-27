package com.miozune.mediapro.game;

import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.decklist.DeckListModel;
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
    private final DeckListModel deckListModel;
    private GameScene scene;

    public GameModel() {
        this.stageFactory = new StageFactory();
        this.player = PlayerModel.createDefaultPlayer();
        this.world = WorldModel.createDefault(stageFactory);
        this.deckListModel = new DeckListModel();
        this.scene = GameScene.TITLE;
        this.deckListModel.ensureActiveDeck();
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

    public DeckListModel getDeckListModel() {
        return deckListModel;
    }

    public DeckModel getActiveDeck() {
        return deckListModel.getActiveDeck();
    }

    public void setActiveDeck(DeckModel deck) {
        deckListModel.setActiveDeck(deck);
    }

    public List<DeckModel> getDecks() {
        return deckListModel.getDecks();
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
        deckListModel.ensureActiveDeck();
        if (deck != null) {
            deckListModel.setActiveDeck(deck);
        }
        setScene(GameScene.DECK_EDIT);
    }

    public StageModel startStage(int stageIndex) {
        deckListModel.ensureActiveDeck();
        StageModel stage = world.createStageFor(player, deckListModel.getActiveDeck(), stageIndex);
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

    public DeckModel createDeck(String name) {
        return deckListModel.createDeck(name);
    }

    public void removeDeck(DeckModel deck) {
        deckListModel.removeDeck(deck);
    }

}
