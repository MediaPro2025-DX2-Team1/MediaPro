package com.miozune.mediapro.stage;

import com.miozune.mediapro.deck.DeckModel;
import com.miozune.mediapro.discard.DiscardModel;
import com.miozune.mediapro.drawpile.DrawPileModel;
import com.miozune.mediapro.enemy.EnemyFactory;
import com.miozune.mediapro.enemy.EnemyFactory.EnemyInstance;
import com.miozune.mediapro.game.GameConfig;
import com.miozune.mediapro.hand.HandModel;
import com.miozune.mediapro.player.PlayerModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ImmutableなStageDefinitionから、ステージ入場時に実行時のStageModelインスタンスを生成するFactoryクラス。
 */
public class StageFactory {

    private final EnemyFactory enemyFactory;

    public StageFactory() {
        this(new EnemyFactory());
    }

    public StageFactory(EnemyFactory enemyFactory) {
        this.enemyFactory = Objects.requireNonNull(enemyFactory, "enemyFactory");
    }

    public StageModel create(StageDefinition definition, PlayerModel player, DeckModel deck) {
        DrawPileModel drawPile = new DrawPileModel(deck);

        HandModel hand = new HandModel();
        player.setHand(hand);

        DiscardModel discard = new DiscardModel();
        List<EnemyInstance> enemies = buildEnemies(definition.enemies());

        player.setMana(player.getMaxMana());

        StageModel model = new StageModel(player, enemies, drawPile, hand, discard, enemyFactory);
        int initialDraw = Math.max(GameConfig.HAND_SIZE - 1, 0);
        model.drawToHand(initialDraw);
        model.startBattle();
        return model;
    }

    private List<EnemyInstance> buildEnemies(List<StageDefinition.EnemyDefinition> definitions) {
        List<EnemyInstance> enemies = new ArrayList<>();
        for (StageDefinition.EnemyDefinition def : definitions) {
            enemies.add(enemyFactory.create(def));
        }
        return enemies;
    }
}
