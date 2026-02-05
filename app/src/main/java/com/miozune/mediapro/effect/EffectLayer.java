package com.miozune.mediapro.effect;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 複数のエフェクトアニメーションを管理し、透明なレイヤーとして重ねて表示するPanel。
 * StageViewのJLayeredPaneの最前面に配置される。
 */
public class EffectLayer extends JPanel {

    private final Map<EffectType, EffectAnimationModel> modelCache = new EnumMap<>(EffectType.class);
    private final List<EffectAnimationView> activeEffects = new ArrayList<>();

    public EffectLayer() {
        // 透明な背景
        setOpaque(false);
        setLayout(null); // 絶対配置

        // 全EffectTypeのモデルを事前にロード
        preloadEffects();
    }

    /**
     * 全エフェクトタイプの画像を事前にロード。
     */
    private void preloadEffects() {
        for (EffectType type : EffectType.values()) {
            try {
                EffectAnimationModel model = new EffectAnimationModel(type);
                modelCache.put(type, model);
            } catch (IOException e) {
                System.err.println("エフェクト画像の読み込みに失敗: " + type);
                e.printStackTrace();
            }
        }
    }

    /**
     * 指定された位置でエフェクトを再生。
     *
     * @param type エフェクトタイプ
     * @param position 再生位置（画像の中心座標）
     */
    public void playEffect(EffectType type, Point position) {
        EffectAnimationModel model = modelCache.get(type);
        if (model == null) {
            System.err.println("エフェクトモデルが見つかりません: " + type);
            return;
        }

        // エフェクトビューを格納する配列（ラムダからアクセスするため）
        final EffectAnimationView[] viewHolder = new EffectAnimationView[1];

        // エフェクトビューを作成
        EffectAnimationView effectView = new EffectAnimationView(model, position, () -> {
            // 再生完了時の処理（EDT上で実行）
            SwingUtilities.invokeLater(() -> {
                EffectAnimationView view = viewHolder[0];
                if (view != null) {
                    remove(view);
                    activeEffects.remove(view);
                    view.dispose();
                    repaint();
                }
            });
        });

        viewHolder[0] = effectView;

        // レイヤー全体に配置（絶対配置なので位置は内部で管理）
        effectView.setBounds(0, 0, getWidth(), getHeight());

        // 追加して再生開始
        add(effectView);
        activeEffects.add(effectView);
        effectView.start();

        // 再描画
        revalidate();
        repaint();
    }

    /**
     * すべてのアクティブなエフェクトを停止してクリア。
     */
    public void clearAllEffects() {
        for (EffectAnimationView effect : new ArrayList<>(activeEffects)) {
            effect.dispose();
            remove(effect);
        }
        activeEffects.clear();
        repaint();
    }

    /**
     * 現在再生中のエフェクト数を取得。
     */
    public int getActiveEffectCount() {
        return activeEffects.size();
    }
}
