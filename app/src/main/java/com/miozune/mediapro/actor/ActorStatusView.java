package com.miozune.mediapro.actor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Objects;
import java.util.function.BiFunction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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

    private static final Dimension DEFAULT_SIZE = new Dimension(400, 180);

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
    private final JLabel hpLabel;
    private final JProgressBar hpBar;

    public ActorStatusView(Style style) {
        this.hpColorSupplier = style.hpBarColorSupplier();

        setPreferredSize(DEFAULT_SIZE);
        setMaximumSize(DEFAULT_SIZE);
        setOpaque(true);
        setBackground(style.background());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(style.borderColor(), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        nameLabel = new JLabel();
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setForeground(style.nameColor());

        hpLabel = new JLabel();
        hpLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        hpLabel.setForeground(style.labelColor());
        hpLabel.setHorizontalAlignment(SwingConstants.LEFT);

        hpBar = new JProgressBar(0, 100);
        hpBar.setStringPainted(true);
        hpBar.setPreferredSize(new Dimension(300, 24));
        hpBar.setMaximumSize(new Dimension(300, 24));
        hpBar.setBackground(style.barBackgroundColor());

        buildLayout();
    }

    private void buildLayout() {
        setLayout(new BorderLayout(10, 10));
        add(nameLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        centerPanel.add(hpLabel);
        centerPanel.add(Box.createVerticalStrut(5));

        JPanel barWrapper = new JPanel();
        barWrapper.setLayout(new BoxLayout(barWrapper, BoxLayout.X_AXIS));
        barWrapper.setOpaque(false);
        barWrapper.add(hpBar);
        barWrapper.add(Box.createHorizontalGlue());

        centerPanel.add(barWrapper);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);
    }

    public void updateName(String name) {
        nameLabel.setText(name != null ? name : "Unknown");
    }

    public void updateHp(int hp, int maxHp) {
        int safeMax = Math.max(1, maxHp);
        int clampedHp = Math.max(0, Math.min(hp, safeMax));

        hpLabel.setText(String.format("HP: %d / %d", clampedHp, safeMax));

        hpBar.setMaximum(safeMax);
        hpBar.setValue(clampedHp);
        hpBar.setString(String.format("%d / %d", clampedHp, safeMax));

        Color barColor = hpColorSupplier.apply(clampedHp, safeMax);
        hpBar.setUI(new ColoredProgressBarUI(barColor != null ? barColor : hpBar.getForeground()));
    }
}
