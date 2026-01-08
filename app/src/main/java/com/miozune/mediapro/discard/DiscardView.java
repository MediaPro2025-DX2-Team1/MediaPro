package com.miozune.mediapro.discard;

import com.miozune.mediapro.card.CardController;
import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardView;
import com.miozune.mediapro.preview.Previewable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DiscardView extends JPanel implements Previewable {

    private JLayeredPane layeredPane;
    private JPanel mainContentPanel;
    private JPanel detailPanel;
    private JPanel cardListPanel;
    private JButton closeButton;
    private ActionListener cardClickListener;

    public DiscardView() {
        setPreferredSize(new Dimension(800, 600));
        setOpaque(false);
        setLayout(new BorderLayout());

        // レイヤーペインの初期化
        layeredPane = new JLayeredPane();
        add(layeredPane, BorderLayout.CENTER);

        // --- 1. メインコンテンツ層の作成 (奥側) ---
        mainContentPanel = createContentPanel();
        // ウィンドウサイズ固定設定
        mainContentPanel.setBounds(0, 0, 800, 600);
        layeredPane.add(mainContentPanel, JLayeredPane.DEFAULT_LAYER);

        // --- 2. 拡大表示層の作成 (手前側) ---
        createDetailPanel();
        detailPanel.setVisible(false); 
        // 拡大パネルも固定サイズ設定
        detailPanel.setBounds(0, 0, 800, 600);
        layeredPane.add(detailPanel, JLayeredPane.MODAL_LAYER);
    }

    /** メインコンテンツ（既存の一覧画面）を作成 */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);

        // 上部パネル
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        closeButton = new JButton("× 閉じる");
        closeButton.setFont(new Font("Meiryo", Font.BOLD, 16));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(closeButton);
    
        JLabel titleLabel = new JLabel("捨て札一覧", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Meiryo", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        topContainer.add(btnPanel, BorderLayout.NORTH);
        topContainer.add(titleLabel, BorderLayout.CENTER);
        panel.add(topContainer, BorderLayout.NORTH);

        // 中央パネル（カード一覧エリア）
        cardListPanel = new JPanel(new GridLayout(0, 4, 20, 20));
        cardListPanel.setOpaque(false);
        cardListPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(cardListPanel, BorderLayout.NORTH);
 
        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /** 拡大表示用のパネルを作成 */
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

        // 背景クリックで閉じるアクション
        detailPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hideCardDetail();
            }
        });
    }

    /** 指定されたカードモデルで拡大表示を行う */
    public void showCardDetail(CardModel cardModel) {
        detailPanel.removeAll();
        CardView bigCardView = new CardView(cardModel);
        bigCardView.setPreferredSize(new Dimension(300, 420)); 

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

    /** 拡大表示を隠す */
    public void hideCardDetail() {
        detailPanel.setVisible(false);
        detailPanel.removeAll(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    public void setCloseButtonListener(ActionListener listener) {
        this.closeButton.addActionListener(listener);
    }

    public void setCardClickListener(ActionListener listener) {
        this.cardClickListener = listener;
    }

    public void updateView(List<CardModel> cardList) {
        cardListPanel.removeAll();
        for (CardModel cardModel : cardList) {
            CardView cardView = new CardView(cardModel);
            CardController controller = new CardController(cardView);     
            controller.setClickListener((view, e) -> {
                if (cardClickListener != null) {
                    ActionEvent actionEvent = new ActionEvent(
                        cardModel,
                        ActionEvent.ACTION_PERFORMED,
                        "CARD_CLICKED"
                    );
                    cardClickListener.actionPerformed(actionEvent);
                }
            });
            cardListPanel.add(cardView);
        }
        cardListPanel.revalidate();
        cardListPanel.repaint();
    }

    @Override
    public String getPreviewDescription() {
        return "捨て札画面（クリックで拡大表示）";
    }

    @Override
    public void setupPreview() {
        closeButton.addActionListener(e -> System.out.println("close button clicked"));
        DiscardModel model = new DiscardModel();
        for (int i = 1; i <= 14; i++) {
            CardModel sample = CardModel.createSample();
            model.addCard(sample);
        }
        setCardClickListener(e -> {
            if (e.getSource() instanceof CardModel) {
                showCardDetail((CardModel) e.getSource());
            }
        });
        updateView(model.getCards());
    }
}