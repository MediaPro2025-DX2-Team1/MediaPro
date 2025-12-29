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
    private static final Map<String, PreviewableComponent> PREVIEWABLE_COMPONENTS = new LinkedHashMap<>();
    private static boolean componentsScanned = false;
    
    /**
     * プレビュー可能なコンポーネントとその説明を保持するクラス。
     */
    private static class PreviewableComponent {
        final JComponent component;
        final String description;
        
        PreviewableComponent(JComponent component, String description) {
            this.component = component;
            this.description = description;
        }
    }

    /**
     * クラスパスをスキャンして@Previewableアノテーションを持つコンポーネントを自動登録する。
     * com.miozune.mediaproパッケージ配下のすべてのクラスを検査し、
     * @PreviewableアノテーションとJComponentを継承した具象クラスを検出する。
     * 
     * インスタンス化の優先順位:
     * 1. public static [Type] createPreview() メソッド（推奨）
     * 2. public no-arg constructor（フォールバック、WARNログ出力）
     * 
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
                    // @Previewableアノテーションを持ち、JComponentを継承し、抽象クラスでないことをチェック
                    if (clazz.isAnnotationPresent(Previewable.class) &&
                        JComponent.class.isAssignableFrom(clazz) &&
                        !Modifier.isAbstract(clazz.getModifiers())) {
                        previewableClasses.add(clazz);
                    }
                } catch (Throwable t) {
                    // 無視
                }
            }

            previewableClasses.sort((a, b) -> a.getSimpleName().compareTo(b.getSimpleName()));
            for (Class<?> clazz : previewableClasses) {
                try {
                    JComponent component = createPreviewInstance(clazz);
                    String name = component.getClass().getSimpleName().toLowerCase();
                    
                    // Descriptionを取得
                    Previewable annotation = clazz.getAnnotation(Previewable.class);
                    String description = annotation.description();
                    if (description.isEmpty()) {
                        description = clazz.getSimpleName();
                    }
                    
                    PreviewableComponent previewableComponent = new PreviewableComponent(component, description);
                    PREVIEWABLE_COMPONENTS.put(name, previewableComponent);
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to register " + clazz.getSimpleName() + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("ERROR: Failed to scan classpath: " + e.getMessage());
        }
    }
    
    /**
     * プレビュー用のインスタンスを生成する。
     * 
     * 1. createPreview() staticメソッドを探して実行
     * 2. なければno-arg constructorを使用（ERRORログ出力）
     * 3. どちらもなければ例外をスロー
     *
     * @param clazz コンポーネントクラス
     * @return プレビュー用インスタンス
     * @throws Exception インスタンス化に失敗した場合
     */
    @SuppressWarnings("unchecked")
    private static JComponent createPreviewInstance(Class<?> clazz) throws Exception {
        // Step 1: createPreview() staticメソッドを探す
        try {
            java.lang.reflect.Method method = clazz.getMethod("createPreview");
            if (java.lang.reflect.Modifier.isStatic(method.getModifiers()) &&
                JComponent.class.isAssignableFrom(method.getReturnType())) {
                return (JComponent) method.invoke(null);
            }
        } catch (NoSuchMethodException e) {
            // createPreview()が見つからない場合はno-arg constructorにフォールバック
        }
        
        // Step 2: No-arg constructor にフォールバック（WARNログ出力）
        try {
            System.err.println("WARN: " + clazz.getSimpleName() + " does not have createPreview() static method.");
            System.err.println("       Falling back to no-arg constructor. Consider adding:");
            System.err.println("       public static " + clazz.getSimpleName() + " createPreview() { ... }");
            return (JComponent) clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new Exception("No createPreview() method or no-arg constructor found in " + clazz.getSimpleName());
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

        PreviewableComponent previewableComponent = PREVIEWABLE_COMPONENTS.get(componentName.toLowerCase());

        if (previewableComponent == null) {
            System.err.println("Error: Unknown component '" + componentName + "'");
            System.err.println();
            printAvailableComponents();
            System.exit(1);
            return;
        }

        SwingUtils.invokeLater(() -> {
            showPreviewWindow(previewableComponent);
        });
    }

    /**
     * プレビューウィンドウを表示する。
     *
     * @param previewableComponent プレビューするコンポーネント
     */
    private static void showPreviewWindow(PreviewableComponent previewableComponent) {
        JFrame frame = new JFrame("Preview: " + previewableComponent.component.getClass().getSimpleName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ヘッダーパネル
        JPanel headerPanel = createHeaderPanel(previewableComponent);

        // メインレイアウト
        frame.setLayout(new BorderLayout());
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(previewableComponent.component, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * ヘッダーパネルを作成する。
     *
     * @param previewableComponent プレビューするコンポーネント
     * @return ヘッダーパネル
     */
    private static JPanel createHeaderPanel(PreviewableComponent previewableComponent) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(50, 50, 50));

        JLabel titleLabel = new JLabel("  Preview: " + previewableComponent.component.getClass().getSimpleName());

        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel(previewableComponent.description + "  ");
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

        PREVIEWABLE_COMPONENTS.forEach((key, previewableComponent) -> {
            System.out.println(
                    "  " + previewableComponent.component.getClass().getSimpleName() + " - " + previewableComponent.description);
        });

        System.out.println();
        System.out.println("Usage: ./preview.sh <ComponentName>");
        System.out.println("       ./preview.bat <ComponentName>  (Windows)");
    }
}
