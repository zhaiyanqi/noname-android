package online.nonamekill.android.module.qqfile;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import com.alibaba.fastjson.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import online.nonamekill.android.module.qqfile.util.FileUriUtils;
import online.nonamekill.common.util.FileUtil;
import online.nonamekill.common.util.RxToast;
import online.nonamekill.common.util.ThreadUtil;
import online.nonamekill.common.util.XPopupUtil;
import online.nonamekill.common.versionAdapter.AdapterListAbstract;
import online.nonamekill.common.versionAdapter.VersionData;
import online.nonamekill.common.versionAdapter.VersionListRecyclerAdapter;

public class ModuleQQFile extends AdapterListAbstract {
    // 2022年11月27日21点
    private static final int REQUEST_DATA_ALL_CODE = 2022112721;
    // 未授权
    private static final int UNAUTHORIZED = 0;
    private final String QQ_FILE_RECV = "Android/data/com.tencent.mobileqq/Tencent/QQfile_recv";
    private final String PRIMARY_QQ_FILE_RECV = "/tree/primary:Android/data/document/primary:";
    // nt_qq_ 频道的标识符 由于只能获取ROOT权限或者虚拟机下才能访问，故放弃实现
    // private static final String FILE_ORI = "/File/Ori";
    private final DateFormat dateTimeFormat = SimpleDateFormat.getDateTimeInstance();
    // 文件不存在
    private final int FILE_NOT_EXISTS = 1;
    // 获取了权限，而且文件存在
    private final int ALL_OK = 2;

    /*@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.module_qqfile_layout, null);
        return inflate;
    }*/

    @Override
    protected void initAdapter(View view) {
        adapter = new VersionListRecyclerAdapter() {

            @Override
            protected void onItemClick(View view, VersionData data) {

            }

            @Override
            public void onItemDelete(VersionData data) {

            }

            @Override
            public void replaceList(List<VersionData> l) {
                if (mRefreshLayout.isRefreshing()) {
                    setRefreshing(false);
                }
                loadingText.setVisibility(View.GONE);
                list.clear();
                list.addAll(l);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    protected void initTitle(View view) {
        TextView title_text_1 = view.findViewById(R.id.title_text_1);
        title_text_1.setText("文件名称");
        TextView title_text_2 = view.findViewById(R.id.title_text_2);
        title_text_2.setText("文件大小");
        TextView title_text_3 = view.findViewById(R.id.title_text_3);
        title_text_3.setText("修改时间");
        TextView title_text_4 = view.findViewById(R.id.title_text_4);
        title_text_4.setText("文件路径");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int checkPermission() {
        // 检测是否授权
        if (!FileUriUtils.isGrant(getActivity(), "Android/data")) {
            return UNAUTHORIZED;
        }
        // 获取qq下载的文件路径
        DocumentFile documentFile = FileUriUtils.getTreeDocumentFile(DocumentFile.fromTreeUri(getActivity(),
                Uri.parse(FileUriUtils.changeToUri3("Android/data"))), QQ_FILE_RECV);
        // 是否存在
        if (Objects.isNull(documentFile)) {
            Toast.makeText(getContext(), "未找到QQ下载的路径！", Toast.LENGTH_SHORT).show();
            return FILE_NOT_EXISTS;
        }
        return ALL_OK;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void refresh() {
        ThreadUtil.submit(() -> {
            int permission = checkPermission();
            switch (permission) {
                case UNAUTHORIZED: {
                    XPopupUtil.asConfirm(getActivity(), "授权Android/data提示！", "需要授权Android/data目录权限才能使用此功能，是否进行授权？", () -> {
                        FileUriUtils.startForRoot(getActivity(), REQUEST_DATA_ALL_CODE);
                    });
                    return;
                }
                case FILE_NOT_EXISTS: {
                    return;
                }
            }
            DocumentFile documentFile = FileUriUtils.getTreeDocumentFile(DocumentFile.fromTreeUri(getActivity(),
                    Uri.parse(FileUriUtils.changeToUri3("Android/data"))), QQ_FILE_RECV);
            JSONArray array = new JSONArray();
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
                        object.put("size", FileUtil.getFileSize(file.length()));
                        array.add(object);
                    });
            List<VersionData> lists = array.toJavaList(VersionData.class);
            getActivity().runOnUiThread(() -> {
                adapter.replaceList(lists);
            });
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // 如果不是请求Android/data权限的话，就不执行了，因为其他请求页面的回调会报错
        if (!Objects.equals(requestCode, REQUEST_DATA_ALL_CODE)) return;

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
                RxToast.success(getActivity(), "授权目录成功");
                Toast.makeText(getContext(), "授权目录成功", Toast.LENGTH_SHORT).show();
                refresh();
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
