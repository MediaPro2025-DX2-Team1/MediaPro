package com.miozune.mediapro.player;

import java.util.List;

import com.miozune.mediapro.Effect.EffectModel;
import com.miozune.mediapro.hand.HandModel;

public class PlayerModel {
    private String name;
    private HandModel hand;
    private int mana;
    private int hp;
    private List<EffectModel> effects;
    
    public PlayerModel() {
        
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public HandModel getHand() {
        throw new UnsupportedOperationException();
    }   

    public int getMana() {
        throw new UnsupportedOperationException();
    }

    public int getHp() {
        throw new UnsupportedOperationException();
    }

    public int addMana() {
        throw new UnsupportedOperationException();
    }

    public int resetMana() {
        throw new UnsupportedOperationException();
    }
}
