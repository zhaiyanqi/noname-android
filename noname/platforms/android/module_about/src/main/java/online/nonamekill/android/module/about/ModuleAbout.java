package online.nonamekill.android.module.about;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import online.nonamekill.common.module.BaseModule;

public class ModuleAbout extends BaseModule {

    @Override
    public View getView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.module_about_layout, null);

        return view;
    }

    @Override
    public String getName() {
        return "关于";
    }
}
