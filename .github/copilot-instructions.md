# Copilot Instructions

このドキュメントは、コーディングエージェント（GitHub Copilot）への指示書です。

## 基本ルール

- **言語**: ユーザーへの応答、コード内のコメントは**全て日本語**で行ってください。

## プロジェクト概要

- **プロジェクト名**: MediaPro
- **目的**: メディプロ DX2 チーム1のゲーム開発プロジェクト
- **技術スタック**:
  - Java 25
  - Swing（GUI）
  - Gradle（ビルドツール）

## ゲームの内容・流れ

本プロジェクトは、カードゲーム要素を含むステージ攻略型ゲームです。

### 全体フロー

1. **タイトル画面** (`title/`)
   - ゲーム開始、設定、終了の入口
2. **ワールド/ステージ選択** (`world/`)
   - マップ上で次に挑むステージを選択
3. **ステージ（バトル）** (`stage/`, `player/`, `card/`)
   - プレイヤー、カード、敵を使用した戦闘シーケンス
4. **リザルト**
   - 勝敗結果の表示、リトライまたはワールドマップへの戻り

### 画面遷移の仕組み

画面遷移は以下の原則で行います：

1. **Modelの状態更新**: 例 `gameModel.setScene(Scene.WORLD)`
2. **イベント発行**: Modelがイベントを発行
3. **View切り替え/更新**: Applicationクラス等がイベントを検知し、`CardLayout`の切り替えやViewの再描画を行う

**重要**:
- 未完成の画面への遷移アクションは、**ダミーのログ出力**（例: `System.out.println("Go to World")`）にとどめてください。
- 遷移先のViewが存在しない場合、無理にダミーのViewクラスを作成しないでください。

### 機能とパッケージの対応

| 機能・画面 | パッケージ | 役割 |
|---|---|---|
| タイトル | `title/` | ゲームのエントリポイント |
| ワールドマップ | `world/` | ステージ選択機能 |
| バトルステージ | `stage/` | 戦闘の進行管理、描画 |
| プレイヤー | `player/` | ステータス管理、UI表示 |
| カード | `card/` | カードデータの管理、デッキ処理 |

将来的に追加する画面も、原則として新しいパッケージを作成し、その中でMVCパターンを適用してください。

## パッケージ構成

ベースパッケージ: `com.miozune.mediapro`

### 既存パッケージ

| パッケージ | 説明 |
|-----------|------|
| `game/` | ゲーム全体の統括（Application, Model, Scene） |
| `title/` | タイトル画面機能 |
| `world/` | ワールドマップ画面機能 |
| `stage/` | ステージ（バトル）画面機能 |
| `player/` | プレイヤー機能 |
| `card/` | カード機能 |
| `preview/` | コンポーネントプレビュー機能 |
| `util/` | ユーティリティクラス |

### 新機能追加時のパッケージ作成

新しい機能を追加する際は、**機能ごとにパッケージを作成**してください。

```
com.miozune.mediapro/
├── title/           # タイトル画面機能
├── world/           # ワールドマップ機能
├── stage/           # ステージ（バトル）機能
├── player/          # プレイヤー機能
├── deck/            # デッキ機能
└── ...
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

- **Model**: データと状態を管理。**イベント駆動パターン**を使用して変更を通知（詳細は後述）
  - **注意**: 状態を持たない場合（Modelの中身が空になる場合）は、Modelクラスを作成しなくて良い
- **View**: UIの描画を担当。`JPanel`を継承し、Modelの変更を監視して表示を更新
  - **重要**: Modelへの参照は**可能な限り`final`フィールド**とし、コンストラクタで注入する
- **Controller**: ユーザー入力を処理し、ModelとViewを仲介
  - **注意**: まだ完成していない他のViewへの遷移処理は、ダミーのログ出力にとどめる。無理にダミーコンポーネントを作成しないこと

### 例: プレイヤー機能のパッケージ構成

イベントクラスは、**機能パッケージ内の`events`サブパッケージ**に配置します。

```
com.miozune.mediapro.player/
├── PlayerModel.java                    # プレイヤーの状態管理
├── PlayerView.java                     # プレイヤーの描画（Previewable実装）
├── PlayerController.java               # プレイヤーの入力処理
└── events/                             # イベント専用サブパッケージ
    ├── PlayerPropertyChangeEvent.java  # sealed interface（基底）
    ├── PlayerHpChangedEvent.java       # record（HP変更）
    ├── PlayerManaChangedEvent.java     # record（マナ変更）
    └── PlayerNameChangedEvent.java     # record（名前変更）
