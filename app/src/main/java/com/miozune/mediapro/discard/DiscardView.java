package com.miozune.mediapro.discard;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardView;
import com.miozune.mediapro.discard.events.DiscardCardChangedEvent;
import com.miozune.mediapro.preview.Previewable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DiscardView extends JPanel implements Previewable {
    
    private final DiscardModel model;
    private JLayeredPane layeredPane;
    private JPanel contentPanel;
    private JPanel detailPanel;
    private JPanel cardListPanel;
    private JButton closeButton;
    private ActionListener cardClickListener;
    private DiscardModel.PropertyChangeListener modelListener;
    
    public DiscardView() {
        this(DiscardModel.createDefaultDiscard());
    }

    public DiscardView(DiscardModel model) {
        this.model = model;
        setupPanels();  
        createContentPanel();
        createDetailPanel();
        setupModelListener();
        updateDiscard(model.getCards());
    }
    
    private void setupPanels() {
        setPreferredSize(new Dimension(800, 600));
        setOpaque(false);
        setLayout(new BorderLayout());

        // レイヤーペインの初期化
        layeredPane = new JLayeredPane();
        add(layeredPane, BorderLayout.CENTER);
    }

    /** 捨て札一覧用のパネルを作成 */
    private void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        contentPanel.setOpaque(false);

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
        contentPanel.add(topContainer, BorderLayout.NORTH);

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
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.setBounds(0, 0, 800, 600);
        layeredPane.add(contentPanel, JLayeredPane.DEFAULT_LAYER);
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

        detailPanel.setVisible(false); 
        detailPanel.setBounds(0, 0, 800, 600);
        layeredPane.add(detailPanel, JLayeredPane.MODAL_LAYER);
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

    // --- イベントリスナー設定メソッド ---
    
    public void setCloseButtonListener(ActionListener listener) {
        this.closeButton.addActionListener(listener);
    }

    public void setCardClickListener(ActionListener listener) {
        this.cardClickListener = listener;
    }
    
    // --- Model連携 ---

    private void setupModelListener() {
        modelListener = event -> {
            switch (event) {
                case DiscardCardChangedEvent e -> updateDiscard(e.newcards());
            }
        };

        model.addPropertyChangeListener(modelListener);
    }

    private void updateDiscard(List<CardModel> cards) {
        cardListPanel.removeAll();
        for (CardModel cardModel : cards) {
            CardView cardView = new CardView(cardModel);

            cardView.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (cardClickListener != null) {
                        ActionEvent actionEvent = new ActionEvent(cardModel, ActionEvent.ACTION_PERFORMED, "cardClicked");
                        cardClickListener.actionPerformed(actionEvent);
                    }
                }
            });

            cardListPanel.add(cardView);
        }
        cardListPanel.revalidate();
        cardListPanel.repaint();
    }
    
    // --- Previewable実装 ---

    @Override
    public String getPreviewDescription() {
        return "捨て札画面（クリックで拡大表示）";
    }

    @Override
    public void setupPreview() {
        closeButton.addActionListener(e -> System.out.println("closebutton clicked."));
        updateDiscard(DiscardModel.createDefaultDiscard().getCards());
        setCardClickListener(e -> {
            if (e.getSource() instanceof CardModel) {
                showCardDetail((CardModel) e.getSource());
            }
        });
    }
}