package com.miozune.mediapro.card;

public class CardModel {
    private String name;
    private int cost;
    private String image;
    private String description; 
    
    public CardModel() {
        
    }

    public int getCost() {
        throw new UnsupportedOperationException();
    }

    public void applyEffect() {
        throw new UnsupportedOperationException();
    }
}