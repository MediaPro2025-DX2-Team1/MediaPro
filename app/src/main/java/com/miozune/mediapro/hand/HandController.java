package com.miozune.mediapro.hand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.miozune.mediapro.card.CardModel;
import com.miozune.mediapro.card.CardView;

public class HandController {
    
    private HandModel model;
    private HandView view;

    public HandController(HandModel model, HandView view) {
        this.model = model;
        this.view = view;

        this.view.setHandActionListener(new HandView.HandActionListener() {
            @Override
            public void onCardLeftClick(CardModel card) {
                useCard(card);
            }

            @Override
            public void onCardRightClick(CardModel card) {
                showCardDetail(card);
            }
        });

        refreshView();
    }

    public void addCard(CardModel card) {
        model.addCard(card);
        refreshView();
    }

    public void removeCard(CardModel card) {
        model.removeCard(card);
        refreshView();
    }

    private void refreshView() {
        view.updateView(model.getCards());
    }

    private void useCard(CardModel card) {
        System.out.println("カードを使用しました: " + card.name());
        // カードの効果を適用するコードをここに追加
    }

    private void showCardDetail(CardModel card) {
        Window parentWindow = SwingUtilities.getWindowAncestor(view);
        if (parentWindow == null) return;

        // 1. ダイアログの作成（親ウィンドウを指定、モーダル）
        JDialog dialog = new JDialog((Frame) parentWindow, "カード詳細", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0)); 
        dialog.setSize(parentWindow.getSize());
        dialog.setLocation(parentWindow.getLocationOnScreen());

        // 2. 背景パネル（半透明の黒）
        JPanel overlayPanel = new JPanel(new GridBagLayout());
        overlayPanel.setBackground(new Color(0, 0, 0, 180));

        // 背景クリックで閉じる処理
        overlayPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dialog.dispose();
            }
        });

        // 3. 拡大カードの作成
        CardView bigCard = new CardView(card);
        bigCard.setPreferredSize(new Dimension(300, 420)); // 拡大サイズ
        
        // カード自体のクリックで閉じる処理
        bigCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dialog.dispose();
            }
        });

        overlayPanel.add(bigCard);        
        dialog.add(overlayPanel);
        dialog.setVisible(true);
    }
}