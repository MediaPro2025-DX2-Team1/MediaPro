package com.miozune.mediapro.ui.overlay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;

public final class OverlayPanels {

    private OverlayPanels() {
    }

    /**
     * 半透明の背景を描画し、背景クリックで指定のハンドラを呼び出すパネルを生成する。
     */
    public static JPanel backdrop(JComponent content, Runnable onBackgroundClick) {
        JPanel backdrop = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backdrop.setOpaque(false);
        if (onBackgroundClick != null) {
            backdrop.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (backdrop.getComponentAt(e.getPoint()) == backdrop) {
                        onBackgroundClick.run();
                    }
                }
            });
        }
        if (content != null) {
            // コンテンツ側でイベントを消費させて背景クリックと区別する
            content.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    e.consume();
                }
            });
            backdrop.add(content);
        }
        return backdrop;
    }
}
