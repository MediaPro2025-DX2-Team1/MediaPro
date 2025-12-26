package com.miozune.mediapro.title;

import javax.swing.*;

public class TitleController {

    private TitleView view;
    private JFrame frame;

    public TitleController(TitleView view) {
        this.view  = view;

        view.getStartButton().addActionListener(e -> openSelectScreen());
    }

    public void showView() {
        if (frame == null) {
            frame = new JFrame("Title Screen");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(view);
            frame.pack();
            frame.setLocationRelativeTo(null);
        }
        frame.setVisible(true);
    }

    private void openSelectScreen() {
        System.out.println("ステージ選択画面に遷移");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TitleView view = new TitleView();
            TitleController controller = new TitleController(view);
            controller.showView();
        });
    }
}