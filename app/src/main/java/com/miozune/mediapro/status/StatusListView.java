package com.miozune.mediapro.status;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 付与中のステータスを横並びで表示するビュー。
 */
public class StatusListView extends JPanel {

    private static final Dimension BADGE_SIZE = new Dimension(90, 46);
    private static final Dimension LIST_SIZE = new Dimension(290, 52);

    public StatusListView() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setPreferredSize(LIST_SIZE);
        setMinimumSize(LIST_SIZE);
        setMaximumSize(LIST_SIZE);
    }

    /**
     * 現在のステータス一覧で表示を更新する。
     */
    public void updateStatuses(List<StatusEffect> effects) {
        removeAll();
        if (effects != null) {
            for (StatusEffect effect : effects) {
                add(createBadge(effect));
            }
        }
        revalidate();
        repaint();
    }

    private JPanel createBadge(StatusEffect effect) {
        String title = "ステータス";
        String detail = "";
        Color background = new Color(55, 60, 70, 200);
        Color border = new Color(90, 100, 115);
        Color text = Color.WHITE;

        if (effect instanceof ShieldStatus shield) {
            title = "シールド";
            if (shield.isPermanent()) {
                detail = String.format("+%d / ∞", shield.amount());
            } else {
                detail = String.format("+%d / あと1", shield.amount());
            }
            background = new Color(40, 90, 150, 200);
            border = new Color(70, 130, 190);
        } else if (effect instanceof StrengthStatus strength) {
            title = "筋力";
            detail = String.format("+%d / ∞", strength.bonus());
            background = new Color(140, 90, 30, 200);
            border = new Color(180, 130, 60);
        } else if (effect instanceof WeaknessStatus weakness) {
            title = "弱体化";
            detail = String.format("+%d被ダメ / あと%d", weakness.bonusDamage(), weakness.remainingTurns());
            background = new Color(110, 60, 120, 200);
            border = new Color(150, 100, 170);
        } else if (effect != null) {
            title = effect.getClass().getSimpleName();
        }

        JPanel badge = new JPanel();
        badge.setOpaque(true);
        badge.setBackground(background);
        badge.setPreferredSize(BADGE_SIZE);
        badge.setMaximumSize(BADGE_SIZE);
        badge.setLayout(new BoxLayout(badge, BoxLayout.Y_AXIS));
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(text);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        JLabel detailLabel = new JLabel(detail);
        detailLabel.setForeground(text);
        detailLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        badge.add(titleLabel);
        badge.add(detailLabel);
        return badge;
    }
}
