package com.miozune.mediapro.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import com.miozune.mediapro.preview.Previewable;

/**
 * プレイヤーの状態（HP、マナ、名前）を表示するViewコンポーネント。
 * PlayerModelの変更を監視し、UIを自動更新する。
 */
public class PlayerView extends JPanel implements Previewable {

    // --- UIコンポーネント ---

    private JLabel nameLabel;
    private JProgressBar hpBar;
    private JLabel hpLabel;
    private JProgressBar manaBar;
    private JLabel manaLabel;

    // --- Model参照 ---

    private final PlayerModel model;
    private PlayerModel.PropertyChangeListener modelListener;

    // --- コンストラクタ ---

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
        setupPanel();
        initComponents();
        layoutComponents();
        setupModelListener();
        updateAllDisplays();
    }

    // --- 初期化メソッド ---

    private void setupPanel() {
        setPreferredSize(new Dimension(400, 180));
        setOpaque(true);
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
    }

    private void initComponents() {
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        hpBar = new JProgressBar(0, 100);
        hpBar.setValue(100);
        hpBar.setStringPainted(true);
        hpBar.setForeground(new Color(220, 50, 50));
        hpBar.setPreferredSize(new Dimension(300, 30));

        hpLabel = new JLabel();
        hpLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));

        manaBar = new JProgressBar(0, 10);
        manaBar.setValue(5);
        manaBar.setStringPainted(true);
        manaBar.setForeground(new Color(50, 150, 220));
        manaBar.setPreferredSize(new Dimension(300, 30));

        manaLabel = new JLabel();
        manaLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // 名前を上部に配置
        add(nameLabel, BorderLayout.NORTH);

        // HPとマナを中央に配置
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // HPセクション
        JPanel hpPanel = createStatPanel(hpLabel, hpBar);

        // マナセクション
        JPanel manaPanel = createStatPanel(manaLabel, manaBar);

        centerPanel.add(hpPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(manaPanel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createStatPanel(JLabel label, JProgressBar bar) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        panel.add(label, BorderLayout.NORTH);
        panel.add(bar, BorderLayout.CENTER);
        return panel;
    }

    // --- Model連携 ---

    /**
     * PlayerModelのリスナーをセットアップする（内部用）。
     */
    private void setupModelListener() {
        modelListener = event -> {
            switch (event) {
                case PlayerHpChangedEvent e -> updateHpDisplay(e.newHp());
                case PlayerManaChangedEvent e -> updateManaDisplay(e.newMana());
                case PlayerNameChangedEvent e -> updateNameDisplay(e.newName());
            }
        };

        model.addPropertyChangeListener(modelListener);
    }

    /**
     * すべての表示を現在のモデル状態に更新する。
     */
    private void updateAllDisplays() {
        updateNameDisplay(model.getName());
        updateHpDisplay(model.getHp());
        updateManaDisplay(model.getMana());
    }

    /**
     * PlayerModelを取得する。
     * 
     * @return PlayerModel
     */
    public PlayerModel getPlayerModel() {
        return model;
    }

    /**
     * 名前表示を更新する。
     * 
     * @param name 新しい名前
     */
    private void updateNameDisplay(String name) {
        nameLabel.setText(name != null ? name : "Unknown");
    }

    /**
     * HP表示を更新する。
     * 
     * @param hp 新しいHP
     */
    private void updateHpDisplay(int hp) {
        int maxHp = model.getMaxHp();
        hpBar.setMaximum(maxHp);
        hpBar.setValue(hp);
        hpBar.setString(String.format("%d / %d", hp, maxHp));
        hpLabel.setText(String.format("HP: %d / %d", hp, maxHp));

        // HPに応じて色を変更
        if (hp < maxHp * 0.3) {
            hpBar.setForeground(new Color(180, 30, 30)); // 暗い赤
        } else if (hp < maxHp * 0.6) {
            hpBar.setForeground(new Color(220, 180, 50)); // 黄色
        } else {
            hpBar.setForeground(new Color(220, 50, 50)); // 通常の赤
        }
    }

    /**
     * マナ表示を更新する。
     * 
     * @param mana 新しいマナ
     */
    private void updateManaDisplay(int mana) {
        int maxMana = model.getMaxMana();
        manaBar.setMaximum(maxMana);
        manaBar.setValue(mana);
        manaBar.setString(String.format("%d / %d", mana, maxMana));
        manaLabel.setText(String.format("Mana: %d / %d", mana, maxMana));
    }

    // --- Previewable実装 ---
    
    @Override
    public String getPreviewDescription() {
        return "プレイヤー情報（HP、マナ、名前）を表示するコンポーネント。" +
               "PlayerModelの変更をリアルタイムで反映する。";
    }

    @Override
    public void setupPreview() {
        model.setMaxHp(100);
        model.setHp(75);
        model.setMaxMana(10);
        model.setMana(6);
        updateAllDisplays();
    }
}
