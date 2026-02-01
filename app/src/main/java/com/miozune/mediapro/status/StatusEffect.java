package com.miozune.mediapro.status;

/**
 * キャラクターに付与される一時的な状態（バフ・デバフ）。
 */
public interface StatusEffect {

    /** ターン開始時の処理。 */
    void onTurnStart();

    /** 攻撃側として与えるダメージを補正する。 */
    int onOutgoingDamage(int baseDamage);

    /** 防御側として受けるダメージを補正する。 */
    int onIncomingDamage(int baseDamage);

    /** 有効期限切れか判定。 */
    boolean isExpired();
}
