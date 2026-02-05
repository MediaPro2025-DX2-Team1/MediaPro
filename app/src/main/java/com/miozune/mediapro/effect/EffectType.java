package com.miozune.mediapro.effect;

/**
 * ビジュアルエフェクトの種類を定義するenum。
 */
public enum EffectType {
    /** プレイヤーの攻撃エフェクト */
    PLAYER_ATTACK("effects/player_attack", 6, 60, 0.2, 0.3),

    /** 敵の攻撃エフェクト */
    ENEMY_ATTACK("effects/enemy_attack", 6, 60, 0.2, 0.3),

    /** バフエフェクト（筋力上昇など） */
    BUFF("effects/buff", 8, 60, 0.2, 0.2),

    /** デバフエフェクト（弱体化など） */
    DEBUFF("effects/debuff", 8, 60, 0.2, 0.2),

    /** 回復エフェクト */
    HEAL("effects/heal", 9, 60, 0.4, 0.2),

    /** シールドエフェクト */
    SHIELD("effects/shield", 10, 60, 0.1, 0.2);

    private final String resourcePath;
    private final int frameCount;
    private final int fps;
    private final double scale;
    private final double speedMultiplier;

    /**
     * @param resourcePath リソースディレクトリのパス（images/ からの相対パス）
     * @param frameCount フレーム数
     * @param fps 再生フレームレート（1秒あたりのフレーム数）
     * @param scale 表示スケール（1.0 = 元サイズ、0.5 = 50%サイズ）
     * @param speedMultiplier 再生速度倍率（1.0 = 標準速度、0.5 = 半分の速度、2.0 = 2倍速）
     */
    EffectType(String resourcePath, int frameCount, int fps, double scale, double speedMultiplier) {
        this.resourcePath = resourcePath;
        this.frameCount = frameCount;
        this.fps = fps;
        this.scale = scale;
        this.speedMultiplier = speedMultiplier;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int getFps() {
        return fps;
    }

    public double getScale() {
        return scale;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    /**
     * フレーム間隔をミリ秒で取得。速度倍率を考慮。
     */
    public int getFrameDelayMs() {
        return (int) (1000.0 / fps / speedMultiplier);
    }
}
