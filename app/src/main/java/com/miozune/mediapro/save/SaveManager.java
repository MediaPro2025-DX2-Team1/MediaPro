package com.miozune.mediapro.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.miozune.mediapro.progress.ProgressModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * セーブデータの保存と読み込みを管理するクラス。
 * JSON形式でホームディレクトリ配下（~/.mediapro/save.json）に保存します。
 */
public class SaveManager {

    private static final String SAVE_DIR = ".mediapro";
    private static final String SAVE_FILE = "save.json";
    private final Gson gson;
    private final Path savePath;

    /**
     * SaveManagerを作成します。
     * セーブファイルのパスはホームディレクトリ配下の .mediapro/save.json となります。
     */
    public SaveManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        String homeDir = System.getProperty("user.home");
        this.savePath = Paths.get(homeDir, SAVE_DIR, SAVE_FILE);
    }

    /**
     * 指定されたProgressModelの状態をセーブファイルに保存します。
     *
     * @param progressModel 保存する進行状況
     * @throws IOException ファイル書き込みに失敗した場合
     */
    public void save(ProgressModel progressModel) throws IOException {
        SaveData saveData = new SaveData(
                progressModel.getClearedStages(),
                progressModel.getUnlockedStages()
        );

        // ディレクトリが存在しない場合は作成
        Path saveDir = savePath.getParent();
        if (!Files.exists(saveDir)) {
            Files.createDirectories(saveDir);
        }

        // JSON形式で保存
        String json = gson.toJson(saveData);
        Files.writeString(savePath, json);
    }

    /**
     * セーブファイルから進行状況を読み込み、ProgressModelに復元します。
     * セーブファイルが存在しない場合やJSON解析に失敗した場合は、デフォルトの状態（stage1のみアンロック）となります。
     *
     * @param progressModel 復元先のProgressModel
     * @return ロードに成功した場合true、失敗した場合false
     */
    public boolean load(ProgressModel progressModel) {
        if (!Files.exists(savePath)) {
            System.out.println("セーブファイルが見つかりません: " + savePath);
            return false;
        }

        try {
            String json = Files.readString(savePath);
            SaveData saveData = gson.fromJson(json, SaveData.class);

            if (saveData != null) {
                progressModel.restore(
                        saveData.getClearedStages(),
                        saveData.getUnlockedStages()
                );
                System.out.println("セーブデータを読み込みました: " + savePath);
                return true;
            } else {
                System.err.println("セーブデータの解析に失敗しました");
                return false;
            }
        } catch (IOException e) {
            System.err.println("セーブファイルの読み込みに失敗しました: " + e.getMessage());
            return false;
        } catch (JsonSyntaxException e) {
            System.err.println("セーブファイルのJSON解析に失敗しました: " + e.getMessage());
            return false;
        }
    }

    /**
     * セーブファイルが存在するかどうかを判定します。
     *
     * @return セーブファイルが存在する場合true
     */
    public boolean saveFileExists() {
        return Files.exists(savePath);
    }

    /**
     * セーブファイルのパスを取得します。
     *
     * @return セーブファイルのパス
     */
    public Path getSavePath() {
        return savePath;
    }

    /**
     * セーブファイルを削除します。
     *
     * @return 削除に成功した場合true
     */
    public boolean deleteSaveFile() {
        try {
            if (Files.exists(savePath)) {
                Files.delete(savePath);
                System.out.println("セーブファイルを削除しました: " + savePath);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("セーブファイルの削除に失敗しました: " + e.getMessage());
            return false;
        }
    }
}
