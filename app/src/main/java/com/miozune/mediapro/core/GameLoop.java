package com.miozune.mediapro.core;

import javax.swing.Timer;
import java.awt.event.ActionListener;

/**
 * ゲームループを管理するクラス。
 * 60FPSでゲームの更新と描画を行う。
 */
public class GameLoop {
    
    private static final int TARGET_FPS = 60;
    private static final int FRAME_DELAY = 1000 / TARGET_FPS;
    
    private final Timer timer;
    private boolean running;
    
    /**
     * ゲームループを作成する。
     *
     * @param updateAction 毎フレーム実行するアクション
     */
    public GameLoop(Runnable updateAction) {
        ActionListener timerAction = e -> {
            if (running) {
                updateAction.run();
            }
        };
        this.timer = new Timer(FRAME_DELAY, timerAction);
        this.timer.setCoalesce(true);
        this.running = false;
    }
    
    /**
     * ゲームループを開始する。
     */
    public void start() {
        running = true;
        timer.start();
    }
    
    /**
     * ゲームループを停止する。
     */
    public void stop() {
        running = false;
        timer.stop();
    }
    
    /**
     * ゲームループが実行中かどうかを返す。
     *
     * @return 実行中の場合はtrue
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * 目標FPSを取得する。
     *
     * @return 目標FPS
     */
    public static int getTargetFps() {
        return TARGET_FPS;
    }
}
