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

import androidx.documentfile.provider.DocumentFile;

import com.alibaba.fastjson.JSONArray;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.impl.AttachListPopupView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import online.nonamekill.common.util.FileUriUtils;
import online.nonamekill.common.util.FileUtil;
import online.nonamekill.common.util.RxToast;
import online.nonamekill.common.util.ThreadUtil;
import online.nonamekill.common.util.XPopupUtil;
import online.nonamekill.common.versionAdapter.AdapterListAbstract;
import online.nonamekill.common.versionAdapter.VersionData;
import online.nonamekill.common.versionAdapter.VersionListRecyclerAdapter;
import online.nonamekill.module.import_progress.ImportProgress;

public class ModuleQQFile extends AdapterListAbstract {
    private final List<String> suffix = new ArrayList<String>(){{
        this.add(".7z");
        this.add(".zip");
        this.add(".rar");
        this.add(".gz");
        this.add(".tar");
        this.add(".cpio");
    }};

    // 2022年11月27日21点
    private static final int REQUEST_DATA_ALL_CODE = 2022112721;
    // nt_qq_ 频道的标识符 由于只能获取ROOT权限或者虚拟机下才能访问，故放弃实现
    // private static final String FILE_ORI = "/File/Ori";
    // 未授权
    private static final int UNAUTHORIZED = 0;
    // 文件不存在
    private final int FILE_NOT_EXISTS = 1;
    // 手机版本不支持(🐔了)
    private final int VERSION_NOT_SUPPORT = 2;
    // 所有权限已拥有
    private final int ALL_OK = 3;

    private final String QQ_FILE_RECV = "Android/data/com.tencent.mobileqq/Tencent/QQfile_recv";
    private final String PRIMARY_QQ_FILE_RECV = "/tree/primary:Android/data/document/primary:";
    private final DateFormat dateTimeFormat = SimpleDateFormat.getDateTimeInstance();

    @Override
    protected void initAdapter(View view) {
        adapter = new VersionListRecyclerAdapter() {
            @Override
            protected void onItemClick(View view, VersionData data) {
                List<String> attachList = new ArrayList<>();
                final String del = "删除",
                        importZip = "导入";
                Collections.addAll(attachList, importZip, del);

                AttachListPopupView asAttachList = XPopupUtil.asAttachList(getActivity(), attachList.toArray(new String[]{}), view,
                        (position, text) -> {
                            DocumentFile documentFile = FileUriUtils.getDocumentFile(getActivity(), data.getPath());
                            switch (text) {
                                case del:
                                    XPopupUtil.asConfirm(getActivity(),"提示", "是否删除 " + data.getPath(), () -> onItemDelete(data));
                                    break;
                                case importZip:
                                    Intent intent = new Intent();
                                    intent.setData(documentFile.getUri());
                                    intent.setClass(getContext(), ImportProgress.class);
                                    getActivity().startActivity(intent);
                                    getActivity().overridePendingTransition(0, 0);
                                    break;
                                default:

                                    break;
                            }
                        });
                asAttachList.show();
            }

            @Override
            public void onItemDelete(VersionData data) {
                BasePopupView loading = XPopupUtil.loading(getActivity(), "正在删除...");
                ThreadUtil.submit(()->{
                    String path = data.getPath();
                    DocumentFile documentFile = FileUriUtils.getDocumentFile(getActivity(), path);
                    boolean delete = documentFile.delete();
                    runOnUiThread(()->{
                        try {
                            if (delete) RxToast.success(getActivity(), "删除成功：" + data.getName());
                            else RxToast.error(getActivity(), "删除失败：" + data.getName());
                        } finally {
                            loading.dismiss();
                        }
                    });
                    refresh();
                });
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

    private int checkPermission() {
        // 检测版本号 安卓8以下不支持 安卓12以上不支持
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || Build.VERSION.SDK_INT > 31) {
            return VERSION_NOT_SUPPORT;
        }
        // 检测是否授权
        if (!FileUriUtils.isGrant(getActivity(), "Android/data")) {
            return UNAUTHORIZED;
        }
        // 获取qq下载的文件路径
        DocumentFile documentFile = FileUriUtils.getTreeDocumentFile(DocumentFile.fromTreeUri(getActivity(), Uri.parse(FileUriUtils.changeToUri3("Android/data"))), QQ_FILE_RECV);
        // 是否存在
        if (Objects.isNull(documentFile)) {
            return FILE_NOT_EXISTS;
        }
        return ALL_OK;
    }

    @Override
    protected void refresh() {
        ThreadUtil.execute(() -> {
            int permission = checkPermission();
            switch (permission) {
                case UNAUTHORIZED: {
                    XPopupUtil.asConfirm(getActivity(), "授权Android/data提示！", "需要授权Android/data目录权限才能使用此功能，是否进行授权？",
                            () -> FileUriUtils.startForRoot(getActivity(), REQUEST_DATA_ALL_CODE));
                    return;
                }
                case FILE_NOT_EXISTS: {
                    RxToast.error(getActivity(), "未找到QQ下载的路径！", Toast.LENGTH_SHORT);
                    return;
                }
                case VERSION_NOT_SUPPORT:{
                    RxToast.warning(getActivity(), "手机版本不支持，请使用安卓七以上安卓十三以下版本的手机！");
                    return;
                }
            }
            DocumentFile documentFile = FileUriUtils.getTreeDocumentFile(DocumentFile.fromTreeUri(getActivity(), Uri.parse(FileUriUtils.changeToUri3("Android/data"))), QQ_FILE_RECV);
            JSONArray array = new JSONArray();
            DocumentFile[] documentFiles = documentFile.listFiles();
            Arrays.stream(documentFiles).filter(DocumentFile::isFile).filter(file -> suffix.stream().anyMatch(item->file.getName().endsWith(item))).forEach(file -> {
                Map<String, String> object = new HashMap<>();
                String name = file.getName();
                object.put("name", name);
                object.put("date", dateTimeFormat.format(file.lastModified()));
                object.put("path", file.getUri().getPath().replace(PRIMARY_QQ_FILE_RECV, ""));
                object.put("size", FileUtil.getFileSize(file.length()));
                array.add(object);
            });
            List<VersionData> lists = array.toJavaList(VersionData.class);
            runOnUiThread(() -> adapter.replaceList(lists));
        });
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (!Objects.equals(requestCode, REQUEST_DATA_ALL_CODE)) {
            // 如果不是请求Android/data权限的话，就不执行了，因为其他请求页面的回调会报错
            return;
        }

        if (resultCode == RESULT_OK) {
            Uri uri;
            if (Objects.isNull(intent)) {
                return;
            }
            if (Objects.nonNull(uri = intent.getData())) {
                //关键是这里，这个就是保存这个目录的访问权限
                getActivity().getContentResolver().takePersistableUriPermission(uri, intent.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
                RxToast.success(getActivity(), "授权目录成功");
                refresh();
            }
        } else if (resultCode == RESULT_CANCELED) {
            RxToast.error(getActivity(), "取消目录授权", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public String getName() {
        return "QQ文件";
    }
}
