package com.miozune.mediapro.game;

import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.decklist.DeckListModel;
import com.miozune.mediapro.game.events.GamePropertyChangeEvent;
import com.miozune.mediapro.game.events.GameSceneChangedEvent;
import com.miozune.mediapro.player.PlayerModel;
import com.miozune.mediapro.progress.ProgressModel;
import com.miozune.mediapro.save.SaveManager;
import com.miozune.mediapro.stage.StageDefinition;
import com.miozune.mediapro.stage.StageFactory;
import com.miozune.mediapro.stage.StageModel;
import com.miozune.mediapro.stage.events.BattleEndedEvent;
import com.miozune.mediapro.world.WorldModel;
import java.io.IOException;
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
    private final ProgressModel progressModel;
    private final SaveManager saveManager;
    private GameScene scene;

    public GameModel() {
        this.stageFactory = new StageFactory();
        this.player = PlayerModel.createDefaultPlayer();
        this.progressModel = new ProgressModel();
        this.saveManager = new SaveManager();
        this.world = WorldModel.createDefault(stageFactory, progressModel);
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

    public ProgressModel getProgressModel() {
        return progressModel;
    }

    public SaveManager getSaveManager() {
        return saveManager;
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

    /**
     * ステージIDを指定してステージを開始します。
     *
     * @param stageId ステージID（例: "stage1"）
     * @return 作成されたステージ
     */
    public StageModel startStage(String stageId) {
        if (!progressModel.isUnlocked(stageId)) {
            throw new IllegalStateException("Stage is locked: " + stageId);
        }

        deckListModel.ensureActiveDeck();
        StageModel stage = world.createStageFor(player, deckListModel.getActiveDeck(), stageId);

        // バトル終了イベントをリッスンして進行状況を保存
        stage.addPropertyChangeListener(event -> {
            if (event instanceof BattleEndedEvent battleEvent) {
                if (battleEvent.playerWon()) {
                    onStageCleared(stageId);
                }
            }
        });

        setScene(GameScene.STAGE);
        return stage;
    }

    /**
     * ステージクリア時の処理。
     * 進行状況を更新し、次のステージをアンロックし、セーブファイルに保存します。
     *
     * @param stageId クリアしたステージID
     */
    private void onStageCleared(String stageId) {
        StageDefinition stageDefinition = world.getDefinitionById(stageId);

        // ステージをクリア済みとしてマーク
        progressModel.clearStage(stageId);

        // 次のステージをアンロック
        String nextStageId = stageDefinition.nextStageId();
        if (nextStageId != null) {
            progressModel.unlockStage(nextStageId);
        }

        // セーブファイルに保存
        try {
            saveManager.save(progressModel);
            System.out.println("進行状況を保存しました: " + stageDefinition.id() + " クリア");
        } catch (IOException e) {
            System.err.println("進行状況の保存に失敗しました: " + e.getMessage());
        }
    }

    /**
     * セーブファイルから進行状況をロードします。
     * ゲーム起動時に呼び出されます。
     */
    public void loadProgress() {
        saveManager.load(progressModel);
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
