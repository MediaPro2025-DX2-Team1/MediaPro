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

1. プレビューしたいコンポーネントで`com.miozune.mediapro.preview.Previewable`インターフェースを実装する
2. `com.miozune.mediapro.preview.PreviewLauncher`で`registerComponent`を呼び出して登録する

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
