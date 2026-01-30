package com.miozune.mediapro.effect.action;

import com.miozune.mediapro.player.PlayerModel;

/**
 * マナを回復する。
 */
public final class RestoreManaActionEffect implements ActionEffect {
    private final int amount;

    public RestoreManaActionEffect(int amount) {
        this.amount = Math.max(0, amount);
    }

    @Override
    public boolean apply(ActionContext context) {
        if (amount <= 0) {
            return true;
        }
        PlayerModel player = context.player();
        player.addMana(amount);
        return true;
    }
}
