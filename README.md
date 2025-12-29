# メディプロ DX2 チーム1

## メンバー

- miozune
- domy29
- ryo

## コマンド

### ゲーム立ち上げ

```bash
./gradlew run
```

### ビルド

```bash
./gradlew build
```

## プレビューについて

作成したコンポーネントを、ゲーム自体を立ち上げることなく見ることができる機能です。

### プレビューの登録

1. プレビューしたいコンポーネントに`@Previewable`アノテーションを付与する
2. `public static [Type] createPreview()`メソッドを実装する（推奨）
   - このメソッド内でダミーデータの設定やイベントリスナーの追加を行う
   - 実装しない場合は引数なしコンストラクタが使用される
3. `@Previewable`を付与した`JComponent`は自動的に検出・登録される

#### 実装例

```java
import com.miozune.mediapro.preview.Previewable;
import javax.swing.JPanel;

@Previewable(description = "タイトル画面のプレビュー")
public class TitleView extends JPanel {
    
    public TitleView() {
        // UI初期化
    }
    
    public static TitleView createPreview() {
        TitleView view = new TitleView();
        // プレビュー用のダミーリスナー追加
        view.getStartButton().addActionListener(
            e -> System.out.println("[Preview] Start clicked")
        );
        return view;
    }
}
```

### プレビューの閲覧

```bash
# プレビュー可能なコンポーネントの一覧
# Mac/Linux
./preview.sh list
# Windows
./preview.bat list

# 特定のコンポーネントをプレビューする
# Mac/Linux
./preview.sh <ComponentName>
# Windows
./preview.bat <ComponentName>
```
