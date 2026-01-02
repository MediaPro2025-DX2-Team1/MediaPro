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
