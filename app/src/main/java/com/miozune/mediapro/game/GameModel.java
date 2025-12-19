package com.miozune.mediapro.game;

import java.util.List;

import com.miozune.mediapro.player.PlayerModel;
import com.miozune.mediapro.deckrecipe.DeckRecipeModel;
import com.miozune.mediapro.world.WorldModel;

public class GameModel {
    private PlayerModel player;
    private List<DeckRecipeModel> decks;
    private WorldModel world;
    // private Enum<ScreenType> currentScreen;

    public GameModel() {
        
    }
}
