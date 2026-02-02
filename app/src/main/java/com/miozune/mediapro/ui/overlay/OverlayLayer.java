package com.miozune.mediapro.ui.overlay;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;

/**
 * オーバーレイ用のレイヤー。スタック管理だけを担い、描画やクリック処理は各パネル側に委ねる。
 */
public class OverlayLayer extends JLayeredPane {

    private final Deque<JComponent> stack = new ArrayDeque<>();
    private final Map<JComponent, OverlayMetadata> metadata = new HashMap<>();

    /**
     * オーバーレイのメタデータを保持する内部クラス。
     */
    private static class OverlayMetadata {
        private final boolean closeableByEsc;

        OverlayMetadata(boolean closeableByEsc) {
            this.closeableByEsc = closeableByEsc;
        }

        boolean isCloseableByEsc() {
            return closeableByEsc;
        }
    }

    public OverlayLayer() {
        setOpaque(false);
        setVisible(false);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeChildren();
            }
        });
    }

    /**
     * オーバーレイをスタックに追加する（デフォルト：ESCで閉じられる）。
     *
     * @param overlay 追加するオーバーレイパネル
     */
    public void push(JComponent overlay) {
        push(overlay, true);
    }

    /**
     * オーバーレイをスタックに追加する。
     *
     * @param overlay 追加するオーバーレイパネル
     * @param closeableByEsc ESCキーで閉じられるかどうか
     */
    public void push(JComponent overlay, boolean closeableByEsc) {
        if (overlay == null) {
            return;
        }
        stack.push(overlay);
        metadata.put(overlay, new OverlayMetadata(closeableByEsc));
        overlay.setBounds(0, 0, getWidth(), getHeight());
        add(overlay, Integer.valueOf(stack.size()));
        setVisible(true);
        revalidate();
        repaint();
    }

    public void pop() {
        if (stack.isEmpty()) {
            return;
        }
        JComponent toRemove = stack.pop();
        metadata.remove(toRemove);
        remove(toRemove);
        if (stack.isEmpty()) {
            setVisible(false);
        }
        revalidate();
        repaint();
    }

    public void clearAll() {
        stack.clear();
        metadata.clear();
        removeAll();
        setVisible(false);
        revalidate();
        repaint();
    }

    /**
     * 最上位のオーバーレイがESCキーで閉じられるかどうかを判定する。
     *
     * @return ESCキーで閉じられる場合はtrue、それ以外はfalse
     */
    public boolean isTopCloseableByEsc() {
        if (stack.isEmpty()) {
            return false;
        }
        JComponent top = stack.peek();
        OverlayMetadata meta = metadata.get(top);
        return meta != null && meta.isCloseableByEsc();
    }

    private void resizeChildren() {
        Dimension size = getSize();
        for (Component comp : getComponents()) {
            comp.setBounds(0, 0, size.width, size.height);
        }
    }
}
