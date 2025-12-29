package com.miozune.mediapro.world;

import javax.swing.*;

public class WorldController {
    
    public WorldController(WorldModel model, WorldView view) {
        view.getStageButton1().addActionListener(e -> selectStage(1));
        view.getStageButton2().addActionListener(e -> selectStage(2));
        view.getStageButton3().addActionListener(e -> selectStage(3));
    }    

    private void selectStage(int stage_number) {
        System.out.println("STAGE " + stage_number + " に遷移");
    }

}