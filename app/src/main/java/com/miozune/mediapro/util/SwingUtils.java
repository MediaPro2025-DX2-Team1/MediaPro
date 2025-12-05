package com.miozune.mediapro.util;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Swing関連のユーティリティメソッドを提供するクラス。
 */
public final class SwingUtils {
    
    private SwingUtils() {}
    
    /**
     * EDTでRunnableを実行する。
     * システムのルック&フィールを設定してから実行する。
     *
     * @param runnable 実行するRunnable
     */
    public static void invokeLater(Runnable runnable) {
        SwingUtilities.invokeLater(() -> {
            setupLookAndFeel();
            runnable.run();
        });
    }
    
    /**
     * システムのルック&フィールを設定する。
     */
    public static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | 
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            // システムのルック&フィールが使用できない場合は、デフォルトを使用
            System.err.println("Warning: Could not set system look and feel: " + e.getMessage());
        }
    }
    
    /**
     * EDTで実行中かどうかを確認する。
     *
     * @return EDTで実行中の場合はtrue
     */
    public static boolean isEventDispatchThread() {
        return SwingUtilities.isEventDispatchThread();
    }
    
    /**
     * EDTでなければ例外をスローする。
     *
     * @throws IllegalStateException EDTで実行されていない場合
     */
    public static void checkEventDispatchThread() {
        if (!isEventDispatchThread()) {
            throw new IllegalStateException("This method must be called on the Event Dispatch Thread");
        }
    }
}
