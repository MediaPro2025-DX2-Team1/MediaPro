package com.miozune.mediapro.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 複数の {@link ActionEffect} を組み合わせた複合効果。
 * Composite Patternの実装で、単一効果と同じように扱える。
 */
public final class CompositeActionEffect implements ActionEffect {
    private final List<ActionEffect> actions;

    public CompositeActionEffect(List<ActionEffect> actions) {
        this.actions = List.copyOf(actions);
    }

    public static CompositeActionEffect of(ActionEffect... effects) {
        List<ActionEffect> list = new ArrayList<>();
        if (effects != null) {
            Collections.addAll(list, effects);
        }
        return new CompositeActionEffect(list);
    }

    @Override
    public boolean apply(ActionContext context) {
        boolean success = true;
        for (ActionEffect effect : actions) {
            boolean result = effect.apply(context);
            success = success && result;
        }
        return success;
    }
}
