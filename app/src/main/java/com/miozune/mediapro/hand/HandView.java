package com.miozune.mediapro.hand;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardView;
import com.miozune.mediapro.card.events.CardClickListener;
import com.miozune.mediapro.card.events.CardClickedEvent;
import com.miozune.mediapro.card.events.ClickType;
import com.miozune.mediapro.hand.events.HandCardChangedEvent;
import com.miozune.mediapro.preview.Previewable;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class HandView extends JPanel implements Previewable {

    public interface HandActionListener {
        void onCardLeftClick(CardModel card);
        void onCardRightClick(CardModel card);
    }

    // --- 定数定義 ---
    // ホバー時に浮き上がる距離（ピクセル）
    private static final int HOVER_OFFSET = 20;

    // カード配置計算用の定数
    private static final int CARD_WIDTH = CardView.DEFAULT_WIDTH;  // カードの幅 (CardViewに合わせて調整してください)
    private static final int CARD_HEIGHT = CardView.DEFAULT_HEIGHT; // カードの高さ
    private static final int CARD_GAP = 10;     // 重ならない場合のカード間の隙間
    private static final int BASE_Y = 20;       // カード配置の基準Y座標（上からの距離）

    // --- フィールド変数 ---
    private final HandModel model;
    private JPanel cardListPanel;
    private CardClickListener cardClickListener;
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
        setupModelListener();
        updateHand(model.getCards());
    }

    // --- 初期化メソッド ---
    private void setupPanels() {
        setPreferredSize(new Dimension(800, 260));
        setOpaque(false);
        setLayout(new BorderLayout());

        // カード一覧エリア
        cardListPanel = new JPanel(null);
        cardListPanel.setOpaque(false);
        add(cardListPanel, BorderLayout.CENTER);

        // リサイズ時にカード再配置を行う
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutCards();
            }
        });
    }

    // カード重ねるためのレイアウト調整
    private void layoutCards() {
        if (cardComponentList.isEmpty()) {
            cardListPanel.revalidate();
            cardListPanel.repaint();
            return;
        }

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

            wrapper.setBounds(x, BASE_Y, CARD_WIDTH, panelHeight);
            cardListPanel.setComponentZOrder(wrapper, cardCount - 1 - i);
        }

        cardListPanel.revalidate();
        cardListPanel.repaint();
    }

    // --- カード生成と更新 ---
    public final void updateHand(List<CardModel> cards) {
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
                    handleClick(cardView, e);
                }
            });

            cardListPanel.add(wrapper);
            cardComponentList.add(wrapper);
        }

        // 最後に配置計算を実行
        layoutCards();
        revalidate();
        repaint();
    }

    // --- リスナー設定 ---
    public void setCardClickListener(CardClickListener listener) {
        this.cardClickListener = listener;
    }

    private void handleClick(CardView cardView, MouseEvent e) {
        if (cardClickListener != null) {
            CardClickedEvent event = new CardClickedEvent(cardView.getCardModel(), ClickType.fromMouseEvent(e));
            cardClickListener.onCardClicked(event);
        }
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
        setCardClickListener(event -> {
            System.out.println("[Preview] Card clicked: " + event.clickType());
        });

        // 3. 画面更新
        updateHand(previewModel.getCards());
    }
}
