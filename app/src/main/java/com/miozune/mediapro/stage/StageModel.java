package com.miozune.mediapro.stage;

import java.util.List;
import com.miozune.mediapro.discard.DiscardModel;
import com.miozune.mediapro.drawpile.DrawPileModel;
import com.miozune.mediapro.enemy.EnemyModel;
import com.miozune.mediapro.hand.HandModel;
import com.miozune.mediapro.player.PlayerModel;

public class StageModel {
    // importしてきたものたち
    private PlayerModel player;
    private List<EnemyModel> enemies;
    private DrawPileModel drawpile;
    private HandModel hand;
    private DiscardModel discard;

    private enum Turn {
        PLAYER, ENEMY
    }

    private Turn turn = Turn.PLAYER;

    private boolean isBattleOver = false;// 戦闘終了フラグ

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

    /* 外部から終了フラグを登録 */
    public void setBattleListener(BattleListener listener) {
        this.listener = listener;
    }

    // ドロー処理
    public void draw() {
        if (isBattleOver)
            return;

        drawpile.drawCard();
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

    /* ターン切り替え */
    public void nextTurn() {
        if (isBattleOver)
            return;

        if (turn == Turn.PLAYER) {
            endPlayerTurn();
            turn = Turn.ENEMY;
            startEnemyTurn();
        } else {
            endEnemyTurn();
            turn = Turn.PLAYER;
            startPlayerTurn();
        }

        updateBattleState();
    }

    // 自分ターンへの移行
    private void startPlayerTurn() {
        // 例：
        // マナ回復
        // カードドロー
        // ターン開始Effect通知
    }

    private void endPlayerTurn() {
        // 例：
        // 手札上限処理
        // ターン終了Effect通知
    }

    // 相手ターンへの移行
    private void startEnemyTurn() {
        // 例：
        // 敵の攻撃を呼び出す（後で）
    }

    private void endEnemyTurn() {

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

    public DrawPileModel getDeck() {
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
