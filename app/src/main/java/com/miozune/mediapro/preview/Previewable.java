package com.miozune.mediapro.preview;

/**
 * プレビュー可能なコンポーネントを表すインターフェース。
 * このインターフェースを実装したコンポーネントは、
 * PreviewLauncherで単体プレビューが可能になる。
 */
public interface Previewable {
    
    /**
     * プレビュー時に表示される説明を取得する。
     *
     * @return コンポーネントの説明
     */
    String getPreviewDescription();
    
    /**
     * プレビュー用のセットアップを行う。
     * ダミーデータの設定などを行う。
     */
    void setupPreview();
}
