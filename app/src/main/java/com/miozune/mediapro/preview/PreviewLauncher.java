package com.miozune.mediapro.preview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.google.common.reflect.ClassPath;
import com.miozune.mediapro.util.SwingUtils;

/**
 * コンポーネントを単体でプレビューするためのランチャー。
 * 登録するコンポーネントは {@link Previewable} インターフェースを実装する必要がある。
 */
public class PreviewLauncher {

    // LinkedHashMapで登録順を保持
    private static final Map<String, Previewable> PREVIEWABLE_COMPONENTS = new LinkedHashMap<>();
    private static boolean componentsScanned = false;

    /**
     * プレビュー可能なコンポーネントを登録する。
     * コンポーネントは {@link Previewable} インターフェースを実装している必要がある。
     * プレビュー名はクラス名から自動的に取得される。
     *
     * @param instance コンポーネントのインスタンス
     * @param <T>      Previewableを実装したJComponentのサブタイプ
     */
    public static <T extends JComponent & Previewable> void registerComponent(T instance) {
        String name = instance.getClass().getSimpleName();
        PREVIEWABLE_COMPONENTS.put(name.toLowerCase(), instance);
    }

    /**
     * クラスパスをスキャンしてPreviewableを実装したコンポーネントを自動登録する。
     * com.miozune.mediaproパッケージ配下のすべてのクラスを検査し、
     * Previewableを実装しJComponentを継承した具象クラスを検出する。
     * no-argコンストラクタがない場合は警告を出力してスキップする。
     * 検出されたコンポーネントはアルファベット順にソートして登録される。
     */
    @SuppressWarnings("UseSpecificCatch")
    private static void scanAndRegisterComponents() {
        if (componentsScanned) {
            return;
        }
        componentsScanned = true;

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            ClassPath classPath = ClassPath.from(classLoader);
            List<Class<?>> previewableClasses = new ArrayList<>();

            for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive("com.miozune.mediapro")) {
                try {
                    Class<?> clazz = classInfo.load();
                    // Previewableを実装し、JComponentを継承し、抽象クラスでないことをチェック
                    if (Previewable.class.isAssignableFrom(clazz) &&
                        JComponent.class.isAssignableFrom(clazz) &&
                        !Modifier.isAbstract(clazz.getModifiers())) {
                        // 引数なしコンストラクタの存在をチェック
                        try {
                            clazz.getDeclaredConstructor();
                            previewableClasses.add(clazz);
                        } catch (NoSuchMethodException e) {
                            System.err.println("WARN: " + clazz.getSimpleName() + 
                                " implements Previewable but does not have a no-arg constructor. Skipping.");
                        }
                    }
                } catch (Throwable t) {
                    // 無視
                }
            }

            previewableClasses.sort((a, b) -> a.getSimpleName().compareTo(b.getSimpleName()));
            for (Class<?> clazz : previewableClasses) {
                try {
                    @SuppressWarnings("unchecked")
                    JComponent component = (JComponent) clazz.getDeclaredConstructor().newInstance();
                    registerComponent((JComponent & Previewable) component);
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to register " + clazz.getSimpleName() + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("ERROR: Failed to scan classpath: " + e.getMessage());
        }
    }

    /**
     * 指定されたコンポーネントをプレビューする。
     *
     * @param componentName コンポーネント名、または "list" で一覧表示
     */
    public static void launch(String componentName) {
        scanAndRegisterComponents();

        if (componentName.equalsIgnoreCase("list")) {
            printAvailableComponents();
            return;
        }

        Previewable previewable = PREVIEWABLE_COMPONENTS.get(componentName.toLowerCase());

        if (previewable == null) {
            System.err.println("Error: Unknown component '" + componentName + "'");
            System.err.println();
            printAvailableComponents();
            System.exit(1);
            return;
        }

        SwingUtils.invokeLater(() -> {
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

        PREVIEWABLE_COMPONENTS.forEach((key, previewable) -> {
            System.out.println(
                    "  " + previewable.getClass().getSimpleName() + " - " + previewable.getPreviewDescription());
        });

        System.out.println();
        System.out.println("Usage: ./preview.sh <ComponentName>");
        System.out.println("       ./preview.bat <ComponentName>  (Windows)");
    }
}
