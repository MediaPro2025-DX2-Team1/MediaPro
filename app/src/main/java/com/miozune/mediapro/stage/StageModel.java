package com.miozune.mediapro.stage;

import com.miozune.mediapro.action.ActionContext;
import com.miozune.mediapro.actor.AbstractActorModel;
import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.discard.DiscardModel;
import com.miozune.mediapro.drawpile.DrawPileModel;
import com.miozune.mediapro.effect.EffectType;
import com.miozune.mediapro.enemy.EnemyFactory;
import com.miozune.mediapro.enemy.EnemyFactory.EnemyInstance;
import com.miozune.mediapro.enemy.EnemyModel;
import com.miozune.mediapro.enemy.EnemyType;
import com.miozune.mediapro.enemy.behavior.EnemyActionContext;
import com.miozune.mediapro.game.GameConfig;
import com.miozune.mediapro.hand.HandModel;
import com.miozune.mediapro.player.PlayerModel;
import com.miozune.mediapro.stage.events.BattleEndedEvent;
import com.miozune.mediapro.stage.events.EffectTriggeredEvent;
import com.miozune.mediapro.stage.events.EnemyAttackedPlayerEvent;
import com.miozune.mediapro.stage.events.StagePropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StageModel {
    private final String stageId;
    private final PlayerModel player;
    private final List<EnemyInstance> enemies;
    private final DrawPileModel drawpile;
    private final HandModel hand;
    private final DiscardModel discard;
    private final EnemyFactory enemyFactory;

    public enum Turn {
        PLAYER, ENEMY
    }

    private Turn turn = Turn.PLAYER;

    private boolean isBattleOver = false;// 戦闘終了フラグ

    /**
     * プロパティ変更イベントのリスナーインターフェース。
     */
    @FunctionalInterface
    public interface PropertyChangeListener {
        /**
         * プロパティが変更された際に呼び出されます。
         *
         * @param event プロパティ変更イベント
         */
        void onPropertyChanged(StagePropertyChangeEvent event);
    }

    public interface EnemyListChangeListener {
        void onEnemiesChanged(List<EnemyModel> enemies);
    }

    private final List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<>();
    private final List<EnemyListChangeListener> enemyListListeners = new CopyOnWriteArrayList<>();

    /* コンストラクタ */
    public StageModel(
            String stageId,
            PlayerModel player,
            List<EnemyInstance> enemyInstances,
            DrawPileModel drawpile,
            HandModel hand,
            DiscardModel discard,
            EnemyFactory enemyFactory) {

        this.stageId = stageId;
        this.player = player;
        this.enemies = new ArrayList<>(enemyInstances);
        this.drawpile = drawpile;
        this.hand = hand;
        this.discard = discard;
        this.enemyFactory = enemyFactory;

        notifyEnemyListChanged();
    }

    /* 戦闘開始 */
    public void startBattle() {
        if (isBattleOver) {
            return;
        }
        turn = Turn.PLAYER;
        startPlayerTurn();
    }

    /**
     * プロパティ変更リスナーを追加します。
     *
     * @param listener リスナー
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * プロパティ変更リスナーを削除します。
     *
     * @param listener リスナー
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * イベントを発火します。
     *
     * @param event イベント
     */
    private void fireEvent(StagePropertyChangeEvent event) {
        for (PropertyChangeListener listener : listeners) {
            listener.onPropertyChanged(event);
        }
    }

    /**
     * ビジュアルエフェクトをトリガーします。
     *
     * @param effectType エフェクトの種類
     * @param target エフェクトの対象（座標解決に使用）。nullの場合はデフォルト位置
     */
    public void triggerEffect(EffectType effectType, AbstractActorModel<?> target) {
        fireEvent(new EffectTriggeredEvent(this, effectType, target));
    }

    public void addEnemyListChangeListener(EnemyListChangeListener listener) {
        if (listener != null) {
            enemyListListeners.add(listener);
        }
    }

    public void removeEnemyListChangeListener(EnemyListChangeListener listener) {
        enemyListListeners.remove(listener);
    }

    // ドロー処理（1枚）
    public void drawToHand() {
        drawToHand(1);
    }

    // ドロー処理（複数枚）
    public void drawToHand(int count) {
        if (isBattleOver) {
            return;
        }
        for (int i = 0; i < count; i++) {
            var card = drawpile.drawCard();
            if (card != null) {
                hand.addCard(card);
            }
        }
        updateBattleState();
    }

    /**
     * ターン開始時専用のドロー処理（捨て札再利用あり）。
     * 山札が足りない場合、捨て札をシャッフルして山札に戻してからドローを続行します。
     *
     * @param count ドローする枚数
     */
    public void drawToHandWithRefresh(int count) {
        if (isBattleOver) {
            return;
        }

        for (int i = 0; i < count; i++) {
            // 山札が空で、捨て札にカードがある場合は再構築
            if (drawpile.getRemainingCount() == 0 && !discard.getCards().isEmpty()) {
                var discardCards = discard.removeAllCards();
                drawpile.addCardsFromDiscard(discardCards);
            }

            var card = drawpile.drawCard();
            if (card != null) {
                hand.addCard(card);
            }
        }
        updateBattleState();
    }

    /* 状態チェック：バトル終了判定 */
    private void updateBattleState() {
        if (isBattleOver)
            return; // 二重終了防止

        // プレイヤー敗北
        if (player.getHp() <= 0) {
            endBattle(false);
            return;
        }

        // 敵全滅チェック
        boolean allDead = true;
        for (EnemyInstance e : enemies) {
            if (e.model().getHp() > 0) {
                allDead = false;
                break;
            }
        }

        if (allDead) {
            endBattle(true);
        }
    }

    /**
     * モデル外部から状態が変化した際に呼び出すことで終了判定を走らせる。
     */
    public void checkBattleState() {
        updateBattleState();
    }

    /* ターン切り替え */
    public void nextTurn() {
        if (isBattleOver)
            return;

        if (turn == Turn.PLAYER) {
            endPlayerTurn();
            turn = Turn.ENEMY;
            startEnemyTurn();
            if (isBattleOver) {
                return;
            }
            turn = Turn.PLAYER;
            startPlayerTurn();
        } else {
            endEnemyTurn();
            turn = Turn.PLAYER;
            startPlayerTurn();
        }

        updateBattleState();
    }

    // 自分ターンへの移行
    private void startPlayerTurn() {
        player.onTurnStartStatuses();
        player.setMana(player.getMaxMana());

        // 手札枚数に応じたドロー枚数を計算
        int currentHandSize = hand.getCards().size();
        int drawCount;
        if (currentHandSize <= GameConfig.HAND_SIZE) {
            // 5枚以下の場合、5枚になるまでドロー
            drawCount = GameConfig.HAND_SIZE - currentHandSize;
        } else {
            // 6枚以上の場合、1枚だけドロー
            drawCount = 1;
        }

        // 捨て札再利用ありのドロー処理
        drawToHandWithRefresh(drawCount);
        // TODO: ターン開始時の効果をここに集約する
    }

    private void endPlayerTurn() {}

    // 相手ターンへの移行
    private void startEnemyTurn() {
        List<EnemyInstance> snapshot = new ArrayList<>(enemies);
        for (EnemyInstance enemyInstance : snapshot) {
            EnemyModel enemy = enemyInstance.model();
            enemy.onTurnStartStatuses();
            if (isBattleOver) {
                return;
            }
            if (enemy.isDead()) {
                continue;
            }
            enemyInstance.behavior().performTurn(new EnemyActionContext(this, enemy));
            updateBattleState();
            if (isBattleOver) {
                return;
            }
        }
    }

    private void endEnemyTurn() {

    }

    /* カード使用処理 */
    public boolean playCard(CardModel card, EnemyModel target) {
        if (card == null || isBattleOver || turn != Turn.PLAYER) {
            return false;
        }
        if (!hand.getCards().contains(card)) {
            return false;
        }

        if (!player.consumeMana(card.cost())) {
            return false;
        }

        if (card.action() == null) {
            return false;
        }

        ActionContext context = new ActionContext(this, player, getEnemies(), target, drawpile, hand, discard, card);
        card.action().apply(context);

        hand.removeCard(card);
        discard.addCard(card);
        updateBattleState();
        return true;
    }

    /* ユーティリティ */
    public EnemyModel firstAliveEnemy() {
        for (EnemyInstance enemy : enemies) {
            if (!enemy.model().isDead()) {
                return enemy.model();
            }
        }
        return null;
    }

    /* バトル終了処理 */
    private void endBattle(boolean playerWon) {
        isBattleOver = true;
        fireEvent(new BattleEndedEvent(this, playerWon));
    }

    /* 状態管理の簡単なメソッド群 */
    public PlayerModel getPlayer() {
        return player;
    }

    public List<EnemyModel> getEnemies() {
        return enemies.stream().map(EnemyInstance::model).toList();
    }

    public DrawPileModel getDrawpile() {
        return drawpile;
    }

    public HandModel getHand() {
        return hand;
    }

    public DiscardModel getDiscard() {
        return discard;
    }

    public Turn getTurn() {
        return turn;
    }

    public boolean isBattleOver() {
        return isBattleOver;
    }

    /* 敵側の行動ユーティリティ */
    public void enemyAttack(EnemyModel attacker, int baseDamage) {
        if (attacker == null || isBattleOver || attacker.isDead()) {
            return;
        }
        int actual = attacker.applyOutgoingDamageModifiers(baseDamage);
        player.receiveDamage(actual);
        fireEvent(new EnemyAttackedPlayerEvent(this, attacker, actual));
        updateBattleState();
    }

    public void enemyAttack(EnemyModel attacker, int baseDamage, int times) {
        int repeat = Math.max(1, times);
        for (int i = 0; i < repeat; i++) {
            enemyAttack(attacker, baseDamage);
            if (isBattleOver) {
                break;
            }
        }
    }

    public void summonEnemy(EnemyType type) {
        if (enemyFactory == null || type == null) {
            return;
        }
        EnemyInstance instance = enemyFactory.create(type);
        enemies.add(instance);
        notifyEnemyListChanged();
    }

    private void notifyEnemyListChanged() {
        List<EnemyModel> snapshot = getEnemies();
        for (EnemyListChangeListener enemyListener : enemyListListeners) {
            enemyListener.onEnemiesChanged(snapshot);
        }
    }

    /**
     * ステージIDを取得します。
     *
     * @return ステージID（例: "stage1", "stage2", "stage3"）
     */
    public String getStageId() {
        return stageId;
    }

    /**
     * プレビュー用のデフォルトStageModelを作成します。
     *
     * @return デフォルトのStageModel
     */
    public static StageModel createDefault() {
        PlayerModel player = PlayerModel.createDefaultPlayer();
        List<EnemyInstance> enemies = List.of(
                new EnemyInstance(EnemyModel.createDefault(), null));
        DeckModel deck = new DeckModel("Preview Deck");
        DrawPileModel drawpile = new DrawPileModel(deck);
        HandModel hand = new HandModel();
        player.setHand(hand);
        DiscardModel discard = new DiscardModel();
        EnemyFactory factory = new EnemyFactory();

        return new StageModel("stage1", player, enemies, drawpile, hand, discard, factory);
    }
}
