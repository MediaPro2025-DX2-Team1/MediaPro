package com.miozune.mediapro.title;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TitleView extends JPanel {

    private JButton startButton;

    public TitleView() {
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
        startButton = new JButton("START");
        startButton.setFont(new Font("Arial", Font.BOLD, 32));
        startButton.setPreferredSize(new Dimension(180, 60));
        startButton.setForeground(Color.BLACK);
        startButton.setBackground(Color.WHITE);
        startButton.setOpaque(true);
        startButton.setBorderPainted(true);
        startButton.setContentAreaFilled(true);
        startButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        startButton.setFocusPainted(false);
        
        addMouseHoverEffect(startButton);
    }

    private void addMouseHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(220, 220, 220));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
    }

    private void layoutComponents() {
        JLabel titleLabel = new JLabel("TITLE NAME");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 56));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        add(titleLabel, BorderLayout.CENTER);

        JPanel buttonpanel = new JPanel(new GridBagLayout());
        buttonpanel.setBackground(new Color(240, 240, 240)); 
        buttonpanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 60, 0));
        buttonpanel.add(startButton);

        add(buttonpanel, BorderLayout.SOUTH);
    }

    public JButton getStartButton() {
        return startButton;
    }
}
