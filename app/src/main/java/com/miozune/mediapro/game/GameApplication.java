package com.miozune.mediapro.game;

import com.miozune.mediapro.game.events.GameSceneChangedEvent;
import com.miozune.mediapro.stage.StageController;
import com.miozune.mediapro.stage.StageModel;
import com.miozune.mediapro.stage.StageView;
import com.miozune.mediapro.title.TitleController;
import com.miozune.mediapro.title.TitleView;
import com.miozune.mediapro.world.WorldController;
import com.miozune.mediapro.world.WorldView;
import java.awt.CardLayout;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameApplication {

    private static final String TITLE_CARD = "TITLE";
    private static final String WORLD_CARD = "WORLD";
    private static final String STAGE_CARD = "STAGE";

    private final GameModel model;
    private final JFrame frame;
    private final JPanel root;
    private final CardLayout layout;

    private final TitleView titleView;
    private final WorldView worldView;
    private final TitleController titleController;
    private final WorldController worldController;
    private StageView stageView;
    private StageController stageController;

    public GameApplication(GameModel model) {
        this.model = Objects.requireNonNull(model, "model");
        this.frame = new JFrame("MediaPro");
        this.layout = new CardLayout();
        this.root = new JPanel(layout);

        this.titleView = new TitleView();
        this.worldView = new WorldView();
        this.titleController = new TitleController(titleView, model);
        this.worldController = new WorldController(model.getWorld(), worldView, model);

        initFrame();
        initScenes();
        wireModel();
    }

    private void initFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(root);
    }

    private void initScenes() {
        root.add(titleView, TITLE_CARD);

        root.add(worldView, WORLD_CARD);
    }

    private void wireModel() {
        model.addPropertyChangeListener(event -> {
            if (event instanceof GameSceneChangedEvent sceneEvent) {
                handleSceneChange(sceneEvent.newScene());
            }
        });
    }

    private void handleSceneChange(GameScene scene) {
        switch (scene) {
            case TITLE -> showTitle();
            case WORLD -> showWorld();
            case STAGE -> showStage();
        }
    }

    private void showTitle() {
        layout.show(root, TITLE_CARD);
        packToView(titleView);
    }

    private void showWorld() {
        layout.show(root, WORLD_CARD);
        packToView(worldView);
    }

    private void showStage() {
        StageModel stage = model.getWorld().getCurrentStage();
        if (stage == null) {
            return;
        }
        if (stageView != null) {
            root.remove(stageView);
        }
        stageView = new StageView();
        stageController = new StageController(stage, stageView);
        root.add(stageView, STAGE_CARD);
        layout.show(root, STAGE_CARD);
        root.revalidate();
        root.repaint();
        packToView(stageView);
    }

    private void packToView(JPanel view) {
        frame.pack();
        frame.setLocationRelativeTo(null);
        view.requestFocusInWindow();
    }

    public void launch() {
        handleSceneChange(model.getScene());
        frame.setVisible(true);
    }
}
