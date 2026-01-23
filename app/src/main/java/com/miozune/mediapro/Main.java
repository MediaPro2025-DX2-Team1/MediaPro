package com.miozune.mediapro;

import com.miozune.mediapro.game.GameApplication;
import com.miozune.mediapro.game.GameModel;
import com.miozune.mediapro.preview.PreviewLauncher;
import com.miozune.mediapro.util.SwingUtils;

public class Main {
    
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--preview")) {
            String componentName = args.length > 1 ? args[1] : "list";
            PreviewLauncher.launch(componentName);
            return;
        }

        SwingUtils.invokeLater(() -> {
            GameModel model = new GameModel();
            GameApplication application = new GameApplication(model);
            application.launch();
        });
    }
}
