package online.nonamekill.android.module.qqfile;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import com.lxj.xpopup.XPopup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import online.nonamekill.android.module.qqfile.util.FileUriUtils;
import online.nonamekill.common.module.BaseModule;
import online.nonamekill.common.util.ThreadUtil;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ModuleQQFile extends BaseModule {
    // 2022年11月27日21点
    private static final int REQUEST_DATA_ALL_CODE = 2022112721;

    private static final String QQ_FILE_RECV = "Android/data/com.tencent.mobileqq/Tencent/QQfile_recv";
    private static final String PRIMARY_QQ_FILE_RECV = "/tree/primary:Android/data/document/primary:";
    // nt_qq_ 频道的标识符 由于只能获取ROOT权限或者虚拟机下才能访问，故放弃实现
    // private static final String FILE_ORI = "/File/Ori";
    private final DateFormat dateTimeFormat = SimpleDateFormat.getDateTimeInstance();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.module_qqfile_layout, null);

        Button viewById = inflate.findViewById(R.id.module_qqfile_button);
        viewById.setOnClickListener((v) -> {
            try {
                /*Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata/com.tencent.mobileqq");
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                getActivity().startActivityForResult(intent, 6666);*/
                checkPermission();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return inflate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkPermission() {
        // 检测是否授权
        if (!FileUriUtils.isGrant(getActivity(), "Android/data")) {
            FileUriUtils.startForRoot(getActivity(), REQUEST_DATA_ALL_CODE);
            return;
        }
        // 获取qq下载的文件路径
        DocumentFile documentFile = FileUriUtils.getTreeDocumentFile(DocumentFile.fromTreeUri(getActivity(),
                Uri.parse(FileUriUtils.changeToUri3("Android/data"))), QQ_FILE_RECV);
        // 是否存在
        if (Objects.isNull(documentFile)) {
            Toast.makeText(getContext(), "未找到QQ下载的路径！", Toast.LENGTH_SHORT).show();
            return;
        }
        refresh();
    }

    void refresh() {
        DocumentFile documentFile = FileUriUtils.getTreeDocumentFile(DocumentFile.fromTreeUri(getActivity(),
                Uri.parse(FileUriUtils.changeToUri3("Android/data"))), QQ_FILE_RECV);

        if (Objects.isNull(documentFile)) {
            Toast.makeText(getContext(), "未找到QQ下载的路径！", Toast.LENGTH_SHORT).show();
            return;
        }
        ThreadUtil.execute(() -> {
            List<Map<String, String>> array = new ArrayList<>();
            DocumentFile[] documentFiles = documentFile.listFiles();
            Arrays.stream(documentFiles)
                    .filter(DocumentFile::isFile)
                    .filter(file -> file.getName().endsWith(".zip") || file.getName().endsWith(".7z"))
                    .forEach(file -> {
                        Map<String, String> object = new HashMap<>();
                        String name = file.getName();
                        object.put("name", name);
                        object.put("date", dateTimeFormat.format(file.lastModified()));
                        object.put("path", file.getUri().getPath().replace(PRIMARY_QQ_FILE_RECV, ""));
//                    object.put("size", FileUtil.getFileSize(file.length()));
                        array.add(object);
                    });
            System.out.println(array);
            XPopup.Builder builder = new XPopup.Builder(getContext());
            builder.isViewMode(true)
                    .isDestroyOnDismiss(true)        // 设置使用完后就销毁
                    .isDarkTheme(false)             // 使用暗色主题
                    .hasStatusBar(false)            // 设置是否显示状态栏
                    .dismissOnTouchOutside(false)   // 设置点击弹窗外面是否关闭弹窗，默认为true
                    .dismissOnBackPressed(false);   // 设置按下返回键是否关闭弹窗，默认为true
            builder.asConfirm("QQ下载的zip和7z文件", array.toString(), () -> {
            }).show();
        });
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            Uri uri;
            if (Objects.isNull(intent)) {
                return;
            }
            if (Objects.nonNull(uri = intent.getData())) {
                //关键是这里，这个就是保存这个目录的访问权限
                getActivity().getContentResolver()
                        .takePersistableUriPermission(uri, intent.getFlags()
                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
//                RxToast.success("授权目录成功");
                Toast.makeText(getContext(), "授权目录成功", Toast.LENGTH_SHORT).show();
                //refresh();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getContext(), "取消目录授权", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public String getName() {
        return "QQ文件";
    }
}
