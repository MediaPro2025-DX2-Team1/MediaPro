package com.miozune.mediapro.enemy;

/**
 * Enemy の Model と View を仲介する Controller クラス
 */
public class EnemyController {
    
    private final EnemyModel model;
    private final EnemyView view;
    
    /**
     * コンストラクタ
     * 
     * @param model 管理する EnemyModel
     * @param view 管理する EnemyView
     */
    public EnemyController(EnemyModel model, EnemyView view) {
        this.model = model;
        this.view = view;
    }
    
    // --- Getter ---
    
    public EnemyModel getModel() {
        return model;
    }
    
    public EnemyView getView() {
        return view;
    }
}
