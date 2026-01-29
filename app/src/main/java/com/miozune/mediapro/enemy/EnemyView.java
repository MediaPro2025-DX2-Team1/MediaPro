package com.miozune.mediapro.enemy;

import com.miozune.mediapro.enemy.events.EnemyHpChangedEvent;
import com.miozune.mediapro.enemy.events.EnemyNameChangedEvent;
import com.miozune.mediapro.preview.Previewable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * Enemy の状態を表示する View クラス
 */
public class EnemyView extends JPanel implements Previewable {

    private static class ColoredProgressBarUI extends BasicProgressBarUI {
        private final Color barColor;

        ColoredProgressBarUI(Color barColor) {
            this.barColor = barColor;
        }

        @Override
        protected Color getSelectionForeground() {
            return Color.BLACK;
        }

        @Override
        protected Color getSelectionBackground() {
            return Color.BLACK;
        }

        @Override
        protected void paintDeterminate(Graphics g, JComponent c) {
            progressBar.setForeground(barColor);
            super.paintDeterminate(g, c);
        }
    }

    private final EnemyModel model;
    private JLabel nameLabel;
    private JLabel hpLabel;
    private JProgressBar hpBar;
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
        this.model = model;
        setupPanel();
        initComponents();
        layoutComponents();

        setupModelListener();
        updateAllDisplays();
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(400, 180));
        setMaximumSize(new Dimension(400, 180));
        setOpaque(true);
        setBackground(new Color(32, 32, 36));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 90, 100), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
    }

    private void initComponents() {
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        hpLabel = new JLabel();
        hpLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        hpLabel.setForeground(Color.LIGHT_GRAY);
        hpLabel.setHorizontalAlignment(SwingConstants.LEFT);

        hpBar = new JProgressBar(0, 100);
        hpBar.setStringPainted(true);
        hpBar.setPreferredSize(new Dimension(300, 24));
        hpBar.setMaximumSize(new Dimension(300, 24));
        hpBar.setForeground(new Color(60, 180, 60));
        hpBar.setBackground(new Color(45, 45, 50));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        add(nameLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JPanel hpPanel = new JPanel(new BorderLayout(5, 5));
        hpPanel.setOpaque(false);
        hpPanel.add(hpLabel, BorderLayout.NORTH);
        hpPanel.add(hpBar, BorderLayout.CENTER);

        centerPanel.add(hpPanel);
        centerPanel.add(Box.createVerticalStrut(10));

        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupModelListener() {
        modelListener = event -> {
            if (event instanceof EnemyHpChangedEvent) {
                updateHpDisplay();
                return;
            }
            if (event instanceof EnemyNameChangedEvent) {
                updateNameDisplay();
            }
        };

        model.addPropertyChangeListener(modelListener);
    }

    private void updateAllDisplays() {
        updateNameDisplay();
        updateHpDisplay();
    }

    private void updateNameDisplay() {
        nameLabel.setText(model.getName());
    }

    private void updateHpDisplay() {
        int currentHp = model.getHp();
        int maxHp = model.getMaxHp();

        hpLabel.setText(String.format("HP: %d / %d", currentHp, maxHp));

        hpBar.setMaximum(maxHp);
        hpBar.setValue(currentHp);
        hpBar.setString(String.format("%d / %d", currentHp, maxHp));

        Color barColor = selectHpColor(currentHp, maxHp);
        hpBar.setUI(new ColoredProgressBarUI(barColor));
    }

    private Color selectHpColor(int hp, int maxHp) {
        if (maxHp <= 0) {
            return new Color(90, 90, 90);
        }

        double ratio = (double) hp / maxHp;
        if (ratio < 0.3) {
            return new Color(180, 50, 50);
        }
        if (ratio < 0.5) {
            return new Color(210, 170, 60);
        }
        return new Color(70, 190, 90);
    }

    public EnemyModel getModel() {
        return model;
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
    }
}
