package com.miozune.mediapro.stage;

import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.discard.DiscardModel;
import com.miozune.mediapro.drawpile.DrawPileModel;
import com.miozune.mediapro.enemy.EnemyModel;
import com.miozune.mediapro.game.GameConfig;
import com.miozune.mediapro.hand.HandModel;
import com.miozune.mediapro.player.PlayerModel;
import java.util.ArrayList;
import java.util.List;

/**
 * ImmutableなStageDefinitionから、ステージ入場時に実行時のStageModelインスタンスを生成するFactoryクラス。
 */
public class StageFactory {

    public StageModel create(StageDefinition definition, PlayerModel player, DeckModel deck) {
        DrawPileModel drawPile = new DrawPileModel(deck);

        HandModel hand = new HandModel();
        player.setHand(hand);

        DiscardModel discard = new DiscardModel();
        List<EnemyModel> enemies = buildEnemies(definition.enemies());

        player.setMana(player.getMaxMana());

        StageModel model = new StageModel(player, enemies, drawPile, hand, discard);
        int initialDraw = Math.max(GameConfig.HAND_SIZE - 1, 0);
        model.drawToHand(initialDraw);
        model.startBattle();
        return model;
    }

    private List<EnemyModel> buildEnemies(List<StageDefinition.EnemyDefinition> definitions) {
        List<EnemyModel> enemies = new ArrayList<>();
        for (StageDefinition.EnemyDefinition def : definitions) {
            enemies.add(new EnemyModel(def.name(), def.hp(), def.maxHp()));
        }
        return enemies;
    }
}
