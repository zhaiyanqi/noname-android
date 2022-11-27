package online.nonamekill.android.module.icon;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import online.nonamekill.common.module.BaseModule;

public class ModuleIcon extends BaseModule {

    private View mRootView = null;
    private RecyclerView mIconRecyclerView = null;

    @Override
    public void onPreCreate() {
        createViews();
    }

    private void createViews() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.module_layout_icon, null);
        mIconRecyclerView = mRootView.findViewById(R.id.module_icon_recycler_view);
        mIconRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<IconInfo> list = new ArrayList<>();
        list.add(new IconInfo(R.drawable.icon_noname_zhangba, "online.nonamekill.android.module.icon.zhangba"));
        list.add(new IconInfo(R.drawable.icon_noname_fantian, "online.nonamekill.android.module.icon.fangtian"));
        IconListAdapter adapter = new IconListAdapter(list, info -> {
            PackageManager pm = getActivity().getPackageManager();
            pm.setComponentEnabledSetting(getActivity().getComponentName(),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(new ComponentName(getActivity(), info.getTagName()),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        });

        mIconRecyclerView.setAdapter(adapter);
    }

    @Override
    public View getView(Context context) {
        if (null == mRootView) {
            createViews();
        }

        return mRootView;
    }

    @Override
    public String getName() {
        return "应用图标";
    }
}
