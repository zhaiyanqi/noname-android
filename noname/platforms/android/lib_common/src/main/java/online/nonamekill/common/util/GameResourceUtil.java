package online.nonamekill.common.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import online.nonamekill.common.Constant;

public class GameResourceUtil {
    private static final String TAG = "GameResourceUtil";

    public static boolean checkAssetContext(Context context) {
        try {
            Context assetContext = context.createPackageContext(Constant.ASSET_PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);

            if (null == assetContext) {
                return false;
            }

            AssetManager assets = assetContext.getAssets();

            if (null == assets) {
                return false;
            }

            String[] list = assets.list(Constant.GAME_FOLDER);

            return (null != list) && (list.length != 0);
        } catch (PackageManager.NameNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static class onCopyListener {
        public void onSingleTaskFetch() {
        }

        public void onFinish(int count) {
        }

        public void onBegin(int sum) {

        }
    }

    private static final int EACH_THREAD_COUNT = 1500;

    public static void copyAssetToGameFolder(Context context, Context assetContext, String folderName, onCopyListener listener) {
        String gameFolder = context.getExternalFilesDir(null).getAbsolutePath();
        AssetManager assetManager = assetContext.getAssets();

        ArrayList<String> paths = fetchAllPath(assetManager, folderName, listener);
        int size = paths.size();

        // 多线程复制
        int count = size / EACH_THREAD_COUNT + 1;
        int start = 0;
        listener.onBegin(size);

        for (int i = 0; i < count; i++) {
            final int begin = start;
            final int end = Math.min(start + EACH_THREAD_COUNT, size);

            ThreadUtil.execute(() -> {
                for (int j = begin; j < end; j++) {
                    listener.onFinish(1);
                    copyAssetFileToTarget(assetManager, gameFolder, paths.get(j));
                }
            });

            start = start + EACH_THREAD_COUNT;
        }
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
            listener.onSingleTaskFetch();
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

    public static boolean checkGameResource(Context context) {
        return GameResourceUtil.checkIfGamePath(getGameFileFolder(context), null);
    }

    public static File getGameFileFolder(Context context) {
        return context.getExternalFilesDir(Constant.GAME_FOLDER);
    }

    public interface OnCheckResult {
        void onCheck(boolean exist);
    }

    public static boolean checkIfGamePath(File file, OnCheckResult listener) {
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
