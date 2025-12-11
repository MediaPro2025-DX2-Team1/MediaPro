package com.miozune.mediapro.battlescreen;

import com.miozune.mediapro.model.*;
import java.util.List;

public class BattleScreen {

    private Player player;
    private List<Enemy> enemy;

    private Deck deck;
    private Hand hand;
    private Discard discard;

    private Mana mana;
    private Turn turn;
    private Stage stage;

    private boolean isBattleOver = false;// ターン終了フラグ
    private boolean isPlayerWin = false;// 戦闘の終了フラグ

    public interface BattleListener { // 終了結果を外へ通知するためのリスナー
        void onBattleEnd(boolean playerWon);
    }

    private BattleListener listener;

    /* コンストラクタ */
    public BattleScreen(Player player,
            List<Enemy> enemy,
            Deck deck,
            Hand hand,
            Discard discard,
            Mana mana,
            Turn turn,
            Stage stage) {

        this.player = player;
        this.enemy = enemy;

        this.deck = deck;
        this.hand = hand;
        this.discard = discard;

        this.mana = mana;
        this.turn = turn;
        this.stage = stage;
    }

    /* 外部から終了フラグを登録 */
    public void setBattleListener(BattleListener listener) {
        this.listener = listener;
    }

    /* 状態チェック：バトル終了判定 */
    public void checkBattleEnd() {
        if (isBattleOver)
            return; // 二重終了防止

        // プレイヤー死亡判定
        if (player.getHp() <= 0) {
            endBattle(false);
            return;
        }

        // 敵全滅判定
        boolean allDead = true;
        for (Enemy e : enemy) {
            if (e.getHp() > 0) {
                allDead = false;
                break;
            }
        }

        if (allDead) {
            endBattle(true);
        }
    }

    /* バトル終了処理 */
    private void endBattle(boolean playerWon) {
        this.isBattleOver = true;
        this.isPlayerWin = playerWon;

        if (listener != null) {
            listener.onBattleEnd(playerWon);
        }
    }

    /* 状態管理の簡単なメソッド群 */
    public Player getPlayer() {
        return player;
    }

    public List<Enemy> getEnemy() {
        return enemy;
    }

    public Deck getDeck() {
        return deck;
    }

    public Hand getHand() {
        return hand;
    }

    public Discard getDiscard() {
        return discard;
    }

    public Mana getMana() {
        return mana;
    }

    public Turn getTurn() {
        return turn;
    }

    public Stage getStage() {
        return stage;
    }

    public boolean isBattleOver() {
        return isBattleOver;
    }

    public boolean isPlayerWin() {
        return isPlayerWin;
    }
}