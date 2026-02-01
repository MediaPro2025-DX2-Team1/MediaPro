package com.miozune.mediapro.card;

/**
 * カードが要求するターゲット種別。
 */
public enum CardTargetType {
    /** 敵1体を指定して実行。 */
    SINGLE_ENEMY,
    /** 全ての敵に対して即時実行。 */
    ALL_ENEMIES,
    /** プレイヤー自身のみ対象（選択不要）。 */
    SELF,
    /** 敵/味方を問わず選択不要のユーティリティ。 */
    NONE;

    public boolean requiresEnemySelection() {
        return this == SINGLE_ENEMY;
    }
}
