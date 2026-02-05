package com.miozune.mediapro.actor;

import com.miozune.mediapro.status.StatusListView;
import com.miozune.mediapro.util.ImageLoader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.function.BiFunction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * 名前とHPを表示する共通ステータスビュー。
 */
public class ActorStatusView extends JPanel {

    public record Style(
        Color background,
        Color borderColor,
        Color nameColor,
        Color labelColor,
        Color barBackgroundColor,
        BiFunction<Integer, Integer, Color> hpBarColorSupplier
    ) {
        public Style {
            Objects.requireNonNull(background, "background");
            Objects.requireNonNull(borderColor, "borderColor");
            Objects.requireNonNull(nameColor, "nameColor");
            Objects.requireNonNull(labelColor, "labelColor");
            Objects.requireNonNull(barBackgroundColor, "barBackgroundColor");
            Objects.requireNonNull(hpBarColorSupplier, "hpBarColorSupplier");
        }
    }

    private static final Dimension DEFAULT_SIZE = new Dimension(300, 360);

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

    private final BiFunction<Integer, Integer, Color> hpColorSupplier;
    private final Style style;
    private final Color originalBackground;

    private final JLabel nameLabel;
    private final StatusListView statusListView;
    private final JProgressBar hpBar;
    private final JLabel imageLabel;
    private static final int BASE_IMAGE_SIZE = 100;

    /**
     * StatusListViewなしでActorStatusViewを作成する。
     * バフ・デバフ表示領域は確保されるが、空のまま。
     *
     * @param style スタイル設定
     */
    public ActorStatusView(Style style) {
        this(style, null);
    }

    /**
     * StatusListViewを含むActorStatusViewを作成する。
     * バフ・デバフ表示領域にstatusListViewを配置する。
     *
     * @param style スタイル設定
     * @param statusListView バフ・デバフ表示用のビュー（nullの場合は空の領域のみ確保）
     */
    public ActorStatusView(Style style, StatusListView statusListView) {
        this.hpColorSupplier = style.hpBarColorSupplier();
        this.style = style;
        this.originalBackground = style.background();
        this.statusListView = statusListView;

        setOpaque(false);
        setBackground(originalBackground);

        nameLabel = new JLabel();
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setForeground(style.nameColor());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        hpBar = new JProgressBar(0, 100);
        hpBar.setStringPainted(true);
        hpBar.setPreferredSize(new Dimension(280, 24));
        hpBar.setMaximumSize(new Dimension(280, 24));
        hpBar.setBackground(style.barBackgroundColor());
        hpBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buildLayout();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 背景色を手動で描画（アルファ値を考慮）
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();

        super.paintComponent(g);
    }

    private void buildLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(10));
        add(nameLabel);
        add(Box.createVerticalStrut(10));

        // バフ・デバフ表示領域（固定高さ52px確保）
        JPanel statusContainer = new JPanel();
        statusContainer.setOpaque(false);
        statusContainer.setPreferredSize(new Dimension(300, 52));
        statusContainer.setMaximumSize(new Dimension(300, 52));
        statusContainer.setMinimumSize(new Dimension(300, 52));
        statusContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (statusListView != null) {
            statusContainer.setLayout(new BoxLayout(statusContainer, BoxLayout.X_AXIS));
            statusContainer.add(statusListView);
        }

        add(statusContainer);
        add(Box.createVerticalStrut(10));

        // HPバーをコンテナでラップして左右に余白を追加
        JPanel hpBarContainer = new JPanel(new BorderLayout());
        hpBarContainer.setOpaque(false);
        hpBarContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        hpBarContainer.add(hpBar, BorderLayout.CENTER);
        hpBarContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        hpBarContainer.setMaximumSize(new Dimension(300, 24));
        add(hpBarContainer);

        add(Box.createVerticalStrut(15));
        add(imageLabel);
        add(Box.createVerticalStrut(10));
    }

    public void updateName(String name) {
        nameLabel.setText(name != null ? name : "Unknown");
    }

    public void updateHp(int hp, int maxHp) {
        int safeMax = Math.max(1, maxHp);
        int clampedHp = Math.max(0, Math.min(hp, safeMax));

        hpBar.setMaximum(safeMax);
        hpBar.setValue(clampedHp);
        hpBar.setString(String.format("%d / %d", clampedHp, safeMax));

        Color barColor = hpColorSupplier.apply(clampedHp, safeMax);
        hpBar.setUI(new ColoredProgressBarUI(barColor != null ? barColor : hpBar.getForeground()));
    }

    /**
     * 画像を更新する。
     * スケール係数に基づいて画像サイズを調整し、表示する。
     *
     * @param image 表示する画像（nullの場合は非表示）
     * @param scale スケール係数（例: 1.0で100x100、2.0で200x200）
     */
    public void updateImage(BufferedImage image, double scale) {
        if (image == null) {
            imageLabel.setIcon(null);
            imageLabel.setPreferredSize(new Dimension(0, 0));
            return;
        }

        int targetSize = (int) (BASE_IMAGE_SIZE * scale);
        BufferedImage scaledImage = ImageLoader.getScaledImage(image, targetSize, targetSize);

        if (scaledImage != null) {
            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.setPreferredSize(new Dimension(targetSize, targetSize));
        }
    }

    /**
     * 背景色のハイライト状態を更新する。
     * ターゲット選択時などで背景色を変更する際に使用する。
     *
     * @param highlighted trueの場合、青っぽい色にハイライト。falseの場合、元の背景色に戻す
     */
    public void updateBackgroundHighlight(boolean highlighted) {
        if (highlighted) {
            // 元の背景色を基に青っぽい色を計算
            Color highlightColor = createHighlightColor(originalBackground);
            setBackground(highlightColor);
        } else {
            setBackground(originalBackground);
        }
        repaint();
    }

    /**
     * 元の色を基にハイライト用の青っぽい色を生成する。
     * 色の明暗に応じて適切な青色変換を行い、透明度は維持する。
     */
    private Color createHighlightColor(Color base) {
        int r = base.getRed();
        int g = base.getGreen();
        int b = base.getBlue();
        int alpha = base.getAlpha();

        // 色の明暗を判定（平均値が128未満なら暗い色）
        boolean isDark = (r + g + b) < 384;

        int newR, newG, newB;
        if (isDark) {
            // 暗い色（敵など）: 青を強く加算
            newR = Math.max(0, Math.min(255, r));
            newG = Math.max(0, Math.min(255, g + 20));
            newB = Math.max(0, Math.min(255, b + 60));
        } else {
            // 明るい色（プレイヤーなど）: 赤と緑を減算し、青を加算
            newR = Math.max(0, r - 30);
            newG = Math.max(0, g - 30);
            newB = Math.max(0, Math.min(255, b + 20));
        }

        return new Color(newR, newG, newB, alpha);
    }
}
