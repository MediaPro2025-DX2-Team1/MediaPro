package com.miozune.mediapro.stage;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardView;
import com.miozune.mediapro.card.events.CardClickListener;
import com.miozune.mediapro.card.overlay.CardDetailOverlay;
import com.miozune.mediapro.card.overlay.CardListOverlay;
import com.miozune.mediapro.enemy.EnemyModel;
import com.miozune.mediapro.enemy.EnemyView;
import com.miozune.mediapro.hand.HandView;
import com.miozune.mediapro.player.PlayerModel;
import com.miozune.mediapro.player.PlayerView;
import com.miozune.mediapro.player.events.PlayerManaChangedEvent;
import com.miozune.mediapro.preview.Previewable;
import com.miozune.mediapro.stage.events.BattleResultOkEvent;
import com.miozune.mediapro.stage.events.OverlayClosedEvent;
import com.miozune.mediapro.stage.events.OverlayEvent;
import com.miozune.mediapro.ui.overlay.OverlayLayer;
import com.miozune.mediapro.ui.overlay.OverlayPanels;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;
import javax.swing.border.Border;

public class StageView extends JPanel implements Previewable {

    private static final int HAND_MIN_HEIGHT = CardView.DEFAULT_HEIGHT + 20;
    private static final int SELECTION_BANNER_HEIGHT = 36;

    private final JLayeredPane layeredPane;
    private final JPanel mainContentPanel;
    private final OverlayLayer overlayLayer;

    private final JPanel topPanel;
    private final JPanel bottomPanel;
    private final JPanel selectionBanner;
    private final JLabel selectionLabel;

    private final HandView handView;
    private final JPanel handWrapper;
    private final JPanel actionPanel;
    private final JPanel handContainer;
    private final JPanel manaWrapper;
    private final ManaBadge manaBadge;
    private final JPanel playerContainer;
    private final JPanel enemyGrid;

    private final JButton deckButton;
    private final JButton discardButton;
    private final JButton endTurnButton;

    private PlayerView playerView;
    private final Map<EnemyModel, JPanel> enemyPanels = new HashMap<>();
    private boolean selectingTarget;
    private EnemyClickListener enemyClickListener;
    private BackgroundClickListener backgroundClickListener;
    private PlayerModel playerModel;
    private PlayerModel.PropertyChangeListener playerListener;
    private final List<OverlayListener> overlayListeners = new CopyOnWriteArrayList<>();

