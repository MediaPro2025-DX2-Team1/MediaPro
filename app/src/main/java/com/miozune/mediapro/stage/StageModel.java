package com.miozune.mediapro.stage;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.discard.DiscardModel;
import com.miozune.mediapro.drawpile.DrawPileModel;
import com.miozune.mediapro.effect.action.ActionContext;
import com.miozune.mediapro.enemy.EnemyModel;
import com.miozune.mediapro.hand.HandModel;
import com.miozune.mediapro.player.PlayerModel;
import java.util.List;

public class StageModel {
    private final PlayerModel player;
    private final List<EnemyModel> enemies;
    private final DrawPileModel drawpile;
    private final HandModel hand;
    private final DiscardModel discard;

    public enum Turn {
        PLAYER, ENEMY
    }

    private Turn turn = Turn.PLAYER;

    private boolean isBattleOver = false;// 戦闘終了フラグ

    private static final int ENEMY_BASE_DAMAGE = 5;

    public interface BattleListener { // 終了結果を外へ通知するためのリスナー
        void onBattleEnd(boolean playerWon);
    }

    private BattleListener listener;

    /* コンストラクタ */
    public StageModel(
            PlayerModel player,
            List<EnemyModel> enemies,
            DrawPileModel drawpile,
            HandModel hand,
            DiscardModel discard) {

        this.player = player;
        this.enemies = enemies;
        this.drawpile = drawpile;
        this.hand = hand;
        this.discard = discard;
    }

    /* 戦闘開始 */
    public void startBattle() {
        if (isBattleOver) {
            return;
        }
        turn = Turn.PLAYER;
        startPlayerTurn();
    }

    /* 外部から終了フラグを登録 */
    public void setBattleListener(BattleListener listener) {
        this.listener = listener;
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
        for (EnemyModel e : enemies) {
            if (e.getHp() > 0) {
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
        player.addMana();
        drawToHand(1);
        // TODO: ターン開始時の効果をここに集約する
    }

    private void endPlayerTurn() {
        // 2026-01 現仕様: ターン終了時に手札は捨て札へ送らない
    }

    // 相手ターンへの移行
    private void startEnemyTurn() {
        for (EnemyModel enemy : enemies) {
            enemy.onTurnStartStatuses();
            if (isBattleOver) {
                return;
            }
            if (enemy.isDead()) {
                continue;
            }
            player.receiveDamage(ENEMY_BASE_DAMAGE);
            updateBattleState();
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

        ActionContext context = new ActionContext(this, player, enemies, target, drawpile, hand, discard);
        card.action().execute(context);

        hand.removeCard(card);
        discard.addCard(card);
        updateBattleState();
        return true;
    }

    /* ユーティリティ */
    public EnemyModel firstAliveEnemy() {
        for (EnemyModel enemy : enemies) {
            if (!enemy.isDead()) {
                return enemy;
            }
        }
        return null;
    }

    /* バトル終了処理 */
    private void endBattle(boolean playerWon) {
        isBattleOver = true;

        if (listener != null) {
            listener.onBattleEnd(playerWon);
        }
    }

    /* 状態管理の簡単なメソッド群 */
    public PlayerModel getPlayer() {
        return player;
    }

    public List<EnemyModel> getEnemies() {
        return enemies;
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
}
