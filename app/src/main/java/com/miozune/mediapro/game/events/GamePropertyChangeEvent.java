package com.miozune.mediapro.game.events;

import com.miozune.mediapro.game.GameModel;

public sealed interface GamePropertyChangeEvent permits GameSceneChangedEvent {
    GameModel source();
}
