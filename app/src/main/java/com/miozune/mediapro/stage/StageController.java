package com.miozune.mediapro.stage;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardTargetType;
import com.miozune.mediapro.card.events.CardClickedEvent;
import com.miozune.mediapro.enemy.EnemyModel;
import com.miozune.mediapro.game.GameModel;
import com.miozune.mediapro.hand.events.HandCardChangedEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;

public class StageController {

    private final GameModel gameModel;
    private final StageModel model;
    private final StageView view;
    private CardModel targetingCard;
    private final Set<EnemyModel> enemyListenerRegistered = new HashSet<>();

    public StageController(GameModel gameModel, StageModel model, StageView view) {
        this.gameModel = gameModel;
        this.model = model;
        this.view = view;

        // バトル終了リスナー登録
        model.setBattleListener(playerWon -> handleBattleEnd(playerWon));

        model.getHand().addPropertyChangeListener(event -> {
            if (event instanceof HandCardChangedEvent handEvent) {
                view.updateHand(handEvent.newcards(), this::handleCardClick);
            }
        });

        model.getPlayer().addPropertyChangeListener(event -> model.checkBattleState());
        attachEnemyListeners(model.getEnemies());

        model.addEnemyListChangeListener(this::handleEnemiesChanged);

        // プレイヤー/敵ビューをセット
        view.setActors(model.getPlayer(), model.getEnemies());
        view.setEnemyClickListener(this::handleEnemyClick);
        view.setBackgroundClickListener(this::handleBackgroundCancel);

        connectUI();
        updateView();
    }

    private void attachEnemyListeners(List<EnemyModel> enemies) {
        if (enemies == null) {
            return;
        }
        for (EnemyModel enemy : enemies) {
            if (enemy == null || enemyListenerRegistered.contains(enemy)) {
                continue;
            }
            enemy.addPropertyChangeListener(event -> model.checkBattleState());
            enemyListenerRegistered.add(enemy);
        }
    }

    /* View のボタンやイベントを Model とつなぐ */
    private void connectUI() {
        view.getDeckButton().addActionListener(e -> {
            view.showDrawPile(model.getDrawpile().getCards());
        });

        view.getDiscardButton().addActionListener(e -> {
            view.showDiscardPile(model.getDiscard().getCards());
        });

        view.getEndTurnButton().addActionListener(e -> {
            targetingCard = null;
            view.exitTargetSelection();
            model.nextTurn();
            updateView();
        });
    }

    /* Model の情報を View に反映 */
    private void updateView() {
        view.updateHand(model.getHand().getCards(), event -> {
            if (event.isRightClick()) {
                view.showCardDetail(event.card());
                return;
            }
            handleCardClick(event);
        });
    }

    private void handleEnemiesChanged(List<EnemyModel> enemies) {
        attachEnemyListeners(enemies);
        view.refreshEnemies(enemies);
    }

    /* バトル終了時の処理 */
    private void handleBattleEnd(boolean playerWon) {
        view.getDeckButton().setEnabled(false);
        view.getDiscardButton().setEnabled(false);
        view.getEndTurnButton().setEnabled(false);
        if (!playerWon) {
            model.getPlayer().resetAfterDefeat();
        }
        SwingUtilities.invokeLater(() -> {
            view.showBattleResult(playerWon, () -> gameModel.goToWorld());
        });
    }

    private void handleCardClick(CardClickedEvent event) {
        if (!event.isLeftClick()) {
            return;
        }
        if (model.isBattleOver() || model.getTurn() != StageModel.Turn.PLAYER) {
            return;
        }
        CardTargetType targetType = event.card().targetType();

        // 選択不要のカードは即時実行して終了
        if (!targetType.requiresEnemySelection()) {
            playCard(event.card(), null);
            return;
        }

        // 単体指定カードのみ敵選択ロジック
        EnemyModel singleAlive = null;
        int aliveCount = 0;
        for (EnemyModel enemy : model.getEnemies()) {
            if (enemy != null && !enemy.isDead()) {
                aliveCount++;
                singleAlive = enemy;
                if (aliveCount > 1) {
                    break;
                }
            }
        }

        if (aliveCount == 0) {
            return;
        }
        if (aliveCount == 1) {
            playCard(event.card(), singleAlive);
            return;
        }

        targetingCard = event.card();
        view.enterTargetSelection();
    }

    private void playCard(CardModel card, EnemyModel target) {
        boolean played = model.playCard(card, target);
        if (!played) {
            System.out.println("カードを使用できませんでした");
        }
        targetingCard = null;
        view.exitTargetSelection();
        updateView();
    }

    private void handleEnemyClick(EnemyModel enemy) {
        if (targetingCard == null || enemy == null || enemy.isDead()) {
            return;
        }
        playCard(targetingCard, enemy);
    }

    private void handleBackgroundCancel() {
        if (targetingCard != null) {
            targetingCard = null;
            view.exitTargetSelection();
        }
    }
}
