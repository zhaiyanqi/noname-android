package online.nonamekill.common.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import online.nonamekill.common.Constant;

public class GameResourceUtil {
    private static final String TAG = "GameResourceUtil";

    public static class onCopyListener {
        public void onProgressChanged(int progress) {
        }

        public void onSizeIncrease() {
        }

        public void onFinish() {
        }
    }

    public static void copyAssetToGameFolder(Context context, String folderName, onCopyListener listener) {
        String gameFolder = context.getExternalFilesDir(null).getAbsolutePath();
        AssetManager assetManager = context.getAssets();

        ArrayList<String> paths = fetchAllPath(assetManager, folderName, listener);

        listener.onProgressChanged(20);

        int i = 0;
        int size = paths.size();

        for (String path : paths) {
            listener.onProgressChanged(20 + (i * 80 / size));
            i++;
            copyAssetFileToTarget(assetManager, gameFolder, path);
        }

        listener.onFinish();
    }

    private static ArrayList<String> fetchAllPath(AssetManager assetManager, String res, onCopyListener listener) {
        String[] files = new String[0];
        ArrayList<String> resources = new ArrayList<>();

        try {
            files = assetManager.list(res);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (files.length > 0) {
            for (String file : files) {
                resources.addAll(fetchAllPath(assetManager, res + File.separator + file, listener));
            }
        } else {
            resources.add(res);
            listener.onSizeIncrease();
        }

        return resources;
    }
    private static void copyAssetFileToTarget(AssetManager assetManager, String gameFolder,
            String file) {
        try {
            File outFile = new File(gameFolder + File.separator + file);

            outFile.delete();
            outFile.getParentFile().mkdirs();
            outFile.createNewFile();

            FileOutputStream out = new FileOutputStream(outFile);
            InputStream in = assetManager.open(file);

            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkIfGamePath(File file) {
        if (null != file) {
            File[] gameFolders = file.listFiles(dir -> dir.isDirectory() && Constant.GAME_FOLDER_NAME.equals(dir.getName()));

            if ((null != gameFolders) && gameFolders.length == 1) {
                File gameFolder = gameFolders[0];
                File[] gameJs = gameFolder.listFiles(f -> f.isFile() && Constant.GAME_FILE.equals(f.getName()));

                return (null != gameJs) && (gameJs.length > 0);
            }
        }

        return false;
    }
}
