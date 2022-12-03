package online.nonamekill.android.module.qqfile.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import online.nonamekill.common.util.AppUtils;

public class FileUriUtils {
    public static String root = Environment.getExternalStorageDirectory().getPath() + "/";

    private static final String PRIVATE_DOCUMENT_TREE = "/tree/primary:Android/data/document/primary:";


    // 判断是否已经获取了Data权限，改改逻辑就能判断其他目录，懂得都懂
    public static boolean isGrant(Context context,String path) {
        path = path.replace("/", "%2F");
        for (UriPermission persistedUriPermission : context.getContentResolver().getPersistedUriPermissions()) {
            if (persistedUriPermission.isReadPermission() && persistedUriPermission.getUri().toString().equals("content://com.android.externalstorage.documents/tree/primary%3A"+path)) {
                return true;
            }
        }
        return false;
    }

    //直接返回DocumentFile
    public static DocumentFile getDocumentFilePath(Context context, String path, String sdCardUri) {
        DocumentFile document = DocumentFile.fromTreeUri(context, Uri.parse(sdCardUri));
        String[] parts = path.split("/");
        for (int i = 3; i < parts.length; i++) {
            document = document.findFile(parts[i]);
        }
        return document;
    }

    //转换至uriTree的路径
    public static String changeToUri(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2;
    }

    //替换掉Android/data目录
    public static String replaceAndroidData(String path) {
        if(TextUtils.isEmpty(path)) return path;
        return path.replaceFirst("/storage/emulated/0/Android/data/"+ AppUtils.getAppPackageName() + "/", "");
    }
    public static String replacePath(String path) {
        if(TextUtils.isEmpty(path)) return path;
        return path.replaceFirst("/storage/emulated/0/Android", "")
                .replaceFirst("/data/","")
                .replaceFirst(AppUtils.getAppPackageName() + "/", "");
    }

    //转换至uriTree的路径 获取沙盒路径
    public static DocumentFile getDocumentFile(Context context, String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return DocumentFile.fromSingleUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2));
    }

    public static DocumentFile getTreeDocumentFile(DocumentFile documentFile, String path) {
        path = path.replace("Android/data/", "");
        List<String> list = Arrays.asList(path.split(File.separator));
        Queue<String> queue = new LinkedList<>(list);
        return getTreeDocumentFile(documentFile, queue);
    }

    public static DocumentFile getTreeDocumentFile(DocumentFile documentFile, Queue<String> path) {
        if (!documentFile.exists()) return documentFile;
        String replace = documentFile.getUri().getPath().replace(PRIVATE_DOCUMENT_TREE, "");
        if (documentFile.isDirectory()) {
            String poll = path.poll();
            DocumentFile anElse = Arrays.stream(documentFile.listFiles())
                    .parallel().filter(file -> {
                        String filePath = file.getUri().getPath().replace(PRIVATE_DOCUMENT_TREE, "");
                        return filePath.endsWith(poll);
                    }).findAny().orElse(null);
            if (Objects.nonNull(anElse)) {
                if (path.size() > 0)
                    return getTreeDocumentFile(anElse, path);
                return anElse;
            }
            return null;
        } else {
            if (path.equals(replace)) {
                return documentFile;
            }
            return null;
        }
    }


    //转换至uriTree的路径
    public static String changeToUri2(String path) {
        String[] paths = path.replaceAll("/storage/emulated/0/Android/data", "").split("/");
        StringBuilder stringBuilder = new StringBuilder("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata");
        for (String p : paths) {
            if (p.length() == 0) continue;
            stringBuilder.append("%2F").append(p);
        }
        return stringBuilder.toString();

    }


    //转换至uriTree的路径
    public static String changeToUri3(String path) {
        path = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return ("content://com.android.externalstorage.documents/tree/primary%3A" + path);

    }

    //获取指定目录的权限
    public static void startFor(String path, Activity context, int REQUEST_CODE_FOR_DIR) {
        String uri = changeToUri(path);
        Uri parse = Uri.parse(uri);
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, parse);
        }
        context.startActivityForResult(intent, REQUEST_CODE_FOR_DIR);

    }

    //直接获取data权限，推荐使用这种方案
    public static void startForRoot(Activity context, int REQUEST_CODE_FOR_DIR) {
        Uri uri1 = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");
        String uri = changeToUri(Environment.getExternalStorageDirectory().getPath());
        uri = uri + "/document/primary%3A" + Environment.getExternalStorageDirectory().getPath().replace("/storage/emulated/0/", "").replace("/", "%2F");
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri1);
        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        intent1.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.getUri());
        context.startActivityForResult(intent1, REQUEST_CODE_FOR_DIR);
    }
}
