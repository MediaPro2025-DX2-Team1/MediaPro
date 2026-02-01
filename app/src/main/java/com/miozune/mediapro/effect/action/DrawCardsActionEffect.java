package com.miozune.mediapro.effect.action;

/**
 * 山札からカードをドロー。
 */
public final class DrawCardsActionEffect implements ActionEffect {
    private final int count;

    public DrawCardsActionEffect(int count) {
        this.count = Math.max(0, count);
    }

    @Override
    public boolean apply(ActionContext context) {
        if (count <= 0) {
            return true;
        }
        context.stage().drawToHand(count);
        return true;
    }
}
