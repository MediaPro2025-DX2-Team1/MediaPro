package com.miozune.mediapro.game.events;

import com.miozune.mediapro.game.GameModel;
import com.miozune.mediapro.game.GameScene;

public record GameSceneChangedEvent(
    GameModel source,
    GameScene oldScene,
    GameScene newScene
) implements GamePropertyChangeEvent {
}
