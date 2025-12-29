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
