package com.miozune.mediapro.effect;

import com.miozune.mediapro.effect.action.ActionContext;
import com.miozune.mediapro.effect.action.ActionEffect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * カードが持つ複合効果。
 */
public final class CardAction {
    private final List<ActionEffect> actions;

    public CardAction(List<ActionEffect> actions) {
        this.actions = List.copyOf(actions);
    }

    public static CardAction of(ActionEffect... effects) {
        List<ActionEffect> list = new ArrayList<>();
        if (effects != null) {
            Collections.addAll(list, effects);
        }
        return new CardAction(list);
    }

    public List<ActionEffect> actions() {
        return actions;
    }

    public boolean execute(ActionContext context) {
        boolean success = true;
        for (ActionEffect effect : actions) {
            boolean result = effect.apply(context);
            success = success && result;
        }
        return success;
    }
}
