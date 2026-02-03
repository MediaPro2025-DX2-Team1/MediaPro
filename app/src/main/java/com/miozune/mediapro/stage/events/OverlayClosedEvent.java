package com.miozune.mediapro.stage.events;

/**
 * オーバーレイが閉じられたことを示すイベント。
 * 山札・捨て札一覧の閉じるボタンやカード詳細の背景クリックなどで発行される。
 */
public record OverlayClosedEvent() implements OverlayEvent {}
