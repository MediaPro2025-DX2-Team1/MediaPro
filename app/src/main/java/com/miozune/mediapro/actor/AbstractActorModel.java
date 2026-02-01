package com.miozune.mediapro.actor;

import com.miozune.mediapro.status.ShieldStatus;
import com.miozune.mediapro.status.StatusEffect;
import com.miozune.mediapro.status.StrengthStatus;
import com.miozune.mediapro.status.WeaknessStatus;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 名前・HP・ステータス管理を共通化した抽象モデル。
 * イベント型はサブクラスごとのsealedインターフェースに合わせてジェネリクスで指定する。
 */
public abstract class AbstractActorModel<E> {

    @FunctionalInterface
    public interface PropertyChangeListener<E> {
        void onPropertyChanged(E event);
    }

    private final List<PropertyChangeListener<E>> listeners = new CopyOnWriteArrayList<>();
    private final List<StatusEffect> statusEffects = new CopyOnWriteArrayList<>();

    private String name;
    private int hp;
    private int maxHp;

    protected AbstractActorModel(String name, int hp, int maxHp) {
        this.name = name;
        this.maxHp = Math.max(1, maxHp);
        this.hp = clampHp(hp, this.maxHp);
    }

    // --- Listener 管理 ---

    public void addPropertyChangeListener(PropertyChangeListener<E> listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener<E> listener) {
        listeners.remove(listener);
    }

    protected void fireEvent(E event) {
        if (event == null) {
            return;
        }
        for (PropertyChangeListener<E> listener : listeners) {
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
        if (!Objects.equals(oldName, name)) {
            fireEvent(createNameChangedEvent(oldName, name));
        }
    }

    public int getHp() {
        return hp;
    }

    /**
     * 直接HPを設定する。
     * ダメージ計算や回復処理には receiveDamage / heal メソッドを使用すること。
     */
    public void setHp(int hp) {
        int clamped = clampHp(hp, maxHp);
        if (clamped == this.hp) {
            return;
        }
        int oldHp = this.hp;
        this.hp = clamped;
        fireEvent(createHpChangedEvent(oldHp, clamped));
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = Math.max(1, maxHp);
        setHp(hp);
    }

    protected static int clampHp(int value, int limit) {
        return Math.max(0, Math.min(value, limit));
    }

    // --- ステータス管理 ---

    public List<StatusEffect> getStatusEffects() {
        return List.copyOf(statusEffects);
    }

    public void clearStatusEffects() {
        if (statusEffects.isEmpty()) {
            return;
        }
        statusEffects.clear();
        fireStatusesChanged();
    }

    public void addStatus(StatusEffect effect) {
        if (effect == null) {
            return;
        }
        if (!mergeStatus(effect)) {
            statusEffects.add(effect);
        }
        fireStatusesChanged();
    }

    /**
     * 追加するステータスのスタック・統合処理。
     * 統合を行った場合はtrueを返し、呼び出し元で追加を抑制する。
     */
    protected boolean mergeStatus(StatusEffect effect) {
        return switch (effect) {
            case ShieldStatus shield -> {
                mergeShield(shield);
                yield true;
            }
            case StrengthStatus strength -> {
                mergeStrength(strength);
                yield true;
            }
            case WeaknessStatus weakness -> {
                mergeWeakness(weakness);
                yield true;
            }
            default -> false;
        };
    }

    public void clearExpiredStatuses() {
        boolean removed = statusEffects.removeIf(StatusEffect::isExpired);
        if (removed) {
            fireStatusesChanged();
        }
    }

    public void addShield(int amount) {
        addShield(amount, false);
    }

    public void addShield(int amount, boolean isPermanent) {
        if (amount <= 0) {
            return;
        }
        addStatus(new ShieldStatus(amount, isPermanent));
    }

    public void addStrength(int bonus) {
        if (bonus <= 0) {
            return;
        }
        addStatus(new StrengthStatus(bonus));
    }

    public void addWeakness(int turns) {
        if (turns <= 0) {
            return;
        }
        addStatus(new WeaknessStatus(turns));
    }

    protected void mergeWeakness(WeaknessStatus weakness) {
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

    protected void mergeShield(ShieldStatus shield) {
        ShieldStatus existing = null;
        for (StatusEffect status : statusEffects) {
            if (status instanceof ShieldStatus s) {
                existing = s;
                break;
            }
        }
        if (existing != null) {
            // 永続エフェクトと一時的エフェクトは別々に管理
            if (existing.isPermanent() == shield.isPermanent()) {
                existing.addShield(shield.amount());
            } else {
                // 永続と一時が混在する場合は追加
                statusEffects.add(shield);
            }
        } else {
            statusEffects.add(shield);
        }
    }

    protected void mergeStrength(StrengthStatus strength) {
        StrengthStatus existing = null;
        for (StatusEffect status : statusEffects) {
            if (status instanceof StrengthStatus s) {
                existing = s;
                break;
            }
        }
        if (existing != null) {
            existing.stack(strength);
        } else {
            statusEffects.add(strength);
        }
    }

    public void onTurnStartStatuses() {
        for (StatusEffect status : statusEffects) {
            status.onTurnStart();
        }
        statusEffects.removeIf(StatusEffect::isExpired);
        fireStatusesChanged();
    }

    public int applyIncomingDamageModifiers(int baseDamage) {
        int result = baseDamage;
        for (StatusEffect status : statusEffects) {
            result = status.onIncomingDamage(result);
        }
        fireStatusesChanged();
        return Math.max(0, result);
    }

    public int applyOutgoingDamageModifiers(int baseDamage) {
        int result = baseDamage;
        for (StatusEffect status : statusEffects) {
            result = status.onOutgoingDamage(result);
        }
        fireStatusesChanged();
        return Math.max(0, result);
    }

    // --- ダメージ計算 ---

    public void receiveDamage(int rawDamage) {
        int actual = applyIncomingDamageModifiers(rawDamage);
        setHp(hp - actual);
    }

    public void heal(int amount) {
        setHp(getHp() + amount);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public boolean isDead() {
        return !isAlive();
    }

    // --- イベント生成フック ---

    protected abstract E createNameChangedEvent(String oldName, String newName);

    protected abstract E createHpChangedEvent(int oldHp, int newHp);

    protected abstract void fireStatusesChanged();
}