    public StageView() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 700));
        setBackground(new Color(30, 30, 30));

        // JLayeredPaneの初期化
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1000, 700));
        add(layeredPane, BorderLayout.CENTER);

        // メインコンテンツパネル
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        overlayLayer = new OverlayLayer();

        // リサイズ時のレイヤー更新
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLayerBounds();
            }
        });

        /* --- 上部：戦闘画面エリア --- */
        topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        selectionBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        selectionBanner.setOpaque(false);
        selectionBanner.setPreferredSize(new Dimension(0, SELECTION_BANNER_HEIGHT));
        selectionBanner.setMinimumSize(new Dimension(0, SELECTION_BANNER_HEIGHT));
        selectionBanner.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        selectionLabel = new JLabel();
        selectionLabel.setForeground(new Color(200, 230, 255));
        selectionLabel.setFont(selectionLabel.getFont().deriveFont(Font.BOLD, 14f));
        selectionBanner.add(selectionLabel);
        selectionBanner.setVisible(true);

        playerContainer = new JPanel(new BorderLayout());
        playerContainer.setOpaque(false);

        enemyGrid = new JPanel(new GridLayout(0, 3, 20, 20));
        enemyGrid.setOpaque(false);
        enemyGrid.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        enemyGrid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!selectingTarget || backgroundClickListener == null) {
                    return;
                }
                Component target = enemyGrid.getComponentAt(e.getPoint());
                if (target == enemyGrid) {
                    backgroundClickListener.onBackgroundClicked();
                }
            }
        });

        topPanel.add(selectionBanner, BorderLayout.NORTH);
        topPanel.add(playerContainer, BorderLayout.WEST);
        topPanel.add(enemyGrid, BorderLayout.CENTER);

        /* --- 下部：操作エリア --- */
        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        // 左側：手札
        handView = new HandView();
        handWrapper = new JPanel(new BorderLayout());
        handWrapper.setBackground(new Color(45, 45, 45));
        handWrapper.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        handWrapper.setOpaque(true);
        handWrapper.setMinimumSize(new Dimension(0, HAND_MIN_HEIGHT));
        handWrapper.setPreferredSize(new Dimension(0, HAND_MIN_HEIGHT));
        handWrapper.add(handView, BorderLayout.CENTER);
        manaBadge = new ManaBadge();
        manaWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        manaWrapper.setOpaque(false);
        manaWrapper.add(manaBadge);

        // 右側：アクションボタン（3分割して埋める）
        actionPanel = new JPanel(new GridLayout(3, 1, 0, 5)); // 縦に3つ並べる
        actionPanel.setPreferredSize(new Dimension(200, 0));
        actionPanel.setBackground(new Color(50, 50, 50));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        deckButton = new JButton("山札確認");
        discardButton = new JButton("捨札確認");
        endTurnButton = new JButton("ターン終了");

        // ボタンのフォントと見た目の微調整
        Font btnFont = new Font("SansSerif", Font.BOLD, 16);
        for (JButton btn : new JButton[] { deckButton, discardButton, endTurnButton }) {
            btn.setFont(btnFont);
            btn.setFocusPainted(false);
            actionPanel.add(btn);
        }

        handContainer = new JPanel(new BorderLayout());
        handContainer.setOpaque(false);
        handContainer.add(manaWrapper, BorderLayout.NORTH);
        handContainer.add(handWrapper, BorderLayout.CENTER);

        bottomPanel.add(handContainer, BorderLayout.CENTER);
        bottomPanel.add(actionPanel, BorderLayout.EAST);

        mainContentPanel.add(topPanel, BorderLayout.CENTER);
        mainContentPanel.add(bottomPanel, BorderLayout.SOUTH);

        // レイヤー構造の構築
        layeredPane.add(mainContentPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(overlayLayer, JLayeredPane.PALETTE_LAYER);

        setupKeyBindings();
    }

    /** レイヤーのサイズを親に合わせて更新 */
    private void updateLayerBounds() {
        int width = getWidth();
        int height = getHeight();
        layeredPane.setBounds(0, 0, width, height);
        mainContentPanel.setBounds(0, 0, width, height);
        overlayLayer.setBounds(0, 0, width, height);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        updateLayerBounds();
    }

    public void setActors(PlayerModel player, List<EnemyModel> enemies) {
        if (playerListener != null && playerModel != null) {
            playerModel.removePropertyChangeListener(playerListener);
        }
        playerModel = player;
        if (player == null) {
            throw new IllegalArgumentException("PlayerModel cannot be null");
        }
        playerView = new PlayerView(player);
        playerContainer.removeAll();
        playerContainer.add(playerView, BorderLayout.NORTH);
        setupPlayerListener();
        updateManaDisplay(player.getMana());

        updateEnemies(enemies);
        exitTargetSelection();
        revalidate();
        repaint();
    }

    public void refreshEnemies(List<EnemyModel> enemies) {
        updateEnemies(enemies);
        revalidate();
        repaint();
    }

    private void updateEnemies(List<EnemyModel> enemies) {
        enemyGrid.removeAll();
        enemyPanels.clear();
        if (enemies == null || enemies.isEmpty()) {
            enemyGrid.revalidate();
            enemyGrid.repaint();
            return;
        }
        for (EnemyModel enemy : enemies) {
            if (enemy == null) {
                continue;
            }
            JPanel panel = createEnemyPanel(enemy);
            enemyPanels.put(enemy, panel);
            enemyGrid.add(panel);
        }
        enemyGrid.revalidate();
        enemyGrid.repaint();
    }

    private JPanel createEnemyPanel(EnemyModel enemy) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(createEnemyBorder(false));
        EnemyView view = new EnemyView(enemy);
        wrapper.add(view, BorderLayout.CENTER);

        wrapper.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                handleEnemyHover(enemy, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                handleEnemyHover(enemy, false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (enemyClickListener != null) {
                    enemyClickListener.onEnemyClicked(enemy);
                }
            }
        });
        return wrapper;
    }

    private void setupPlayerListener() {
        if (playerModel == null) {
            return;
        }
        playerListener = event -> {
            if (event instanceof PlayerManaChangedEvent manaEvent) {
                updateManaDisplay(manaEvent.newMana());
            }
        };
        playerModel.addPropertyChangeListener(playerListener);
    }

    private void updateManaDisplay(int mana) {
        if (playerModel == null) {
            return;
        }
        int maxMana = playerModel.getMaxMana();
        manaBadge.setValues(mana, maxMana);
    }

    private void handleEnemyHover(EnemyModel enemy, boolean entered) {
        JPanel panel = enemyPanels.get(enemy);
        if (panel == null) {
            return;
        }
        if (!selectingTarget) {
            panel.setBorder(createEnemyBorder(false));
            return;
        }
        panel.setBorder(createEnemyBorder(entered));
    }

    private void updateSelectionUiState() {
        Cursor cursor = selectingTarget
            ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            : Cursor.getDefaultCursor();
        enemyGrid.setCursor(cursor);
        enemyPanels.values().forEach(panel -> {
            panel.setCursor(cursor);
            panel.setBorder(createEnemyBorder(false));
        });
        if (selectingTarget) {
            selectionBanner.setOpaque(true);
            selectionBanner.setBackground(new Color(60, 90, 140, 80));
            selectionLabel.setText("敵をクリックして攻撃対象を選んでください (Escでキャンセル)");
            requestFocusInWindow();
        } else {
            selectionBanner.setOpaque(false);
            selectionBanner.setBackground(new Color(0, 0, 0, 0));
            selectionLabel.setText("");
        }
    }

    private Border createEnemyBorder(boolean highlighted) {
        Color color = highlighted ? new Color(110, 150, 220) : new Color(80, 80, 80);
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
    }

    public void enterTargetSelection() {
        selectingTarget = true;
        updateSelectionUiState();
    }

    public void exitTargetSelection() {
        selectingTarget = false;
        updateSelectionUiState();
    }

    private void setupKeyBindings() {
        setFocusable(true);
        ActionMap actionMap = getActionMap();
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancelAction");
        actionMap.put("cancelAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // オーバーレイが表示されていて、ESCで閉じられる場合のみ閉じる
                if (overlayLayer.isVisible() && overlayLayer.isTopCloseableByEsc()) {
                    hideOverlay();
                    return;
                }
                // ターゲット選択中ならキャンセル
                if (selectingTarget && backgroundClickListener != null) {
                    backgroundClickListener.onBackgroundClicked();
                }
            }
        });
    }

    public void setEnemyClickListener(EnemyClickListener listener) {
        this.enemyClickListener = listener;
    }

    public void setBackgroundClickListener(BackgroundClickListener listener) {
        this.backgroundClickListener = listener;
    }

    private static class ManaBadge extends JComponent {
        private int mana;
        private int maxMana = 1;

        private static final int SIZE = 64;
        private static final Color FILL = new Color(50, 150, 220);
        private static final Color BORDER = new Color(20, 70, 140);
        private static final Color TEXT = Color.WHITE;

        ManaBadge() {
            setPreferredSize(new Dimension(SIZE, SIZE));
            setMinimumSize(new Dimension(SIZE, SIZE));
            setMaximumSize(new Dimension(SIZE, SIZE));
            setOpaque(false);
        }

        void setValues(int mana, int maxMana) {
            this.mana = mana;
            this.maxMana = Math.max(1, maxMana);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(FILL);
            g2.fillOval(x, y, size, size);

            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x, y, size, size);

            String text = String.format("%d/%d", mana, maxMana);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int tx = getWidth() / 2 - textWidth / 2;
            int ty = getHeight() / 2 + textHeight / 2 - 4;
            g2.setColor(TEXT);
            g2.drawString(text, tx, ty);

            g2.dispose();
        }
    }

    public void updateHand(List<CardModel> cards, CardClickListener clickListener) {
        handView.setCardClickListener(clickListener);
        handView.updateHand(cards);
    }

    /** カードの拡大表示をオーバーレイに表示 */
    public void showCardDetail(CardModel card) {
        CardDetailOverlay content = new CardDetailOverlay(card);
        overlayLayer.push(OverlayPanels.backdrop(content, this::hideOverlay));
    }

    /** 山札一覧をオーバーレイに表示 */
    public void showDrawPile(List<CardModel> drawPileCards) {
        CardListOverlay content = new CardListOverlay(
            "山札一覧",
            drawPileCards,
            () -> {
                hideOverlay();
                fireOverlayEvent(new OverlayClosedEvent());
            },
            this::showCardDetail
        );
        overlayLayer.push(OverlayPanels.backdrop(content, () -> {
            hideOverlay();
            fireOverlayEvent(new OverlayClosedEvent());
        }));
    }

    /** 捨て札一覧をオーバーレイに表示 */
    public void showDiscardPile(List<CardModel> discardCards) {
        CardListOverlay content = new CardListOverlay(
            "捨札一覧",
            discardCards,
            () -> {
                hideOverlay();
                fireOverlayEvent(new OverlayClosedEvent());
            },
            this::showCardDetail
        );
        overlayLayer.push(OverlayPanels.backdrop(content, () -> {
            hideOverlay();
            fireOverlayEvent(new OverlayClosedEvent());
        }));
    }

    /** 勝敗結果をオーバーレイに表示 */
    public void showBattleResult(boolean isVictory) {
        clearAllOverlays();

        JPanel resultPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bgColor = isVictory ? new Color(30, 60, 30, 240) : new Color(60, 30, 30, 240);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        resultPanel.setOpaque(false);
        resultPanel.setPreferredSize(new Dimension(400, 200));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // 結果メッセージ
        String message = isVictory ? "勝利しました！" : "敗北しました...";
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Meiryo", Font.BOLD, 32));
        messageLabel.setForeground(Color.WHITE);
        resultPanel.add(messageLabel, BorderLayout.CENTER);

        // OKボタン
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Meiryo", Font.BOLD, 18));
        okButton.setPreferredSize(new Dimension(120, 40));
        okButton.addActionListener(e -> {
            hideOverlay();
            fireOverlayEvent(new BattleResultOkEvent(isVictory));
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);
        resultPanel.add(buttonPanel, BorderLayout.SOUTH);

        // イベント消費
        resultPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });

        overlayLayer.push(OverlayPanels.backdrop(resultPanel, null), false);
        requestFocusInWindow();
    }

    /** オーバーレイを1階層閉じる */
    public void hideOverlay() {
        overlayLayer.pop();
    }

    /** 全てのオーバーレイをクリア */
    private void clearAllOverlays() {
        overlayLayer.clearAll();
    }

    /* Previewableインターフェースの実装 */
    @Override
    public String getPreviewDescription() {
        return "戦闘画面のテストビューです。";
    }

    @Override
    public void setupPreview() {
        PlayerModel player = PlayerModel.createDefaultPlayer();
        player.setHp(80);
        player.setMana(3);
        EnemyModel enemyA = EnemyModel.createDefault();
        enemyA.setHp(50);
        EnemyModel enemyB = EnemyModel.createDefault();
        enemyB.setHp(70);
        setActors(player, List.of(enemyA, enemyB));
    }

    // Getter
    public JButton getDeckButton() {
        return deckButton;
    }

    public JButton getDiscardButton() {
        return discardButton;
    }

    public JButton getEndTurnButton() {
        return endTurnButton;
    }

    @FunctionalInterface
    public interface EnemyClickListener {
        void onEnemyClicked(EnemyModel enemy);
    }

    public interface BackgroundClickListener {
        void onBackgroundClicked();
    }

    /** オーバーレイイベントのリスナーインターフェース */
    @FunctionalInterface
    public interface OverlayListener {
        void onOverlayEvent(OverlayEvent event);
    }

    /** オーバーレイリスナーを追加 */
    public void addOverlayListener(OverlayListener listener) {
        if (listener != null) {
            overlayListeners.add(listener);
        }
    }

    /** オーバーレイリスナーを削除 */
    public void removeOverlayListener(OverlayListener listener) {
        overlayListeners.remove(listener);
    }

    /** オーバーレイイベントを発行 */
    private void fireOverlayEvent(OverlayEvent event) {
        if (event == null) return;
        for (OverlayListener listener : overlayListeners) {
            listener.onOverlayEvent(event);
        }
    }
}
