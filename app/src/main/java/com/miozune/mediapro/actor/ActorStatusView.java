package com.miozune.mediapro.actor;

import com.miozune.mediapro.util.ImageLoader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.function.BiFunction;
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

    private static final Dimension DEFAULT_SIZE = new Dimension(300, 320);

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

    private final JLabel nameLabel;
    private final JProgressBar hpBar;
    private final JLabel imageLabel;
    private static final int BASE_IMAGE_SIZE = 100;

    public ActorStatusView(Style style) {
        this.hpColorSupplier = style.hpBarColorSupplier();

        setPreferredSize(DEFAULT_SIZE);
        setMaximumSize(DEFAULT_SIZE);
        setOpaque(true);
        setBackground(style.background());

        nameLabel = new JLabel();
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setForeground(style.nameColor());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        hpBar = new JProgressBar(0, 100);
        hpBar.setStringPainted(true);
        hpBar.setPreferredSize(new Dimension(300, 24));
        hpBar.setMaximumSize(new Dimension(300, 24));
        hpBar.setBackground(style.barBackgroundColor());
        hpBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buildLayout();
    }

    private void buildLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(10));
        add(nameLabel);
        add(Box.createVerticalStrut(10));
        add(hpBar);
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
}
