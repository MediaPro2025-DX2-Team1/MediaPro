package com.miozune.mediapro.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.miozune.mediapro.util.SwingUtils;

/**
 * PlayerView + PlayerController の動作確認用テストプログラム。
 * ボタン押下でPlayerModelの値を変化させ、PlayerViewが正しく更新されることを確認する。
 */
public class PlayerViewTest {

    public static void main(String[] args) {
        System.out.println("PlayerViewTest starting...");

        SwingUtils.invokeLater(() -> {
            System.out.println("Inside SwingUtils.invokeLater");

            // Model作成
            PlayerModel model = PlayerModel.createDefaultPlayer();
            System.out.println("PlayerModel created");

            model.setName("テストプレイヤー");
            model.setMaxHp(100);
            model.setHp(80);
            model.setMaxMana(10);
            model.setMana(5);
            System.out.println("PlayerModel initialized");

            // View作成（modelを渡す）
            System.out.println("Creating PlayerView...");
            PlayerView view = new PlayerView(model);
            System.out.println("PlayerView created");

            // Controller作成（ViewとModelを連携）
            System.out.println("Creating PlayerController...");
            PlayerController controller = new PlayerController(model, view);
            System.out.println("PlayerController created");

            // テスト用のコントロールパネルを作成
            JPanel controlPanel = createControlPanel(model);

            // デバッグパネル（Controllerが提供）も追加
            JPanel debugPanel = controller.createDebugPanel();

            // 両方のパネルを統合
            JPanel allControlsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
            allControlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            allControlsPanel.add(controlPanel);
            allControlsPanel.add(debugPanel);

            // ウィンドウ作成
            JFrame frame = new JFrame("PlayerView Test - Model変更確認");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout(10, 10));

            // レイアウト構成
            JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            contentPanel.add(view, BorderLayout.CENTER);

            frame.add(contentPanel, BorderLayout.CENTER);
            frame.add(allControlsPanel, BorderLayout.SOUTH);

            // 説明ラベル
            JLabel instructionLabel = new JLabel(
                "<html><b>テスト手順:</b> 下のボタンを押してPlayerModelの値を変更し、" +
                "上のPlayerViewが自動的に更新されることを確認してください。</html>"
            );
            instructionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            frame.add(instructionLabel, BorderLayout.NORTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // 初期状態をコンソールに出力
            System.out.println("=== PlayerViewTest Started ===");
            System.out.println("Initial State:");
            System.out.printf("  Name: %s%n", model.getName());
            System.out.printf("  HP: %d / %d%n", model.getHp(), model.getMaxHp());
            System.out.printf("  Mana: %d / %d%n", model.getMana(), model.getMaxMana());
            System.out.println("==============================");
        });
    }

    /**
     * テスト用のコントロールパネルを作成する。
     * より多様なテストケースを提供する。
     * 
     * @param model 操作対象のPlayerModel
     * @return コントロールパネル
     */
    private static JPanel createControlPanel(PlayerModel model) {
        JPanel panel = new JPanel(new GridLayout(3, 3, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Test Controls"));

        // HP関連のボタン
        JButton damage5Btn = new JButton("ダメージ -5");
        damage5Btn.addActionListener(e -> {
            model.takeDamage(5);
            System.out.println("→ ダメージ5を与えた");
        });

        JButton damage25Btn = new JButton("ダメージ -25");
        damage25Btn.addActionListener(e -> {
            model.takeDamage(25);
            System.out.println("→ ダメージ25を与えた");
        });

        JButton heal10Btn = new JButton("回復 +10");
        heal10Btn.addActionListener(e -> {
            model.heal(10);
            System.out.println("→ HP10回復した");
        });

        JButton setHp0Btn = new JButton("HP → 0");
        setHp0Btn.setForeground(Color.RED);
        setHp0Btn.addActionListener(e -> {
            model.setHp(0);
            System.out.println("→ HPを0に設定（死亡状態）");
        });

        JButton setHpFullBtn = new JButton("HP → MAX");
        setHpFullBtn.setForeground(new Color(0, 150, 0));
        setHpFullBtn.addActionListener(e -> {
            model.setHp(model.getMaxHp());
            System.out.println("→ HPを最大値に回復");
        });

        // マナ関連のボタン
        JButton addMana1Btn = new JButton("マナ +1");
        addMana1Btn.addActionListener(e -> {
            model.addMana();
            System.out.println("→ マナを1追加");
        });

        JButton consume3Btn = new JButton("マナ消費 -3");
        consume3Btn.addActionListener(e -> {
            boolean success = model.consumeMana(3);
            if (success) {
                System.out.println("→ マナ3を消費");
            } else {
                System.out.println("→ マナ不足で消費失敗");
            }
        });

        JButton setMana0Btn = new JButton("マナ → 0");
        setMana0Btn.addActionListener(e -> {
            model.resetMana();
            System.out.println("→ マナをリセット（0に）");
        });

        JButton setManaMaxBtn = new JButton("マナ → MAX");
        setManaMaxBtn.addActionListener(e -> {
            model.setMana(model.getMaxMana());
            System.out.println("→ マナを最大値に設定");
        });

        // ボタンを配置
        panel.add(damage5Btn);
        panel.add(damage25Btn);
        panel.add(heal10Btn);
        panel.add(setHp0Btn);
        panel.add(setHpFullBtn);
        panel.add(addMana1Btn);
        panel.add(consume3Btn);
        panel.add(setMana0Btn);
        panel.add(setManaMaxBtn);

        return panel;
    }
}
