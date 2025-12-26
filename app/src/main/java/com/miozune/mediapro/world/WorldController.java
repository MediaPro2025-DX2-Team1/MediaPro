package com.miozune.mediapro.world;

import javax.swing.*;

public class WorldController {
    
    private WorldModel model;
    private WorldView view;
    private JFrame frame;

    public WorldController(WorldModel model, WorldView view) {
        this.model = model;
        this.view = view;
        this.view.getStageButton1().addActionListener(e -> selectStage(1));
        this.view.getStageButton2().addActionListener(e -> selectStage(2));
        this.view.getStageButton3().addActionListener(e -> selectStage(3));
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

    private void selectStage(int stage_number) {
        System.out.println("STAGE " + stage_number + " に遷移");
    }

}