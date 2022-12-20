package online.nonamekill.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import online.nonamekill.common.GameLog;

public class ShareUtils {

    private static final String TAG = "SHARE_UTILS";
    private static final AtomicReference<File> fileAtomicReference = new AtomicReference<>();
    private static final int REQUEST_CODE_TEXT = 20113;
    private static final int REQUEST_CODE_FILE = 20114;
    // QQ分享文件的缓存地址，会一直存放的，需要清理一下
    private static final String cacheSharePath = "Android/data/com.tencent.mobileqq/cache/share/";

    // 优先调用qq的分享接口，如果不存在，那么调用系统的分享接口
    public static void shareFileQqOrSystem(Context context, Activity activity, File file) {
        if (PlatformUtil.isQQClientAvailable(context)) {
            shareFileForQQ(context, activity, file);
        } else {
            shareFileForSystem(context, activity, file);
        }
    }


    /**
     * 分享文件给QQ
     *
     * @param file
     */
    public static void shareFileForQQ(Context context, Activity activity, File file) {
        if (Objects.nonNull(file) && file.exists()) {
            Intent qqIntent = new Intent(Intent.ACTION_SEND);
            Uri shareFileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            //添加权限 这一句表示对目标应用临时授权该Uri所代表的文件
            qqIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            qqIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            qqIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            qqIntent.setType(getMimeType(file.getPath()));
            qqIntent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
            qqIntent.putExtra(Intent.EXTRA_STREAM, shareFileUri);
            fileAtomicReference.set(file);
            startActivityForResult(activity, qqIntent);
        } else {
            RxToast.error(activity, "要分享的文件不存在!");
        }
    }

    /**
     * 分享文件 - 调用系统方法
     *
     * @param file 传过来一个文件
     */
    public static void shareFileForSystem(Context context, Activity activity, File file) {
        // 获取文件共享目录的路径
        Uri imageUri = FileProvider.getUriForFile(context,
                context.getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        fileAtomicReference.set(file);
        Log.d("share", "uri:" + imageUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);  //传输图片或者文件 采用流的方式
        intent.setType("*/*");   //分享文件
        startActivityForResult(activity, Intent.createChooser(intent, "分享"));
    }


    private static void startActivityForResult(Activity context, Intent intent) {
        checkFileUriExposure();
        context.startActivityForResult(intent, REQUEST_CODE_FILE);
    }

    private static void checkFileUriExposure() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }


    /**
     * 根据文件后缀获取文件MIME类型
     *
     * @param filePath 文件路径
     * @return
     */
    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

    //  分享后的回调方法，无论成功失败，我需要把之前生成的文件给删掉
    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
        if (!Objects.equals(requestCode, REQUEST_CODE_FILE)) return;
        try {
            delThreadLocalFile();
            delIntentFile(intent);
            delQQCacheShareFile(activity);
        }catch (RuntimeException e){
            RxToast.error(activity, "删除临时分享文件异常：" + e.getMessage());
            GameLog.e(TAG, e);
        }finally {
            fileAtomicReference.set(null);
        }
    }

    // 删除threadLocal里面存放的文件
    private static void delThreadLocalFile() {
        File file = fileAtomicReference.get();
        new Handler().postDelayed(() -> {
            if (file != null && file.exists()) {
                try {
                    FileUtil.del(file);
                } catch (IOException e) {
                    GameLog.v(TAG, "删除分享文件失败！");
                    e.printStackTrace();
                }
            }
        }, 500);
    }

    // 删除Intent返回的文件
    private static void delIntentFile(Intent intent) {
        if (Objects.isNull(intent)) return;
        Uri data = intent.getData();
        if (Objects.isNull(data)) return;
        String path = data.getPath();
        try {
            FileUtil.del(path);
        } catch (IOException e) {
            GameLog.v(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void delQQCacheShareFile(Activity activity) {
        if(!FileUriUtils.isGrant(activity, "Android/data")) {
            RxToast.error(activity, "没有授权Android/data目录，无法删除QQ缓存的分享文件");
            return;
        }
        // 从变量里面取到分享的文件
        File file = fileAtomicReference.get();
        if(Objects.isNull(file)) return;
        // 查看有没有这个文件
        DocumentFile cacheShareFile = FileUriUtils.getTreeDocumentFile(DocumentFile.fromTreeUri(activity,
                Uri.parse(FileUriUtils.changeToUri3("Android/data"))), cacheSharePath + file.getName());
        // 可能没赋予权限，或者没能找到文件
        if(Objects.isNull(cacheShareFile) || !cacheShareFile.exists()) return;
        // 删掉缓存文件，防止占用户存储空间
        cacheShareFile.delete();
    }


}
