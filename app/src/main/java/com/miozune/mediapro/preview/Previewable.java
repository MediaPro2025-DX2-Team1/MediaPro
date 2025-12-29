package com.miozune.mediapro.preview;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * プレビュー可能なコンポーネントを示すアノテーション。
 * このアノテーションを付与したコンポーネントは、
 * PreviewLauncherで単体プレビューが可能になる。
 * 
 * <p>プレビュー用のインスタンス生成は以下の優先順位で行われる:
 * <ol>
 *   <li>public static [Type] createPreview() メソッド（推奨）</li>
 *   <li>public no-arg constructor（フォールバック）</li>
 * </ol>
 * 
 * <p>createPreview()メソッドでは、ダミーデータの設定やイベントリスナーの追加など、
 * プレビューに必要な初期化を完結させる。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Previewable {
    
    /**
     * プレビュー時に表示される説明。
     * 指定しない場合はクラス名が使用される。
     *
     * @return コンポーネントの説明
     */
    String description() default "";
}
