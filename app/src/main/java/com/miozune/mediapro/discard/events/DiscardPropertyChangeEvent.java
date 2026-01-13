package com.miozune.mediapro.discard.events;

import com.miozune.mediapro.discard.DiscardModel;

public sealed interface DiscardPropertyChangeEvent 
    permits DiscardCardChangedEvent {
    
    DiscardModel getDiscard();
}