```

## イベント駆動パターン

Modelからの変更通知には、**型安全なイベント駆動パターン**を使用します。Java 17+の`sealed interface`と`record`を活用することで、コンパイル時の型チェックとパターンマッチングが可能になります。

### なぜPropertyChangeSupportではなくイベント駆動か

| 観点 | PropertyChangeSupport | イベント駆動パターン |
|------|----------------------|---------------------|
| 型安全性 | ❌ `Object`型でキャスト必須 | ✅ ジェネリクスで完全保証 |
| プロパティ名 | ❌ 文字列（タイプミスがコンパイル時に検出されない） | ✅ クラス名で保証 |
| 拡張性 | △ イベントクラスの拡張が困難 | ✅ 新しいrecordを追加するだけ |
| デバッグ | △ ブレークポイントを張りづらい | ✅ イベントクラスで簡単に追跡 |
| 複数リスナー | ✅ 標準サポート | ✅ `CopyOnWriteArrayList`で実装 |

### イベントクラスの実装

#### 1. sealed interfaceで基底イベントを定義

```java
// events/[Feature]PropertyChangeEvent.java
package com.miozune.mediapro.player.events;

public sealed interface PlayerPropertyChangeEvent 
    permits PlayerHpChangedEvent, PlayerManaChangedEvent, PlayerNameChangedEvent {
    
    PlayerModel getPlayer();
}
```

#### 2. recordで具体的なイベントを定義

```java
// events/PlayerHpChangedEvent.java
package com.miozune.mediapro.player.events;

public record PlayerHpChangedEvent(
    PlayerModel player,
    int oldHp,
    int newHp
) implements PlayerPropertyChangeEvent {
    
    @Override
    public PlayerModel getPlayer() {
        return player;
    }
    
    public int getDelta() {
        return newHp - oldHp;
    }
}
```

#### 3. Modelにリスナー管理を実装

```java
public class PlayerModel {
    
    @FunctionalInterface
    public interface PropertyChangeListener {
        void onPropertyChanged(PlayerPropertyChangeEvent event);
    }
    
    private final List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<>();
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void fireEvent(PlayerPropertyChangeEvent event) {
        for (PropertyChangeListener listener : listeners) {
            listener.onPropertyChanged(event);
        }
    }
    
    public void setHp(int hp) {
        int oldHp = this.hp;
        this.hp = hp;
        fireEvent(new PlayerHpChangedEvent(this, oldHp, hp));
    }
}
```

### イベントの購読（パターンマッチング）

```java
model.addPropertyChangeListener(event -> {
    switch (event) {
        case PlayerHpChangedEvent e -> updateHpDisplay(e.newHp());
        case PlayerManaChangedEvent e -> updateManaDisplay(e.newMana());
        case PlayerNameChangedEvent e -> updateNameDisplay(e.newName());
    }
});
```

## Viewの設計指針

### Modelフィールドはfinalにする

ViewがModelへの参照を持つ場合、**`final`フィールド**として宣言し、コンストラクタで注入します。

#### 理由

1. **不変性の保証**: Model参照が途中で変更されることを防ぎ、バグを減らす
2. **明示的な依存関係**: コンストラクタで依存性が明確になる
3. **スレッドセーフ**: finalフィールドはJavaメモリモデルで安全に公開される

#### 実装パターン

```java
public class ExampleView extends JPanel implements Previewable {
    
    private final ExampleModel model;  // finalで宣言
    
    // no-argコンストラクタ（Previewable要件）
    public ExampleView() {
        this(ExampleModel.createDefault());  // デフォルトモデルで初期化
    }
    
    // Modelを受け取るコンストラクタ
    public ExampleView(ExampleModel model) {
        this.model = model;
        setupPanel();
        initComponents();
        layoutComponents();
        
        setupModelListener();
        updateAllDisplays();
    }
    
    private void setupModelListener() {
        model.addPropertyChangeListener(event -> {
            switch (event) {
                case ExampleValueChangedEvent e -> updateDisplay(e.newValue());
                // ... 他のイベント
            }
        });
    }
    
