package online.nonamekill.common.util;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Objects;


@RequiresApi(api = Build.VERSION_CODES.N)
public class FileUtil {
    private static final String TAG = "FileUtil";
    private static final int MOD_K = 1024;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##");
    private static final String DO_NOT_DEL_PATH = "files";
    private static final String CACHE_CODE_PATH = "code_cache";

    public static String getFileSize(File file) {
        long length = FileUtil.folderSize(file);
        float size = FileUtil.fileSizeToMb(length);

        String suffix = " MB";
        if (size >= MOD_K) {
            size = size / MOD_K;
            suffix = " GB";
        }

        return DECIMAL_FORMAT.format(size) + suffix;
    }

    /**
     * 获取文件大小
     *
     * @param length 文件大小 Long类型
     * @return 512MB
     */
    public static String getFileSize(Long length) {
        float size = FileUtil.fileSizeToMb(length);

        String suffix = " MB";
        if (size >= MOD_K) {
            size = size / MOD_K;
            suffix = " GB";
        }

        return DECIMAL_FORMAT.format(size) + suffix;
    }

    public static float fileSizeToMb(long size) {

        float result = size * 1f / MOD_K;
        result = result / MOD_K;

        return result;
    }

    public static long folderSize(File directory) {
        long length = 0;

        if (null == directory) return length;
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (null != files) {
                for (File file : files) {
                    if (file.isFile())
                        length += file.length();
                    else
                        length += folderSize(file);
                }
            }
        } else if (directory.isFile()) {
            length = directory.length();
        }

        return length;
    }

    private static String getFileName(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }


    // 备份
    public static void backupWebContentToPath(Context context, String curPath, String toPath) {
        if (Objects.nonNull(context)) {
            ThreadUtil.execute(() -> {
                try {
                    File root = context.getFilesDir().getParentFile();

                    if (root != null) {
                        String[] files = new String[]{
                                /*"app_database",
                                "app_textures",*/
                                "app_webview",
                                /*"cache",
                                "shared_prefs"*/
                        };

                        String rootPath = root.getPath() + File.separator;
                        String backPath = curPath + "/backup/";
                        String restorePath = toPath + "/backup/";

                        // 1.backup
                        for (String file : files) {
                            copy(rootPath + file + File.separator,
                                    backPath + file + File.separator);
                        }

                        // 2.restore
                        File restoreFile = new File(restorePath);

                        if (restoreFile.exists() && restoreFile.isDirectory()) {
                            for (String file : files) {
                                String from = restorePath + file + File.separator;
                                String to = rootPath + file + File.separator;
                                File del = new File(to);
                                del.delete();
                                copy(from, to);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
//                    MsgVersionControl msg = new MsgVersionControl();
//                    msg.setMsgType(MsgVersionControl.MSG_TYPE_CHANGE_ASSET_FINISH);
//                    EventBus.getDefault().post(msg);
                }
            });
        }
    }

    public static void copyFileUsingFileChannels(String source, String dest) {
        try (FileChannel inputChannel = new FileInputStream(source).getChannel();
             FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copy(String fromFile, String toFile) {
        File[] currentFiles;
        File root = new File(fromFile);

        if (!root.exists()) {
            return;
        }

        currentFiles = root.listFiles();

        File targetDir = new File(toFile);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        if (null != currentFiles) {
            for (File currentFile : currentFiles) {
                if (currentFile.isDirectory()) {
                    copy(currentFile.getPath() + "/", toFile + currentFile.getName() + "/");
                } else {
                    copyFileUsingFileChannels(currentFile.getPath(), toFile + currentFile.getName());
                }
            }
        }
    }









    // 移植 hutool 工具包


    /**
     * 删除文件或者文件夹<br>
     * 路径如果为相对路径，会转换为ClassPath路径！
     * 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param fullFileOrDirPath 文件或者目录的路径
     * @return 成功与否
     * @throws IOException IO异常
     */
    public static boolean del(String fullFileOrDirPath) throws IOException {
        return del(new File(fullFileOrDirPath));
    }

    /**
     * 删除文件或者文件夹<br>
     * 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param file 文件对象
     * @return 成功与否
     * @throws IOException IO异常
     */
    public static boolean del(File file) throws IOException {
        if (file == null || !file.exists()) {
            return true;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File childFile : files) {
                boolean isOk = del(childFile);
                if (!isOk) {
                    // 删除一个出错则本次删除任务失败
                    return false;
                }
            }
        }
        return file.delete();
    }
}
