package com.miozune.mediapro.stage;

import java.util.ArrayList;
import java.util.List;

import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.discard.DiscardModel;
import com.miozune.mediapro.drawpile.DrawPileModel;
import com.miozune.mediapro.enemy.EnemyModel;
import com.miozune.mediapro.hand.HandModel;
import com.miozune.mediapro.player.PlayerModel;

/**
 * ImmutableなStageDefinitionから、ステージ入場時に実行時のStageModelインスタンスを生成するFactoryクラス。
 */
public class StageFactory {

    public StageModel create(StageDefinition definition, PlayerModel player, DeckModel deck) {
        DrawPileModel drawPile = new DrawPileModel(deck);
        drawPile.initialize();

        HandModel hand = new HandModel();
        player.setHand(hand);

        DiscardModel discard = new DiscardModel();
        List<EnemyModel> enemies = buildEnemies(definition.enemies());

        player.resetMana();

        return new StageModel(player, enemies, drawPile, hand, discard);
    }

    private List<EnemyModel> buildEnemies(List<StageDefinition.EnemyDefinition> definitions) {
        List<EnemyModel> enemies = new ArrayList<>();
        for (StageDefinition.EnemyDefinition def : definitions) {
            enemies.add(new EnemyModel(def.name(), def.hp(), def.maxHp()));
        }
        return enemies;
    }
}
