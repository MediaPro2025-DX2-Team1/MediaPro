package com.miozune.mediapro.stage_select;

import javax.swing.*;

public class SelectController {

    private SelectView view;
    private JFrame frame;

    public SelectController(SelectView view) {
        this.view = view;

        view.getStageButton1().addActionListener(e -> selectStage(1));
        view.getStageButton2().addActionListener(e -> selectStage(2));
        view.getStageButton3().addActionListener(e -> selectStage(3));
    }    

    public void showView() {
        if (frame == null) {
            frame = new JFrame("Stage Select Screen");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(view);
            frame.pack();
            frame.setLocationRelativeTo(null);
        }
        frame.setVisible(true);
    }

    private void selectStage(int stage) {
        System.out.println("STAGE" + stage + "に遷移");
    }

}