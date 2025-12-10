package com.miozune.mediapro.stage_select;

import javax.swing.*;

public class SelectController {

    private SelectModel model;
    private SelectView view;

    public SelectController(SelectModel model, SelectView view) {
        this.model = model;
        this.view = view;

        view.getStageButton1().addActionListener(e -> selectStage(1));
        view.getStageButton2().addActionListener(e -> selectStage(2));
        view.getStageButton3().addActionListener(e -> selectStage(3));
    }    

    public void showView() {
        view.setVisible(true);
    }

    private void selectStage(int stage) {
        view.dispose();

        JFrame next = new JFrame("Stage" + stage);
        next.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        next.setSize(600, 400);
        next.setLocationRelativeTo(null);

        JLabel label = new JLabel("STAGE" + stage, SwingConstants.CENTER);
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));

        next.add(label);
        next.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SelectModel model = new SelectModel();
            SelectView view = new SelectView();
            SelectController controller = new SelectController(model, view);
            controller.showView();
        });
    }
}

