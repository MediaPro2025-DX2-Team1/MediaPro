package com.miozune.mediapro.enemy;

/** 敵の種類を表す識別子。 */
public enum EnemyType {
    SLIME(0.6),
    ZOMBIE(0.9),
    DEVIL(0.9),
    GOBLIN(0.7),
    GOBLIN_LEADER(1.7),
    DRAGON(2.3);

    private final double scale;

    EnemyType(double scale) {
        this.scale = scale;
    }

    /**
     * この敵の画像表示スケール係数を取得する。
     *
     * @return スケール係数（0.8～2.0）
     */
    public double getScale() {
        return scale;
    }

    /**
     * この敵の画像ファイル名を取得する。
     *
     * @return 画像ファイル名（例: "slime.png"）
     */
    public String getImageFileName() {
        return name().toLowerCase() + ".png";
    }
}
