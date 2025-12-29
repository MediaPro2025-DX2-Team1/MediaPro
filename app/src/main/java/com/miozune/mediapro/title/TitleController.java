package com.miozune.mediapro.title;

import javax.swing.*;

public class TitleController {

    public TitleController(TitleView view) {
        view.getStartButton().addActionListener(e -> openSelectScreen());
    }

    private void openSelectScreen() {
        System.out.println("ステージ選択画面に遷移");
    }

}