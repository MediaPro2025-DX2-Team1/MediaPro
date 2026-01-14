package com.miozune.mediapro.enemy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import com.miozune.mediapro.enemy.events.EnemyHpChangedEvent;
import com.miozune.mediapro.enemy.events.EnemyNameChangedEvent;
import com.miozune.mediapro.preview.Previewable;

/**
 * Enemy の状態を表示する View クラス
 */
public class EnemyView extends JPanel implements Previewable {
    
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
        setPreferredSize(new Dimension(250, 150));
        setBackground(new Color(50, 50, 50));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 0, 0), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }
    
    private void initComponents() {
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Meiryo", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        hpLabel = new JLabel();
        hpLabel.setFont(new Font("Meiryo", Font.PLAIN, 14));
        hpLabel.setForeground(Color.WHITE);
        hpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        hpBar = new JProgressBar(0, 100);
        hpBar.setStringPainted(false);
        hpBar.setPreferredSize(new Dimension(220, 20));
        hpBar.setForeground(new Color(220, 50, 50));
        hpBar.setBackground(new Color(100, 30, 30));
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(0, 8));
        
        add(nameLabel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout(0, 5));
        centerPanel.setOpaque(false);
        centerPanel.add(hpLabel, BorderLayout.NORTH);
        centerPanel.add(hpBar, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void setupModelListener() {
        modelListener = event -> {
            switch (event) {
                case EnemyHpChangedEvent e -> updateHpDisplay();
                case EnemyNameChangedEvent e -> updateNameDisplay();
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
        
        int percentage = maxHp > 0 ? (currentHp * 100) / maxHp : 0;
        hpBar.setValue(percentage);
        
        // HP の割合に応じて色を変更
        if (percentage > 50) {
            hpBar.setForeground(new Color(220, 50, 50));
        } else if (percentage > 25) {
            hpBar.setForeground(new Color(220, 150, 50));
        } else {
            hpBar.setForeground(new Color(100, 100, 100));
        }
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
