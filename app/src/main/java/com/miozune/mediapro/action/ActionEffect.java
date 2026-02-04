package com.miozune.mediapro.action;

/**
 * カード効果の最小単位。
 */
public interface ActionEffect {
    boolean apply(ActionContext context);
}
