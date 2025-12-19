package com.miozune.mediapro.preview;

import com.miozune.mediapro.core.GamePanel;
import com.miozune.mediapro.util.SwingUtils;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * コンポーネントを単体でプレビューするためのランチャー。
 * 登録するコンポーネントは {@link Previewable} インターフェースを実装する必要がある。
 */
public class PreviewLauncher {
    
    // LinkedHashMapで登録順を保持
    private static final Map<String, Supplier<? extends Previewable>> PREVIEWABLE_COMPONENTS = new LinkedHashMap<>();
    
    static {
        // プレビュー可能なコンポーネントを登録
        registerComponent(GamePanel::new);
        
        // 新しいコンポーネントを追加する場合はここに登録
        // registerComponent(YourComponent::new);
    }
    
    /**
     * プレビュー可能なコンポーネントを登録する。
     * コンポーネントは {@link Previewable} インターフェースを実装している必要がある。
     * プレビュー名はクラス名から自動的に取得される。
     *
     * @param supplier コンポーネントのサプライヤー
     * @param <T> Previewableを実装したJComponentのサブタイプ
     */
    public static <T extends JComponent & Previewable> void registerComponent(Supplier<T> supplier) {
        // 一度インスタンスを作成してクラス名を取得（登録時のみ）
        T instance = supplier.get();
        String name = instance.getClass().getSimpleName();
        PREVIEWABLE_COMPONENTS.put(name.toLowerCase(), supplier::get);
    }
    
    /**
     * 指定されたコンポーネントをプレビューする。
     *
     * @param componentName コンポーネント名、または "list" で一覧表示
     */
    public static void launch(String componentName) {
        if (componentName.equalsIgnoreCase("list")) {
            printAvailableComponents();
            return;
        }
        
        Supplier<? extends Previewable> supplier = PREVIEWABLE_COMPONENTS.get(componentName.toLowerCase());
        
        if (supplier == null) {
            System.err.println("Error: Unknown component '" + componentName + "'");
            System.err.println();
            printAvailableComponents();
            System.exit(1);
            return;
        }
        
        SwingUtils.invokeLater(() -> {
            Previewable previewable = supplier.get();
            previewable.setupPreview();
            
            showPreviewWindow(previewable);
        });
    }
    
    /**
     * プレビューウィンドウを表示する。
     *
     * @param previewable プレビューするコンポーネント
     */
    private static void showPreviewWindow(Previewable previewable) {
        JFrame frame = new JFrame("Preview: " + previewable.getClass().getSimpleName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ヘッダーパネル
        JPanel headerPanel = createHeaderPanel(previewable);
        
        // メインレイアウト
        frame.setLayout(new BorderLayout());
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add((JComponent) previewable, BorderLayout.CENTER);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    /**
     * ヘッダーパネルを作成する。
     *
     * @param previewable プレビューするコンポーネント
     * @return ヘッダーパネル
     */
    private static JPanel createHeaderPanel(Previewable previewable) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(50, 50, 50));
        
        JLabel titleLabel = new JLabel("  Preview: " + previewable.getClass().getSimpleName());
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel descLabel = new JLabel(previewable.getPreviewDescription() + "  ");
        descLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        descLabel.setForeground(Color.LIGHT_GRAY);
        descLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(descLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * 利用可能なコンポーネントの一覧を表示する。
     */
    private static void printAvailableComponents() {
        System.out.println("Available components for preview:");
        System.out.println();
        
        PREVIEWABLE_COMPONENTS.forEach((key, supplier) -> {
            Previewable previewable = supplier.get();
            System.out.println("  " + previewable.getClass().getSimpleName() + " - " + previewable.getPreviewDescription());
        });
        
        System.out.println();
        System.out.println("Usage: ./preview.sh <ComponentName>");
        System.out.println("       ./preview.bat <ComponentName>  (Windows)");
    }
}
