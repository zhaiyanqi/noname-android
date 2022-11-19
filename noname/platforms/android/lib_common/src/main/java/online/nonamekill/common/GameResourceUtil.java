package online.nonamekill.common;

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

public class GameResourceUtil {
    private static final String TAG = "GameResourceUtil";

    public static void copyAssetToGameFolder(Context context, String folderName, Runnable onFinish) {
        String gameFolder = context.getExternalFilesDir(null).getAbsolutePath();
        AssetManager assetManager = context.getAssets();

        ArrayList<String> paths = fetchAllPath(assetManager, folderName);

        for (String path : paths) {
            copyAssetFileToTarget(assetManager, gameFolder, path);
        }

        onFinish.run();
    }

    private static ArrayList<String> fetchAllPath(AssetManager assetManager, String res) {
        String[] files = new String[0];
        ArrayList<String> resources = new ArrayList<>();

        try {
            files = assetManager.list(res);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (files.length > 0) {
            for (String file : files) {
                resources.addAll(fetchAllPath(assetManager, res + File.separator + file));
            }
        } else {
            resources.add(res);
        }

        return resources;
    }
    private static void copyAssetFileToTarget(AssetManager assetManager, String gameFolder, String file) {
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
}
