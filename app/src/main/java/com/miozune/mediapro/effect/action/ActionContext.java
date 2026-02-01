package com.miozune.mediapro.effect.action;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.discard.DiscardModel;
import com.miozune.mediapro.drawpile.DrawPileModel;
import com.miozune.mediapro.enemy.EnemyModel;
import com.miozune.mediapro.hand.HandModel;
import com.miozune.mediapro.player.PlayerModel;
import com.miozune.mediapro.stage.StageModel;
import java.util.List;

/**
 * 効果実行に必要なコンテキスト。
 */
public record ActionContext(
        StageModel stage,
        PlayerModel player,
        List<EnemyModel> enemies,
        EnemyModel target,
        DrawPileModel drawPile,
        HandModel hand,
        DiscardModel discard,
        CardModel card) {

    public EnemyModel resolveTarget() {
        if (target != null && !target.isDead()) {
            return target;
        }
        if (enemies == null) {
            return null;
        }
        for (EnemyModel enemy : enemies) {
            if (!enemy.isDead()) {
                return enemy;
            }
        }
        return null;
    }
}
