package com.miozune.mediapro.util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * ボタンに統一的なスタイル（カスタム画像背景、ホバー効果、無効状態の視覚効果）を適用するユーティリティクラス。
 *
 * <p>使用方法:
 * <pre>{@code
 * JButton button = new JButton("テキスト");
 * ButtonStyler.applyStyle(button);
 * }</pre>
 */
public final class ButtonStyler {

    /** ボタン背景に使用する画像ファイル名 */
    private static final String BUTTON_IMAGE_FILE = "stone.png";

    /** ホバー時のオーバーレイ色（半透明の白） */
    private static final Color HOVER_OVERLAY = new Color(255, 255, 255, 60);

    /** 無効時のオーバーレイ色（半透明の黒） */
    private static final Color DISABLED_OVERLAY = new Color(0, 0, 0, 120);

    private ButtonStyler() {}

    /**
     * ボタンにデフォルトスタイルを適用する。
     * stone.png を背景画像として使用し、ホバー時と無効時の視覚効果を追加する。
     *
     * @param button スタイルを適用するボタン
     */
    public static void applyStyle(JButton button) {
        if (button == null) {
            return;
        }

        // 背景画像の読み込み
        BufferedImage backgroundImage = ImageLoader.loadButtonImage(BUTTON_IMAGE_FILE);
        if (backgroundImage == null) {
            System.err.println("Warning: Button background image not found: " + BUTTON_IMAGE_FILE);
            return;
        }

        // ボタンの基本設定
        button.setContentAreaFilled(false); // デフォルト背景を無効化
        button.setBorderPainted(false); // デフォルトボーダーを無効化
        button.setFocusPainted(false); // フォーカス枠を無効化
        button.setOpaque(false); // 透過設定
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // カーソルを手の形に
        button.setForeground(Color.WHITE); // テキスト色を白に設定

        // マージンを設定してテキストが画像端に接触しないようにする
        button.setMargin(new Insets(5, 15, 5, 15));

        // ホバー状態を追跡するための状態管理
        final boolean[] isHovered = {false};

        // マウスイベントリスナーでホバー状態を管理
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    isHovered[0] = true;
                    button.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered[0] = false;
                button.repaint();
            }
        });

        // カスタム描画処理を実装
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton btn = (JButton) c;
                Graphics2D g2d = (Graphics2D) g.create();

                try {
                    // アンチエイリアス設定
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                    // ボタンのサイズを取得
                    int width = btn.getWidth();
                    int height = btn.getHeight();

                    // 背景画像を描画（ボタンサイズに合わせて拡縮）
                    g2d.drawImage(backgroundImage, 0, 0, width, height, null);

                    // 無効状態の場合、暗いオーバーレイを描画
                    if (!btn.isEnabled()) {
                        g2d.setColor(DISABLED_OVERLAY);
                        g2d.fillRect(0, 0, width, height);
                    }
                    // ホバー状態の場合、白いオーバーレイを描画
                    else if (isHovered[0]) {
                        g2d.setColor(HOVER_OVERLAY);
                        g2d.fillRect(0, 0, width, height);
                    }

                } finally {
                    g2d.dispose();
                }

                // テキストを黒縁取り付き白文字で描画
                paintTextWithOutline(g, btn);

                // アイコンのみ親クラスで描画（テキストは上で描画済み）
                String originalText = btn.getText();
                btn.setText(null); // 一時的にテキストを非表示
                super.paint(g, c);
                btn.setText(originalText); // テキストを復元
            }

            /**
             * テキストを黒縁取り付き白文字で描画する
             */
            private void paintTextWithOutline(Graphics g, JButton btn) {
                String text = btn.getText();
                if (text == null || text.isEmpty()) {
                    return;
                }

                Graphics2D g2d = (Graphics2D) g.create();
                try {
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2d.setFont(btn.getFont());

                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(text);

                    // テキストの中央配置を計算
                    int x = (btn.getWidth() - textWidth) / 2;
                    int y = (btn.getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                    // 黒い縁取りを描画（8方向にずらして描画）
                    g2d.setColor(Color.BLACK);
                    int outlineWidth = 2;
                    for (int dx = -outlineWidth; dx <= outlineWidth; dx++) {
                        for (int dy = -outlineWidth; dy <= outlineWidth; dy++) {
                            if (dx != 0 || dy != 0) {
                                g2d.drawString(text, x + dx, y + dy);
                            }
                        }
                    }

                    // 白いテキストを描画
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(text, x, y);

                } finally {
                    g2d.dispose();
                }
            }

            @Override
            public Dimension getPreferredSize(JComponent c) {
                // テキストとマージンを考慮したサイズを計算
                Dimension size = super.getPreferredSize(c);
                // 最小サイズを確保
                return new Dimension(Math.max(size.width, 100), Math.max(size.height, 40));
            }
        });

        // 初回描画をトリガー
        button.revalidate();
        button.repaint();
    }
}
