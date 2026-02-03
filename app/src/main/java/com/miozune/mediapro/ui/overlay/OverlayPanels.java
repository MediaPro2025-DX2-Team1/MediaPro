package com.miozune.mediapro.ui.overlay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;

public final class OverlayPanels {

    private OverlayPanels() {}

    /**
     * 半透明の背景を描画し、背景クリックで指定のハンドラを呼び出すパネルを生成する。
     * 下層のUIへのマウスイベント通過を防ぐため、onBackgroundClickがnullでも常にMouseListenerを登録する。
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

        // マウスイベントを消費して下層への通過を防ぐ
        backdrop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (backdrop.getComponentAt(e.getPoint()) == backdrop) {
                    if (onBackgroundClick != null) {
                        onBackgroundClick.run();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                e.consume();
            }
        });

        // ホバーイベントも消費して完全にブロック
        backdrop.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                e.consume();
            }
        });

        if (content != null) {
            backdrop.add(content);
        }
        return backdrop;
    }
}
