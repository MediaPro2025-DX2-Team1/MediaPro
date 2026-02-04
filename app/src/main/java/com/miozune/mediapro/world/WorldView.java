package com.miozune.mediapro.world;

import com.miozune.mediapro.preview.Previewable;
import com.miozune.mediapro.progress.ProgressModel;
import com.miozune.mediapro.stage.StageFactory;
import com.miozune.mediapro.util.ImageLoader;
import com.miozune.mediapro.util.ImageUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class WorldView extends JPanel implements Previewable {

    private final WorldModel worldModel;
    private final BufferedImage backgroundImage;
    private JButton stageButton1;
    private JButton stageButton2;
    private JButton stageButton3;
    private JButton deckListButton;

    public WorldView() {
        this(WorldModel.createDefault(new StageFactory(), new ProgressModel()));
    }

    public WorldView(WorldModel worldModel) {
        this.worldModel = worldModel;
        this.backgroundImage = ImageLoader.loadBackgroundImage("title_bg.png");
        setupPanel();
        initComponents();
        layoutComponents();
        setupModelListener();
        updateStageButtons();
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(600, 400));
        setOpaque(true);
    }

    private void initComponents() {
        stageButton1 = new JButton("Stage 1");
        stageButton1.setPreferredSize(new Dimension(200, 60));
        stageButton1.setFont(new Font("Arial", Font.BOLD, 24));

        stageButton2 = new JButton("Stage 2");
        stageButton2.setPreferredSize(new Dimension(200, 60));
        stageButton2.setFont(new Font("Arial", Font.BOLD, 24));

        stageButton3 = new JButton("Stage 3");
        stageButton3.setPreferredSize(new Dimension(200, 60));
        stageButton3.setFont(new Font("Arial", Font.BOLD, 24));

        deckListButton = new JButton("Decks");
        deckListButton.setPreferredSize(new Dimension(200, 40));
        deckListButton.setFont(new Font("Arial", Font.BOLD, 18));
    }

    private void layoutComponents() {
        JLabel titleLabel = new JLabel("STAGE SELECT");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 56));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // ボタン間の余白

        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(stageButton1, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        buttonPanel.add(stageButton2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        buttonPanel.add(stageButton3, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        buttonPanel.add(deckListButton, gbc);

        add(titlePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    public JButton getStageButton1() { return stageButton1; }
    public JButton getStageButton2() { return stageButton2; }
    public JButton getStageButton3() { return stageButton3; }
    public JButton getDeckListButton() { return deckListButton; }

    public WorldModel getWorldModel() { return worldModel; }

    /**
     * Modelのリスナーをセットアップします。
     */
    private void setupModelListener() {
        worldModel.getProgressModel().addPropertyChangeListener(event -> {
            updateStageButtons();
        });
    }

    /**
     * ステージボタンの状態を更新します。
     * アンロックされていないステージは無効化され、グレーアウト表示されます。
     */
    private void updateStageButtons() {
        updateStageButton(stageButton1, "stage1");
        updateStageButton(stageButton2, "stage2");
        updateStageButton(stageButton3, "stage3");
    }

    /**
     * 個別のステージボタンの状態を更新します。
     *
     * @param button ステージボタン
     * @param stageId ステージID（例: "stage1"）
     */
    private void updateStageButton(JButton button, String stageId) {
        boolean unlocked = worldModel.isStageUnlocked(stageId);
        boolean cleared = worldModel.isStageCleared(stageId);

        button.setEnabled(unlocked);

        if (cleared) {
            button.setBackground(new Color(144, 238, 144)); // ライトグリーン（クリア済み）
        } else if (unlocked) {
            button.setBackground(null); // デフォルト色（アンロック済み）
        } else {
            button.setBackground(new Color(180, 180, 180)); // グレー（ロック中）
        }
    }

    @Override
    public String getPreviewDescription() {
        return "ステージ選択画面のプレビュー";
    }

    @Override
    public void setupPreview() {
        // プレビュー用にstage2とstage3をアンロック、stage1をクリア済みに設定
        worldModel.getProgressModel().clearStage("stage1");
        worldModel.getProgressModel().unlockStage("stage2");
        worldModel.getProgressModel().unlockStage("stage3");
        updateStageButtons();

        stageButton1.addActionListener(e -> System.out.println("[Preview] Stage 1 clicked"));
        stageButton2.addActionListener(e -> System.out.println("[Preview] Stage 2 clicked"));
        stageButton3.addActionListener(e -> System.out.println("[Preview] Stage 3 clicked"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageUtils.drawBackgroundImage(g, backgroundImage, getWidth(), getHeight());
    }
}