    public ExampleModel getModel() {
        return model;
    }
}
```

### Previewable対応のポイント

- **no-argコンストラクタ**ではデフォルトまたはダミーのModelを作成
- **setupPreview()** ではModelの値を変更してプレビュー状態を設定
- Modelがfinalでも、**Modelの中身（プロパティ）は変更可能**


## Controllerの設計指針

### Modelを直接操作する（コールバックの禁止）

Controllerは、操作対象となるModel（`GameModel`を含む）への参照をフィールドとして保持し、メソッドを直接呼び出して状態を変更してください。

`Runnable` や `Consumer` などのコールバックを使用して、処理を親クラス（`GameApplication`等）に委譲することは**禁止**します。これはMVCの「ControllerがModelを操作する」という原則に反し、フローを複雑にするためです。

#### 悪い例（コールバック使用）

```java
public class TitleController {
    // ❌ コールバックで処理を外部に逃している
    public TitleController(TitleView view, Runnable onStart) {
        view.getStartButton().addActionListener(e -> onStart.run());
    }
}
```

#### 良い例（Model直接操作）

```java
public class TitleController {
    private final GameModel gameModel;

    // ✅ Modelを受け取り、直接操作する
    public TitleController(TitleView view, GameModel gameModel) {
        this.gameModel = gameModel;
        view.getStartButton().addActionListener(e -> gameModel.goToWorld());
    }
}
```

### 画面遷移のフロー

画面遷移もModelの状態変更の一種として扱います。

1. **Controller**: ユーザー操作を受け、`GameModel` のメソッド（例: `goToWorld()`）を呼ぶ
2. **Model**: 状態を更新し、イベント（例: `GameSceneChangedEvent`）を発行する
3. **Application**: イベントを検知し、`CardLayout` 等を切り替える

## Previewableの実装

新しいViewコンポーネントを作成する際は、**必ず`Previewable`インターフェースを実装**してください。これにより、ゲーム全体を起動せずにコンポーネント単体でプレビューできます。

### 1. Previewableインターフェースの実装

```java
package com.miozune.mediapro.example;

import com.miozune.mediapro.preview.Previewable;
import javax.swing.JPanel;

public class ExampleView extends JPanel implements Previewable {
    
    // no-argコンストラクタが必須
    public ExampleView() {
        // 初期化処理
    }
    
    @Override
    public String getPreviewDescription() {
        return "サンプルビューの説明";
    }
    
    @Override
    public void setupPreview() {
        // プレビュー用のダミーデータをセットアップ
    }
}
```

**重要**: `Previewable`を実装した`JComponent`のサブクラスは、**no-argコンストラクタ（引数なしのコンストラクタ）を持つ必要があります**。これにより、クラスパススキャンによって自動的に検出・登録されます。

### 2. 自動登録

`Previewable`を実装し、no-argコンストラクタを持つコンポーネントは、`com.miozune.mediapro`パッケージ配下に配置することで**自動的に登録されます**。手動での登録は不要です。

### 3. プレビューの実行

```bash
# 登録されたコンポーネント一覧を表示
./preview.sh list

# 特定のコンポーネントをプレビュー
./preview.sh ExampleView
```

### 4. プレビュー実装のポイント

- **UI確認**: UI配置、遷移ラベル、コンポーネントの初期状態が正しく表示されることを確認できるようにしてください。
- **遷移アクション**: プレビュー実行時は実際のシーン遷移を行わず、ボタン押下時に「ログ出力」で動作を確認できるようにしてください。

## コーディング規約

### インポート（Import） (重要)

- **原則としてimport文を使用してください**。コード本文中で完全修飾名（パッケージ名を含むクラス名）を使用することは避けてください。
- 完全修飾名の使用は、**クラス名の衝突**（例: `java.util.List` と `java.awt.List`）がある場合にのみ許可されます。

## 実装後のチェック手順

実装が完了したら、以下の順番でチェックを実行してください。

### 1. 完全修飾名のチェック

```bash
./check-fqn.sh
```

このスクリプトは、import文を使用すべき箇所で完全修飾名が使われていないかをチェックします。検出された場合は、import文を追加して修正してください。

### 2. ビルドの確認

```bash
./gradlew build
```

最終的にプロジェクト全体がビルドできることを確認します。ビルドエラーがある場合は修正してください。

**注意**: コードフォーマット（`spotlessApply`）は、ビルド時に自動的に適用されます。手動で実行する必要はありません。
