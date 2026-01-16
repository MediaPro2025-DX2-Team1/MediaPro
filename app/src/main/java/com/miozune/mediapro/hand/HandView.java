package com.miozune.mediapro.hand;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardView;
import com.miozune.mediapro.hand.events.HandCardChangedEvent;
import com.miozune.mediapro.preview.Previewable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class HandView extends JPanel implements Previewable {

    public interface HandActionListener {
        void onCardLeftClick(CardModel card);
        void onCardRightClick(CardModel card);
    }

    // --- 定数定義 ---
    // ホバー時に浮き上がる距離（ピクセル）
    private static final int HOVER_OFFSET = 20;

    // カード配置計算用の定数
    private static final int CARD_WIDTH = 100;  // カードの幅 (CardViewに合わせて調整してください)
    private static final int CARD_HEIGHT = 140; // カードの高さ
    private static final int CARD_GAP = 10;     // 重ならない場合のカード間の隙間
    private static final int BASE_Y = 20;       // カード配置の基準Y座標（上からの距離）

    // --- フィールド変数 ---
    private final HandModel model;
    private JLayeredPane layeredPane;
    private JPanel detailPanel;
    private JPanel cardListPanel;
    private HandActionListener actionListener;
    private HandModel.PropertyChangeListener modelListener;

    // 生成したカードのコンポーネントを保持するリスト
    private final List<JPanel> cardComponentList = new ArrayList<>();

    // --- コンストラクタ ---
    public HandView() {
        this(HandModel.createDefaultHand());
    }

    public HandView(HandModel model) {
        this.model = model;
        setupPanels();
        createDetailPanel();
        setupModelListener();
        updateHand(model.getCards());
    }

    // --- 初期化メソッド ---
    private void setupPanels() {
        setPreferredSize(new Dimension(800, 260));
        setOpaque(false);
        setLayout(new BorderLayout());

        // レイヤーペインの初期化
        layeredPane = new JLayeredPane();
        add(layeredPane, BorderLayout.CENTER);

        // カード一覧エリア
        cardListPanel = new JPanel(null);
        cardListPanel.setOpaque(false);
        
        layeredPane.add(cardListPanel, JLayeredPane.DEFAULT_LAYER);

        // リサイズ時にレイヤーサイズ調整とカード再配置を行う
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLayerBounds();
                layoutCards();
            }
        });
    }

    // パネルサイズの同期
    private void updateLayerBounds() {
        Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
        if (detailPanel != null) {
            detailPanel.setBounds(bounds);
        }
        if (cardListPanel != null) {
            cardListPanel.setBounds(bounds);
        }
        revalidate();
        repaint();
    }
    
    // 拡大表示用のパネル作成
    private void createDetailPanel() {
        detailPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        detailPanel.setOpaque(false);

        // 背景クリックで閉じる
        detailPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hideCardDetail();
            }
        });

        detailPanel.setVisible(false);
        detailPanel.setBounds(0, 0, 800, 260);
        layeredPane.add(detailPanel, JLayeredPane.MODAL_LAYER);
    }

    // カード重ねるためのレイアウト調整
    private void layoutCards() {
        if (cardComponentList.isEmpty()) return;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int cardCount = cardComponentList.size();
        int totalWidthNeeded = (CARD_WIDTH * cardCount) + (CARD_GAP * (cardCount - 1));

        int stepX; // カード1枚ごとのずらし幅
        int startX;   // 描画開始位置(X)

        // --- 分岐ロジック ---
        if (totalWidthNeeded <= panelWidth) { // パネルにカードが収まる場合
            stepX = CARD_WIDTH + CARD_GAP;
        
            // コンテンツの実幅を計算
            int actualContentWidth;
            if (cardCount > 1) {
                actualContentWidth = (stepX * (cardCount - 1)) + CARD_WIDTH;
            } else {
                actualContentWidth = CARD_WIDTH;
            }

            startX = (panelWidth - actualContentWidth) / 2;
        } else { // カードが重なる場合
            if (cardCount > 1) {
                stepX = (panelWidth - CARD_WIDTH) / (cardCount - 1);
            } else {
                stepX = 0;
            }
            
            startX = 0;
        }

        // --- 配置ループ ---
        for (int i = 0; i < cardCount; i++) {
            JPanel wrapper = cardComponentList.get(i);
            int x = startX + (i * stepX);
            
            wrapper.setBounds(x, BASE_Y, panelWidth, panelHeight);
            cardListPanel.setComponentZOrder(wrapper, cardCount - 1 - i);
        }
    
        cardListPanel.revalidate();
        cardListPanel.repaint();
    }

    // --- カード生成と更新 ---
    public void updateHand(List<CardModel> cards) {
        cardListPanel.removeAll();
        cardComponentList.clear(); 
        
        for (CardModel cardModel : cards) {
            CardView cardView = new CardView(cardModel);

            // 1. ラッパーパネルの作成
            JPanel wrapper = new JPanel(null);
            wrapper.setOpaque(false);
            
            Dimension cardSize = cardView.getPreferredSize();
            if (cardSize.width == 0) cardSize = new Dimension(CARD_WIDTH, CARD_HEIGHT); 

            wrapper.setPreferredSize(new Dimension(cardSize.width, cardSize.height + HOVER_OFFSET));
            wrapper.setSize(wrapper.getPreferredSize());

            // 2. カードの初期位置（下に配置＝沈んでいる通常状態）
            cardView.setBounds(0, HOVER_OFFSET, cardSize.width, cardSize.height);
            wrapper.add(cardView);

            // 3. マウスリスナーの追加（ホバー動作 ＋ クリック動作）
            cardView.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    cardView.setLocation(0, 0);
                    wrapper.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setCursor(Cursor.getDefaultCursor());
                    cardView.setLocation(0, HOVER_OFFSET);
                    wrapper.repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (actionListener == null) return;

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        actionListener.onCardLeftClick(cardModel);
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        actionListener.onCardRightClick(cardModel);
                    }
                }
            });

            cardListPanel.add(wrapper);
            cardComponentList.add(wrapper);
        }
        
        // 最後に配置計算を実行
        layoutCards();
    }

    // --- 詳細表示メソッド ---
    public void showCardDetail(CardModel cardModel) {
        detailPanel.removeAll();
        CardView bigCardView = new CardView(cardModel);
        bigCardView.setPreferredSize(new Dimension(300, 420)); 

        // 詳細カードクリック時はイベントを消費（背後のカードをクリックしないように）
        bigCardView.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseClicked(MouseEvent e) {
                 e.consume();
             }
        });
        
        detailPanel.add(bigCardView);
        detailPanel.setVisible(true);
        detailPanel.revalidate();
        detailPanel.repaint();
    }
    
    public void hideCardDetail() {
        detailPanel.setVisible(false);
        detailPanel.removeAll(); 
    }

    // --- リスナー設定 ---
    public void setHandActionListener(HandActionListener listener) {
        this.actionListener = listener;
    }

    private void setupModelListener() {
        modelListener = event -> {
            if (event instanceof HandCardChangedEvent e) {
                updateHand(e.newcards());
            }
        };
        model.addPropertyChangeListener(modelListener);
    }

    // --- Previewable実装 ---
    @Override
    public String getPreviewDescription() {
        return "手札プレビュー";
    }

    @Override
    public void setupPreview() {
        // 1. プレビュー用のデータを作成
        HandModel previewModel = HandModel.createDefaultHand();

        // 2. プレビュー用のリスナーをセット
        setHandActionListener(new HandActionListener() {
            @Override
            public void onCardLeftClick(CardModel card) {
                System.out.println("[Preview] left clicked");
            }

            @Override
            public void onCardRightClick(CardModel card) {
                System.out.println("[Preview] right clicked");
                showCardDetail(card);
            }
        });

        // 3. 画面更新
        updateHand(previewModel.getCards());
        updateLayerBounds();
    }
}