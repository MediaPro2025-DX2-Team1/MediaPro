package com.miozune.mediapro.enemy;

import com.miozune.mediapro.actor.AbstractActorModel;
import com.miozune.mediapro.enemy.events.EnemyHpChangedEvent;
import com.miozune.mediapro.enemy.events.EnemyNameChangedEvent;
import com.miozune.mediapro.enemy.events.EnemyPropertyChangeEvent;
import com.miozune.mediapro.enemy.events.EnemyStatusesChangedEvent;

/**
 * Enemy の状態を管理するモデルクラス
 */
public class EnemyModel extends AbstractActorModel<EnemyPropertyChangeEvent> {

    @FunctionalInterface
    public interface PropertyChangeListener extends AbstractActorModel.PropertyChangeListener<EnemyPropertyChangeEvent> {}

    private final EnemyType enemyType;

    /**
     * コンストラクタ
     *
     * @param enemyType 敵の種類
     * @param name 敵の名前
     * @param hp 現在のHP
     * @param maxHp 最大HP
     */
    public EnemyModel(EnemyType enemyType, String name, int hp, int maxHp) {
        super(name, hp, maxHp);
        this.enemyType = enemyType;
    }

    /**
     * デフォルトの Enemy を作成
     *
     * @return デフォルト値で初期化された EnemyModel
     */
    public static EnemyModel createDefault() {
        return new EnemyModel(EnemyType.SLIME, "スライム", 50, 50);
    }

    /**
     * 敵の種類を取得する。
     *
     * @return 敵の種類
     */
    public EnemyType getEnemyType() {
        return enemyType;
    }

    // --- イベント生成フック ---

    @Override
    protected EnemyPropertyChangeEvent createNameChangedEvent(String oldName, String newName) {
        return new EnemyNameChangedEvent(this, oldName, newName);
    }

    @Override
    protected EnemyPropertyChangeEvent createHpChangedEvent(int oldHp, int newHp) {
        return new EnemyHpChangedEvent(this, oldHp, newHp);
    }

    @Override
    protected void fireStatusesChanged() {
        fireEvent(new EnemyStatusesChangedEvent(this, getStatusEffects()));
    }
}
