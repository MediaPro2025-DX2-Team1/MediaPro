package com.miozune.mediapro.effect.action;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.discard.DiscardModel;
import com.miozune.mediapro.enemy.EnemyModel;
import com.miozune.mediapro.hand.HandModel;
import java.util.ArrayList;
import java.util.List;

/**
 * 手札を全て捨て、その枚数に応じて単体へ大ダメージ。
 */
public final class AllOutStrikeActionEffect implements ActionEffect {
    private final int baseDamagePerDiscard;

    public AllOutStrikeActionEffect(int baseDamagePerDiscard) {
        this.baseDamagePerDiscard = Math.max(0, baseDamagePerDiscard);
    }

    @Override
    public boolean apply(ActionContext context) {
        EnemyModel target = context.resolveTarget();
        if (target == null) {
            return false;
        }
        HandModel hand = context.hand();
        DiscardModel discard = context.discard();
        if (hand == null || discard == null) {
            return false;
        }

        List<CardModel> toDiscard = new ArrayList<>(hand.getCards());
        CardModel source = context.card();
        if (source != null) {
            toDiscard.remove(source);
        }

        if (toDiscard.isEmpty()) {
            return true;
        }

        for (CardModel card : toDiscard) {
            hand.removeCard(card);
            discard.addCard(card);
        }

        int totalDamage = toDiscard.size() * baseDamagePerDiscard;
        if (totalDamage <= 0) {
            return true;
        }

        int attackDamage = context.player().applyOutgoingDamageModifiers(totalDamage);
        target.receiveDamage(attackDamage);
        return true;
    }
}
