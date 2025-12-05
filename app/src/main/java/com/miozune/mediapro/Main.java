package com.miozune.mediapro;

import com.miozune.mediapro.game.GameController;
import com.miozune.mediapro.game.GameModel;
import com.miozune.mediapro.game.GameWindow;
import com.miozune.mediapro.preview.PreviewLauncher;
import com.miozune.mediapro.util.SwingUtils;

/**
 * アプリケーションのエントリーポイント。
 * 通常起動とプレビューモードの切り替えを行う。
 */
public class Main {
    
    public static void main(String[] args) {
        // プレビューモードの判定
        if (args.length > 0 && args[0].equals("--preview")) {
            String componentName = args.length > 1 ? args[1] : "list";
            PreviewLauncher.launch(componentName);
            return;
        }
        
        // 通常のゲーム起動
        SwingUtils.invokeLater(() -> {
            GameModel model = new GameModel();
            GameWindow window = new GameWindow(model);
            GameController controller = new GameController(model, window);
            
            window.setVisible(true);
        });
    }
}
