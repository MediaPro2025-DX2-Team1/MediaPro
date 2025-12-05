package com.miozune.mediapro.game;

import com.miozune.mediapro.core.GameState;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * GameModelのテストクラス。
 */
public class GameModelTest {
    
    @Test
    public void testInitialState() {
        GameModel model = new GameModel();
        assertEquals("Initial state should be INITIALIZING", 
            GameState.INITIALIZING, model.getState());
        assertEquals("Initial score should be 0", 0, model.getScore());
    }
    
    @Test
    public void testSetState() {
        GameModel model = new GameModel();
        model.setState(GameState.PLAYING);
        assertEquals("State should be PLAYING", GameState.PLAYING, model.getState());
    }
    
    @Test
    public void testSetScore() {
        GameModel model = new GameModel();
        model.setScore(100);
        assertEquals("Score should be 100", 100, model.getScore());
    }
    
    @Test
    public void testReset() {
        GameModel model = new GameModel();
        model.setState(GameState.PLAYING);
        model.setScore(100);
        model.reset();
        assertEquals("State should be INITIALIZING after reset", 
            GameState.INITIALIZING, model.getState());
        assertEquals("Score should be 0 after reset", 0, model.getScore());
    }
}
