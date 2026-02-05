package com.miozune.mediapro.enemy;

import com.miozune.mediapro.actor.ActorStatusView;
import com.miozune.mediapro.enemy.events.EnemyHpChangedEvent;
import com.miozune.mediapro.enemy.events.EnemyNameChangedEvent;
import com.miozune.mediapro.enemy.events.EnemyStatusesChangedEvent;
import com.miozune.mediapro.preview.Previewable;
import com.miozune.mediapro.status.StatusListView;
import com.miozune.mediapro.util.ImageLoader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.swing.JPanel;

/**
 * Enemy の状態を表示する View クラス
 */
public class EnemyView extends JPanel implements Previewable {

    private static final ActorStatusView.Style STYLE = new ActorStatusView.Style(
        new Color(32, 32, 36, 200),
        new Color(90, 90, 100),
        Color.WHITE,
        Color.LIGHT_GRAY,
        new Color(45, 45, 50),
        (hp, maxHp) -> new Color(70, 190, 90)
    );

    private final EnemyModel model;
    private final ActorStatusView statusView;
    private final StatusListView statusListView;
    private EnemyModel.PropertyChangeListener modelListener;

    /**
     * デフォルトコンストラクタ（Previewable 要件）
     */
    public EnemyView() {
        this(EnemyModel.createDefault());
    }

    /**
     * Model を受け取るコンストラクタ
     *
     * @param model 表示する EnemyModel
     */
    public EnemyView(EnemyModel model) {
        this.model = Objects.requireNonNull(model);
        this.statusListView = new StatusListView();
        this.statusView = new ActorStatusView(STYLE, statusListView);
        setLayout(new BorderLayout());
        setOpaque(false);

        add(statusView, BorderLayout.CENTER);

        setupModelListener();
        updateAllDisplays();
        loadAndDisplayImage();
    }

    private void loadAndDisplayImage() {
        EnemyType type = model.getEnemyType();
        String fileName = type.getImageFileName();
        BufferedImage image = ImageLoader.loadEntityImage(fileName);
        double scale = type.getScale();
        statusView.updateImage(image, scale);
    }

    private void setupModelListener() {
        modelListener = event -> {
            if (event instanceof EnemyHpChangedEvent hpChanged) {
                statusView.updateHp(hpChanged.newHp(), model.getMaxHp());
                return;
            }
            if (event instanceof EnemyNameChangedEvent nameChanged) {
                statusView.updateName(nameChanged.newName());
                return;
            }
            if (event instanceof EnemyStatusesChangedEvent statusesChanged) {
                statusListView.updateStatuses(statusesChanged.effects());
            }
        };

        model.addPropertyChangeListener(modelListener);
    }

    private void updateAllDisplays() {
        statusView.updateName(model.getName());
        statusView.updateHp(model.getHp(), model.getMaxHp());
        statusListView.updateStatuses(model.getStatusEffects());
    }

    public EnemyModel getModel() {
        return model;
    }

    /**
     * 背景のハイライト状態を更新する（ターゲット選択時の表示変更用）。
     *
     * @param highlighted trueの場合、背景を青っぽくハイライト
     */
    public void updateBackgroundHighlight(boolean highlighted) {
        statusView.updateBackgroundHighlight(highlighted);
    }

    // --- Previewable 実装 ---

    @Override
    public String getPreviewDescription() {
        return "敵キャラクターのステータス表示";
    }

    @Override
    public void setupPreview() {
        model.setName("プレビュースライム");
        model.setMaxHp(100);
        model.setHp(60);
        model.addWeakness(2);
        updateAllDisplays();
        loadAndDisplayImage();
    }
}
