package online.nonamekill.android.module.icon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import online.nonamekill.common.module.BaseModule;

public class ModuleIcon extends BaseModule {

    private View mRootView = null;

    @Override
    public void onPreCreate() {
        createViews();
    }

    private void createViews() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.module_layout_icon, null);
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
