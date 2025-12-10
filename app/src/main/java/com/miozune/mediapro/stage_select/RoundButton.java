package com.miozune.mediapro.stage_select;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundButton extends JButton {

    private boolean isHovered = false;

    public RoundButton(String label) {
        super(label);
        setFocusable(false);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(new Color(200, 200, 200));
        } else if (isHovered) {
            g.setColor(new Color(230, 230, 230));
        } else {
            g.setColor(Color.WHITE);
        }

        g.fillOval(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 4;
        g.drawString(getText(), x, y);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
    }

    @Override
    public boolean contains(int x, int y) {
        int radius = getWidth() / 2;
        int centerX = radius;
        int centerY = radius;

        return Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2);
    }
}

