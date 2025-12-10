package com.miozune.mediapro.title;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TitleView extends JFrame {

    private JButton startButton;

    public TitleView() {
        setupFrame();
        initComponents();
        layoutComponents();
    }

    private void setupFrame() {
        setTitle("Title Screen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false); 
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

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240)); 

        panel.add(titleLabel, BorderLayout.CENTER);

        JPanel buttonpanel = new JPanel(new GridBagLayout());
        buttonpanel.setBackground(new Color(240, 240, 240)); 
        buttonpanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 60, 0)); // 上部20px、下部60pxの余白
        buttonpanel.add(startButton);

        panel.add(buttonpanel, BorderLayout.SOUTH);

        add(panel);
    }

    public JButton getStartButton() {
        return startButton;
    }
}
