package online.nonamekill.common.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class BaseModule {
    private Activity mActivity = null;

    public void onCreate(Activity activity) {
        mActivity = activity;
    }

    public void onPreCreate() {

    }

    protected Context getContext() {
        return mActivity;
    }

    protected Activity getActivity() {
        return mActivity;
    }

    public View getView(Context context) {
        return null;
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public String getName() {
        return null;
    }

    public void onDestroy() {

    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }

    public void onCreateView(View view) {

    }
}
