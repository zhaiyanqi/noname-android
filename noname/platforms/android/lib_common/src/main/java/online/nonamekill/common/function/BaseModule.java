package online.nonamekill.common.function;

import android.content.Context;
import android.view.View;

public class BaseModule {
    protected Context mContext = null;
    protected ModuleListener mListener = null;

    public void setListener(ModuleListener listener) {
        mListener = listener;
    }

    public void onCreate(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public View getView(Context context) {
        return null;
    }

    public void onPause() {

    }

    public void onResume() {

    }
}
