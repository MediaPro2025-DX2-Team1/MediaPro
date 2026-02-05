package com.miozune.mediapro.enemy.behavior;

import com.miozune.mediapro.effect.EffectType;
import com.miozune.mediapro.enemy.EnemyModel;
import com.miozune.mediapro.enemy.EnemyType;
import com.miozune.mediapro.player.PlayerModel;
import com.miozune.mediapro.stage.StageModel;
import java.util.List;

/** 敵行動からステージへの操作をまとめるコンテキスト。 */
public class EnemyActionContext {
    private final StageModel stage;
    private final EnemyModel self;

    public EnemyActionContext(StageModel stage, EnemyModel self) {
        this.stage = stage;
        this.self = self;
    }

    public EnemyModel self() {
        return self;
    }

    public PlayerModel player() {
        return stage.getPlayer();
    }

    public List<EnemyModel> enemies() {
        return stage.getEnemies();
    }

    public void attackPlayer(int baseDamage) {
        stage.enemyAttack(self, baseDamage);
    }

    public void attackPlayer(int baseDamage, int times) {
        stage.enemyAttack(self, baseDamage, times);
    }

    public void addShieldToSelf(int amount) {
        self.addShield(amount, true); // 敵のシールドは永続
        stage.triggerEffect(EffectType.SHIELD, self);
    }

    public void addStrengthToSelf(int bonus) {
        self.addStrength(bonus);
        stage.triggerEffect(EffectType.BUFF, self);
    }

    public void applyWeaknessToPlayer(int turns) {
        stage.getPlayer().addWeakness(turns);
        stage.triggerEffect(EffectType.DEBUFF, stage.getPlayer());
    }

    public void summon(EnemyType type) {
        stage.summonEnemy(type);
    }
}
