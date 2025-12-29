# Copilot Instructions

このドキュメントは、コーディングエージェント（GitHub Copilot）への指示書です。

## プロジェクト概要

- **プロジェクト名**: MediaPro
- **目的**: メディプロ DX2 チーム1のゲーム開発プロジェクト
- **技術スタック**:
  - Java 25
  - Swing（GUI）
  - Gradle（ビルドツール）

## パッケージ構成

ベースパッケージ: `com.miozune.mediapro`

### 既存パッケージ

| パッケージ | 説明 |
|-----------|------|
| `core/` | ゲームの基盤となるクラス（GameLoop, GameState等） |
| `game/` | ゲーム本体のMVCクラス |
| `preview/` | コンポーネントプレビュー機能 |
| `util/` | ユーティリティクラス |

### 新機能追加時のパッケージ作成

新しい機能を追加する際は、**機能ごとにパッケージを作成**してください。

```
com.miozune.mediapro/
├── title/           # タイトル画面機能
├── battle/          # バトル機能
├── inventory/       # インベントリ機能
└── settings/        # 設定画面機能
```

## MVCモデル

このプロジェクトは**MVCモデル**で構成されています。新機能を開発する際は、機能パッケージ内に以下の3つのクラスを作成してください。

### 命名規則

| 役割 | 命名規則 | 例（title機能の場合） |
|------|---------|---------------------|
| Model | `[Feature]Model` | `TitleModel` |
| View | `[Feature]View` | `TitleView` |
| Controller | `[Feature]Controller` | `TitleController` |

### 各クラスの責務

- **Model**: データと状態を管理。`PropertyChangeSupport`を使用して変更を通知
  - **注意**: 状態を持たない場合（Modelの中身が空になる場合）は、Modelクラスを作成しなくて良い
- **View**: UIの描画を担当。`JPanel`を継承し、Modelの変更を監視して表示を更新
- **Controller**: ユーザー入力を処理し、ModelとViewを仲介
  - **注意**: まだ完成していない他のViewへの遷移処理は、ダミーのログ出力にとどめる。無理にダミーコンポーネントを作成しないこと

### 例: title機能のパッケージ構成

```
com.miozune.mediapro.title/
├── TitleModel.java       # タイトル画面の状態管理
├── TitleView.java        # タイトル画面の描画（Previewable実装）
└── TitleController.java  # タイトル画面の入力処理
```

## プレビューシステム

新しいViewコンポーネントを作成する際は、**必ず`@Previewable`アノテーションを付与**してください。これにより、ゲーム全体を起動せずにコンポーネント単体でプレビューできます。

### 1. @Previewableアノテーションの付与

```java
package com.miozune.mediapro.example;

import com.miozune.mediapro.preview.Previewable;
import javax.swing.JPanel;

@Previewable(description = "サンプルビューの説明")
public class ExampleView extends JPanel {
    
    public ExampleView() {
        // 初期化処理
    }
    
    /**
     * プレビュー用のインスタンスを生成する。
     * ダミーデータの設定やイベントリスナーの追加を行う。
     */
    public static ExampleView createPreview() {
        ExampleView view = new ExampleView();
        // ダミーデータの設定
        // イベントリスナーの追加
        return view;
    }
}
```

**重要ポイント:**

1. **@Previewableアノテーション**: クラスに`@Previewable`アノテーションを付与します。`description`パラメータはオプションで、省略するとクラス名が使用されます。

2. **createPreview()静的メソッド（推奨）**: プレビュー用のインスタンスを生成する`public static [Type] createPreview()`メソッドを実装します。このメソッド内で:
   - インスタンスを生成
   - プレビュー用のダミーデータを設定
   - プレビュー用のイベントリスナーを追加
   - 完全に初期化されたインスタンスを返す

3. **no-argコンストラクタ（フォールバック）**: `createPreview()`メソッドが実装されていない場合、引数なしコンストラクタが使用されます（ただしWARNログが出力されます）。シンプルなViewでは引数なしコンストラクタのみでも動作しますが、`createPreview()`の実装を推奨します。

4. **実運用コンストラクタ**: Viewが依存関係（ModelやController）を必要とする場合、それらを引数に取る別のコンストラクタを自由に作成できます。プレビューはあくまで`createPreview()`を通じて行われるため、実運用のコンストラクタ設計に制約はありません。

### 2. 自動登録

`@Previewable`アノテーションを持つコンポーネントは、`com.miozune.mediapro`パッケージ配下に配置することで**自動的に検出・登録されます**。手動での登録は不要です。

### 3. プレビューの実行

```bash
# 登録されたコンポーネント一覧を表示
./preview.sh list

# 特定のコンポーネントをプレビュー
./preview.sh ExampleView
```

### 4. 設計パターン例

#### パターンA: 依存関係なしのシンプルなView

```java
@Previewable(description = "タイトル画面")
public class TitleView extends JPanel {
    private JButton startButton;
    
    public TitleView() {
        // UI初期化
    }
    
    public static TitleView createPreview() {
        TitleView view = new TitleView();
        // プレビュー用のダミーリスナー追加
        view.startButton.addActionListener(e -> 
            System.out.println("[Preview] Start clicked"));
        return view;
    }
}
```

#### パターンB: Modelを必要とするView

```java
@Previewable(description = "カード表示コンポーネント")
public class CardView extends JPanel {
    private final CardModel model;
    
    // 実運用用コンストラクタ
    public CardView(CardModel model) {
        this.model = model;
        initUI();
    }
    
    // プレビュー用ファクトリーメソッド
    public static CardView createPreview() {
        return new CardView(CardModel.createSample());
    }
}
```

#### パターンC: 複雑な初期化が必要なView

```java
@Previewable(description = "戦闘画面")
public class BattleView extends JPanel {
    
    public BattleView() {
        initUI();
    }
    
    public static BattleView createPreview() {
        BattleView view = new BattleView();
        // プレビュー用のダミーデータを設定
        view.updatePlayerHP(80);
        view.updateEnemyHP(50);
        // プレビュー用のイベントリスナー追加
        view.getAttackButton().addActionListener(e -> 
            System.out.println("[Preview] Attack clicked"));
        return view;
    }
}
```
