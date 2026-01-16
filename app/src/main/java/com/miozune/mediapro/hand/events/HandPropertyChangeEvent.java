package com.miozune.mediapro.hand.events;

import com.miozune.mediapro.hand.HandModel;

public sealed interface HandPropertyChangeEvent 
    permits HandCardChangedEvent {
    
    HandModel getHand();
}
