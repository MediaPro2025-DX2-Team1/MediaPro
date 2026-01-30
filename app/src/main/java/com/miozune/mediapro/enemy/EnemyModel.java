package com.miozune.mediapro.enemy;

import com.miozune.mediapro.enemy.events.EnemyHpChangedEvent;
import com.miozune.mediapro.enemy.events.EnemyNameChangedEvent;
import com.miozune.mediapro.enemy.events.EnemyPropertyChangeEvent;
import com.miozune.mediapro.enemy.events.EnemyStatusesChangedEvent;
import com.miozune.mediapro.status.StatusEffect;
import com.miozune.mediapro.status.WeaknessStatus;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Enemy の状態を管理するモデルクラス
 */
public class EnemyModel {

    @FunctionalInterface
    public interface PropertyChangeListener {
        void onPropertyChanged(EnemyPropertyChangeEvent event);
    }

    private final List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<>();

    private String name;
    private int hp;
    private int maxHp;
    private final List<StatusEffect> statusEffects = new CopyOnWriteArrayList<>();

    /**
     * コンストラクタ
     *
     * @param name 敵の名前
     * @param hp 現在のHP
     * @param maxHp 最大HP
     */
    public EnemyModel(String name, int hp, int maxHp) {
        this.name = name;
        this.hp = hp;
        this.maxHp = maxHp;
    }

    /**
     * デフォルトの Enemy を作成
     *
     * @return デフォルト値で初期化された EnemyModel
     */
    public static EnemyModel createDefault() {
        return new EnemyModel("スライム", 50, 50);
    }

    // --- Listener 管理 ---

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    private void fireEvent(EnemyPropertyChangeEvent event) {
        for (PropertyChangeListener listener : listeners) {
            listener.onPropertyChanged(event);
        }
    }

    // --- Getter / Setter ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        fireEvent(new EnemyNameChangedEvent(this, oldName, name));
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        int oldHp = this.hp;
        this.hp = Math.max(0, Math.min(hp, maxHp));
        fireEvent(new EnemyHpChangedEvent(this, oldHp, this.hp));
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = Math.max(1, maxHp);
        // 最大HPが変更された場合、現在のHPも調整
        if (this.hp > this.maxHp) {
            setHp(this.maxHp);
        }
    }

    public List<StatusEffect> getStatusEffects() {
        return List.copyOf(statusEffects);
    }

    public void addStatus(StatusEffect effect) {
        if (effect == null) {
            return;
        }
        if (effect instanceof WeaknessStatus weakness) {
            mergeWeakness(weakness);
        } else {
            statusEffects.add(effect);
        }

        fireStatusesChanged();
    }

    public void addWeakness(int turns) {
        if (turns <= 0) {
            return;
        }
        addStatus(new WeaknessStatus(turns));
    }

    private void mergeWeakness(WeaknessStatus weakness) {
        WeaknessStatus existing = null;
        for (StatusEffect status : statusEffects) {
            if (status instanceof WeaknessStatus w) {
                existing = w;
                break;
            }
        }
        if (existing != null) {
            statusEffects.remove(existing);
            int remaining = Math.max(existing.remainingTurns(), weakness.remainingTurns());
            statusEffects.add(new WeaknessStatus(existing.bonusDamage(), remaining));
        } else {
            statusEffects.add(weakness);
        }
    }

    public void onTurnStartStatuses() {
        for (StatusEffect status : statusEffects) {
            status.onTurnStart();
        }
        boolean removed = statusEffects.removeIf(StatusEffect::isExpired);
        if (removed || !statusEffects.isEmpty()) {
            fireStatusesChanged();
        }
    }

    public int applyIncomingDamageModifiers(int baseDamage) {
        int result = baseDamage;
        for (StatusEffect status : statusEffects) {
            result = status.onIncomingDamage(result);
        }
        return Math.max(0, result);
    }

    public int applyOutgoingDamageModifiers(int baseDamage) {
        int result = baseDamage;
        for (StatusEffect status : statusEffects) {
            result = status.onOutgoingDamage(result);
        }
        return Math.max(0, result);
    }

    public int receiveDamage(int rawDamage) {
        int actual = applyIncomingDamageModifiers(rawDamage);
        setHp(hp - actual);
        return actual;
    }

    // --- ユーティリティメソッド ---

    /**
     * ダメージを受ける
     *
     * @param damage ダメージ量
     */
    public void takeDamage(int damage) {
        setHp(hp - damage);
    }

    /**
     * 回復する
     *
     * @param amount 回復量
     */
    public void heal(int amount) {
        setHp(hp + amount);
    }

    /**
     * 死亡しているかどうか
     *
     * @return HP が 0 の場合 true
     */
    public boolean isDead() {
        return hp <= 0;
    }

    private void fireStatusesChanged() {
        fireEvent(new EnemyStatusesChangedEvent(this, getStatusEffects()));
    }
}
