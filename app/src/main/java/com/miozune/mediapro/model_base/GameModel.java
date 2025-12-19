package com.miozune.mediapro.model_base;

import java.util.List;

public class GameModel {
    private Player player;
    private List<DeckRecipe> decks;
    private World world;
    private Enum<ScreenType> currentScreen;

    public GameModel() {
        
    }
}
