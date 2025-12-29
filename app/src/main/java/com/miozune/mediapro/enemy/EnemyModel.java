package com.miozune.mediapro.enemy;

import java.util.List;

import com.miozune.mediapro.Effect.EffectModel;
import com.miozune.mediapro.player.PlayerModel;

public class EnemyModel {
    private String name;
    private int hp;
    private int attackPoint;
    private List<EffectModel> effects;

    public EnemyModel() {

    }

    public int getHp() {
        return hp;
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public PlayerModel receiveDamage() {
        throw new UnsupportedOperationException();
    }
}
