package com.miozune.mediapro.player;

import com.miozune.mediapro.actor.ActorStatusView;
import com.miozune.mediapro.player.events.PlayerHpChangedEvent;
import com.miozune.mediapro.player.events.PlayerNameChangedEvent;
import com.miozune.mediapro.player.events.PlayerStatusesChangedEvent;
import com.miozune.mediapro.preview.Previewable;
import com.miozune.mediapro.status.StatusListView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Objects;
import javax.swing.JPanel;

/**
 * プレイヤーの状態（HP、名前）を表示するViewコンポーネント。
 */
public class PlayerView extends JPanel implements Previewable {

    private static final ActorStatusView.Style STYLE = new ActorStatusView.Style(
        new Color(245, 245, 245),
        new Color(100, 100, 100),
        Color.BLACK,
        new Color(50, 50, 50),
        new Color(240, 240, 240),
        PlayerView::resolveHpColor
    );

    private final PlayerModel model;
    private final ActorStatusView statusView;
    private final StatusListView statusListView;
    private PlayerModel.PropertyChangeListener modelListener;

    /**
     * no-argコンストラクタ（Previewable要件）。
     * プレビュー用のダミーモデルで初期化される。
     */
    public PlayerView() {
        this(PlayerModel.createDefaultPlayer());
    }

    /**
     * PlayerModelを指定するコンストラクタ。
     *
     * @param model 表示するPlayerModel
     */
    public PlayerView(PlayerModel model) {
        this.model = Objects.requireNonNull(model);
        this.statusView = new ActorStatusView(STYLE);
        this.statusListView = new StatusListView();
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(420, 230));
        setMinimumSize(new Dimension(420, 230));
        setMaximumSize(new Dimension(420, 230));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(statusListView, BorderLayout.EAST);

        add(statusView, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setupModelListener();
        updateAllDisplays();
    }

    private static Color resolveHpColor(int hp, int maxHp) {
        if (maxHp <= 0) {
            return new Color(50, 180, 50);
        }
        double ratio = (double) hp / maxHp;
        if (ratio < 0.3) {
            return new Color(180, 30, 30); // 暗い赤
        }
        if (ratio < 0.5) {
            return new Color(220, 180, 50); // 黄色
        }
        return new Color(50, 180, 50); // 緑
    }

    /**
     * PlayerModelのリスナーをセットアップする（内部用）。
     */
    private void setupModelListener() {
        modelListener = event -> {
            switch (event) {
                case PlayerHpChangedEvent e -> statusView.updateHp(e.newHp(), model.getMaxHp());
                case PlayerNameChangedEvent e -> statusView.updateName(e.newName());
                case PlayerStatusesChangedEvent e -> statusListView.updateStatuses(e.effects());
                default -> { }
            }
        };

        model.addPropertyChangeListener(modelListener);
    }

    /**
     * すべての表示を現在のモデル状態に更新する。
     */
    private void updateAllDisplays() {
        statusView.updateName(model.getName());
        statusView.updateHp(model.getHp(), model.getMaxHp());
        statusListView.updateStatuses(model.getEffects());
    }

    /**
     * PlayerModelを取得する。
     *
     * @return PlayerModel
     */
    public PlayerModel getPlayerModel() {
        return model;
    }

    // --- Previewable実装 ---

    @Override
    public String getPreviewDescription() {
        return "プレイヤー情報（HP、名前）を表示するコンポーネント。" +
               "PlayerModelの変更をリアルタイムで反映する。";
    }

    @Override
    public void setupPreview() {
        model.setMaxHp(100);
        model.setHp(75);
        model.addShield(12);
        model.addStrength(3, 2);
        model.addWeakness(2);
        updateAllDisplays();
    }
}
