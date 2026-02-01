package com.miozune.mediapro.player;

import com.miozune.mediapro.actor.AbstractActorModel;
import com.miozune.mediapro.hand.HandModel;
import com.miozune.mediapro.player.events.PlayerHpChangedEvent;
import com.miozune.mediapro.player.events.PlayerManaChangedEvent;
import com.miozune.mediapro.player.events.PlayerNameChangedEvent;
import com.miozune.mediapro.player.events.PlayerPropertyChangeEvent;
import com.miozune.mediapro.player.events.PlayerStatusesChangedEvent;

/**
 * プレイヤーの状態を管理するModel。
 * プロパティ変更時にイベントを発火し、リスナーに通知する。
 */
public class PlayerModel extends AbstractActorModel<PlayerPropertyChangeEvent> {

    /**
     * プロパティ変更イベントを受け取るリスナーインターフェース。
     */
    @FunctionalInterface
    public interface PropertyChangeListener extends AbstractActorModel.PropertyChangeListener<PlayerPropertyChangeEvent> {}

    // --- フィールド ---

    private HandModel hand;
    private int mana;
    private int maxMana;

    // --- コンストラクタ ---

    /**
     * デフォルトコンストラクタ。
     * 初期値を設定する。
     */
    public PlayerModel(String name, int hp, int maxHp, int mana, int maxMana) {
        super(name, hp, maxHp);
        this.maxMana = Math.max(0, maxMana);
        this.mana = Math.max(0, Math.min(mana, this.maxMana));
    }

    // --- Getter/Setter ---

    public HandModel getHand() {
        return hand;
    }

    public void setHand(HandModel hand) {
        this.hand = hand;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        int oldMana = this.mana;
        this.mana = Math.max(0, Math.min(mana, maxMana));
        if (this.mana != oldMana) {
            fireEvent(new PlayerManaChangedEvent(this, oldMana, this.mana));
        }
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = Math.max(0, maxMana);
    }

    // --- ゲームロジックメソッド ---

    /**
     * マナを追加する（ターン開始時など）。
     *
     * @return 追加後のマナ値
     */
    public int addMana(int amount) {
        setMana(mana + amount);
        return mana;
    }

    /**
     * マナをリセットする（戦闘終了時など）。
     *
     * @return リセット後のマナ値（0）
     */
    public int resetMana() {
        setMana(0);
        return mana;
    }

    /**
     * マナを消費する。
     *
     * @param cost 消費マナ
     * @return 消費可能だった場合true
     */
    public boolean consumeMana(int cost) {
        if (mana >= cost) {
            setMana(mana - cost);
            return true;
        }
        return false;
    }

    /**
     * 敗北後にワールドへ戻る際のリセット。
     * HPを全快し、付与中の効果をすべて解除する。
     */
    public void resetAfterDefeat() {
        setHp(getMaxHp());
        clearStatusEffects();
    }

    public static PlayerModel createDefaultPlayer() {
        return new PlayerModel("プレイヤー", 80, 80, 3, 3);
    }

    // --- イベント生成フック ---

    @Override
    protected PlayerPropertyChangeEvent createNameChangedEvent(String oldName, String newName) {
        return new PlayerNameChangedEvent(this, oldName, newName);
    }

    @Override
    protected PlayerPropertyChangeEvent createHpChangedEvent(int oldHp, int newHp) {
        return new PlayerHpChangedEvent(this, oldHp, newHp);
    }

    @Override
    protected void fireStatusesChanged() {
        fireEvent(new PlayerStatusesChangedEvent(this, getStatusEffects()));
    }
}
