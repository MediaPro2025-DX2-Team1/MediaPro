package com.miozune.mediapro.player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.miozune.mediapro.Effect.EffectModel;
import com.miozune.mediapro.hand.HandModel;

/**
 * プレイヤーの状態を管理するModel。
 * プロパティ変更時にイベントを発火し、リスナーに通知する。
 */
public class PlayerModel {

    /**
     * プロパティ変更イベントを受け取るリスナーインターフェース。
     */
    @FunctionalInterface
    public interface PropertyChangeListener {
        /**
         * プロパティが変更された時に呼び出される。
         * 
         * @param event 変更イベント（sealed interfaceによりパターンマッチング可能）
         */
        void onPropertyChanged(PlayerPropertyChangeEvent event);
    }

    // --- フィールド ---

    private String name;
    private HandModel hand;
    private int hp;
    private int maxHp;
    private int mana;
    private int maxMana;
    private final List<EffectModel> effects;

    private final List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<>();

    // --- コンストラクタ ---

    /**
     * デフォルトコンストラクタ。
     * 初期値を設定する。
     */
    public PlayerModel(String name, int hp, int maxHp, int mana, int maxMana) {
        this.name = name;
        this.maxHp = Math.max(1, maxHp);
        this.hp = Math.max(0, Math.min(hp, this.maxHp));
        this.maxMana = Math.max(0, maxMana);
        this.mana = Math.max(0, Math.min(mana, this.maxMana));
        this.effects = new ArrayList<>();
    }

    // --- リスナー管理 ---

    /**
     * プロパティ変更リスナーを追加する。
     * 
     * @param listener リスナー
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * プロパティ変更リスナーを削除する。
     * 
     * @param listener リスナー
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * イベントをすべてのリスナーに通知する。
     * 
     * @param event 発火するイベント
     */
    private void fireEvent(PlayerPropertyChangeEvent event) {
        for (PropertyChangeListener listener : listeners) {
            listener.onPropertyChanged(event);
        }
    }

    // --- Getter/Setter ---
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        fireEvent(new PlayerNameChangedEvent(this, oldName, name));
    }

    public HandModel getHand() {
        return hand;
    }

    public void setHand(HandModel hand) {
        this.hand = hand;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        int oldHp = this.hp;
        this.hp = Math.max(0, Math.min(hp, maxHp)); // 0〜maxHpにクランプ
        if (this.hp != oldHp) {
            fireEvent(new PlayerHpChangedEvent(this, oldHp, this.hp));
        }
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = Math.max(1, maxHp);
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        int oldMana = this.mana;
        this.mana = Math.max(0, Math.min(mana, maxMana)); // 0〜maxManaにクランプ
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

    public List<EffectModel> getEffects() {
        return effects;
    }

    // --- ゲームロジックメソッド ---

    /**
     * マナを追加する（ターン開始時など）。
     * 
     * @return 追加後のマナ値
     */
    public int addMana() {
        setMana(mana + 1);
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
     * ダメージを受ける。
     * 
     * @param damage ダメージ量
     * @return 残りHP
     */
    public int takeDamage(int damage) {
        setHp(hp - damage);
        return hp;
    }

    /**
     * 回復する。
     * 
     * @param amount 回復量
     * @return 回復後のHP
     */
    public int heal(int amount) {
        setHp(hp + amount);
        return hp;
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
     * プレイヤーが生存しているか判定する。
     * 
     * @return HP > 0の場合true
     */
    public boolean isAlive() {
        return hp > 0;
    }

    public static PlayerModel createDefaultPlayer() {
        return new PlayerModel("プレイヤー", 100, 100, 0, 10);
    }
}
