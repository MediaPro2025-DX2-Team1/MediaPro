package com.miozune.mediapro.title;

import com.miozune.mediapro.stage_select.SelectController;
import com.miozune.mediapro.stage_select.SelectModel;
import com.miozune.mediapro.stage_select.SelectView;
import javax.swing.*;

public class TitleController {

    private TitleModel model;
    private TitleView view;

    public TitleController(TitleModel model, TitleView view) {
        this.model = model;
        this.view  = view;

        view.getStartButton().addActionListener(e -> openSelectScreen());
    }

    public void showView() {
        view.setVisible(true);
    }

    private void openSelectScreen() {
        view.dispose();

        SelectModel selectModel = new SelectModel();
        SelectView selectView = new SelectView();
        SelectController selectController = new SelectController(selectModel, selectView);
        selectController.showView();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TitleModel model = new TitleModel();
            TitleView view = new TitleView();
            TitleController controller = new TitleController(model, view);
            controller.showView();
        });
    }
}