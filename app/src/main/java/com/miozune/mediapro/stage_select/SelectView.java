package com.miozune.mediapro.stage_select;

import javax.swing.*;
import java.awt.*;

public class SelectView extends JPanel {

    private RoundButton stageButton1;
    private RoundButton stageButton2;
    private RoundButton stageButton3;

    public SelectView() {
        setupPanel();
        initComponents();
        layoutComponents();
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(600, 400));
        setOpaque(true);
        setBackground(new Color(240, 240, 240));
    }

    private void initComponents() {
        stageButton1 = new RoundButton("1");
        stageButton1.setPreferredSize(new Dimension(100, 100));
        stageButton1.setFont(new Font("Arial", Font.BOLD, 24));

        stageButton2 = new RoundButton("2");
        stageButton2.setPreferredSize(new Dimension(100, 100));
        stageButton2.setFont(new Font("Arial", Font.BOLD, 24));

        stageButton3 = new RoundButton("3");
        stageButton3.setPreferredSize(new Dimension(100, 100));
        stageButton3.setFont(new Font("Arial", Font.BOLD, 24));
    }

    private void layoutComponents() {
        JLabel titleLabel = new JLabel("STAGE SELECT");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 56));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);

        gbc.gridx = 0; gbc.gridy = 0;
        buttonPanel.add(stageButton1, gbc);

        gbc.gridx = 1;
        buttonPanel.add(stageButton2, gbc);

        gbc.gridx = 2;
        buttonPanel.add(stageButton3, gbc);

        add(titlePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    public RoundButton getStageButton1() { return stageButton1; }
    public RoundButton getStageButton2() { return stageButton2; }
    public RoundButton getStageButton3() { return stageButton3; }
    
}
