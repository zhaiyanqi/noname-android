package online.nonamekill.android.module.version;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.permissionx.guolindev.PermissionX;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import online.nonamekill.common.GameLog;
import online.nonamekill.common.data.DataKey;
import online.nonamekill.common.data.DataManager;
import online.nonamekill.common.util.ActivityUtil;
import online.nonamekill.common.util.AppUtils;
import online.nonamekill.common.util.FileUtil;
import online.nonamekill.common.util.GameResourceUtil;
import online.nonamekill.common.util.RxToast;
import online.nonamekill.common.util.ThreadUtil;
import online.nonamekill.common.util.XPopupUtil;
import online.nonamekill.common.versionAdapter.AdapterListAbstract;
import online.nonamekill.common.versionAdapter.VersionData;
import online.nonamekill.common.versionAdapter.VersionListRecyclerAdapter;
import online.nonamekill.module.version.R;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ModuleVersion extends AdapterListAbstract {

    private final DateFormat dateTimeFormat = SimpleDateFormat.getDateTimeInstance();

    private String currentPath = getGamePath();

    @Override
    protected void initAdapter(View view) {
        adapter = new VersionListRecyclerAdapter() {
            @Override
            protected void onItemClick(View view, VersionData data) {
                String del = "删除", setGameBody = "设置为游戏主体";
                List<String> list1 = new ArrayList<>();
                list1.add(del);
                if (!Objects.equals(currentPath, data.getPath())) {
                    list1.add(0, setGameBody);
                }
                XPopupUtil.asAttachList(getActivity(), list1.toArray(new String[]{}), view,
                        (position, text) -> {
                            if (setGameBody.equals(text)) {
                                setGamePath(data);
                            } else if (del.equals(text)) {
                                XPopupUtil.asConfirm(getActivity(), "提示", "是否删除 " + data.getPath(), () -> onItemDelete(data));
                            }
                        }).show();

            }

            @Override
            public void onItemDelete(VersionData data) {
                XPopup.Builder builder = new XPopup.Builder(getContext());
                BasePopupView show = builder.dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .asLoading("正在删除...").show();

                CompletableFuture.supplyAsync(() -> {
                    try {
                        FileUtil.del(data.getPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    // 如果删除的是当前目录，那么就让其重新设置一下，如果不是，就不需要重新设置了，省去了重启的时间
                    return Objects.equals(DataManager.getInstance().getValue(DataKey.KEY_GAME_PATH), data.getPath());
                }, ThreadUtil.getThreadPool())
                        .thenAcceptAsync(del -> {
                            getActivity().runOnUiThread(() -> {
                                RxToast.success(getActivity(), "删除成功");
                            });
                            if (del) {
                                DataManager.getInstance().setValue(DataKey.KEY_GAME_PATH, null);
                            }
                            refresh();
                            show.smartDismiss();
                        }, ThreadUtil.getThreadPool())
                        .exceptionally(throwable -> {
                            GameLog.e(this.getClass(), throwable);
                            getActivity().runOnUiThread(() -> {
                                RxToast.error(getActivity(), "删除失败" + throwable.getMessage());
                            });
                            show.smartDismiss();
                            return null;
                        });
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void replaceList(List<VersionData> l) {
                endLoading();
                list.clear();
                list.addAll(l);
                currentPath = getGamePath();
                notifyDataSetChanged();
            }

            @Override
            public boolean isInitSelected(VersionData data) {
                return Objects.equals(data.getPath(), getGamePath());
            }
        };
    }

    private String getGamePath() {
        return DataManager.getInstance().getValue(DataKey.KEY_GAME_PATH);
    }

    private void setGamePath(@NonNull String value) {
        DataManager.getInstance().setValue(DataKey.KEY_GAME_PATH, value);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setGamePath(VersionData data) {
        XPopupUtil.asConfirm(getActivity(), "提示", "需要重启才能生效, 是否设置为当前版本", () -> {
            adapter.unSelectAll();

            String curPath = getGamePath();
            if (!TextUtils.isEmpty(curPath)) {
                FileUtil.backupWebContentToPath(getContext(), curPath, data.getPath());
            }
            setGamePath(data.getPath());
            currentPath = data.getPath();
            data.setSelected(true);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void initTitle(@NonNull View view) {
        TextView title_text_1 = view.findViewById(R.id.title_text_1);
        title_text_1.setText("版本名称");
        TextView title_text_2 = view.findViewById(R.id.title_text_2);
        title_text_2.setText("导入时间");
        TextView title_text_3 = view.findViewById(R.id.title_text_3);
        title_text_3.setVisibility(View.GONE);
        TextView title_text_4 = view.findViewById(R.id.title_text_4);
        title_text_4.setText("路径");
    }

    private void findAllGameFileInRootView(boolean includeSd) {
        ThreadUtil.submit(() -> {

            startLoading();

            File root = getContext().getExternalFilesDir(null);
            List<File> list = new ArrayList<>(GameResourceUtil.findGameInPath(root));

            if (includeSd) {
                File sd = Environment.getExternalStorageDirectory();
                if (sd != null) {
                    File noname = new File(sd.getAbsoluteFile() + File.separator + AppUtils.getAppPackageName());
                    if (!noname.exists()) {
                        noname.mkdirs();
                    }
                    list.addAll(GameResourceUtil.findGameInPath(noname));
                }
            }

            List<VersionData> verList = new ArrayList<>();

            list.forEach(file -> {
                VersionData data = new VersionData();
                data.setDate(dateTimeFormat.format(file.lastModified()));
                data.setName(file.getName());
                data.setPath(file.getPath());
                verList.add(data);
            });

            getActivity().runOnUiThread(() -> adapter.replaceList(verList));
        });
    }

    @Override
    protected void refresh() {
        if (!PermissionX.isGranted(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) || !PermissionX.isGranted(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            XPopupUtil.asConfirm(getActivity(), "授权提示", "此功能需要授权文件的读写权限，是否授权？", () -> {
                PermissionX.init((FragmentActivity) getActivity())
                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .request((allGranted, grantedList, deniedList) -> findAllGameFileInRootView(allGranted));
            });
        } else {
            ThreadUtil.submit(() -> findAllGameFileInRootView(true));
        }
    }


    @Override
    public String getName() {
        return "版本管理";
    }
}
