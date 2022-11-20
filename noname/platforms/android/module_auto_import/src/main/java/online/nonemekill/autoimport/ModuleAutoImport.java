package online.nonemekill.autoimport;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.RelativeLayout;

import com.daimajia.numberprogressbar.NumberProgressBar;

import online.nonamekill.common.Constant;
import online.nonamekill.common.function.BaseModule;
import online.nonamekill.common.util.GameResourceUtil;
import online.nonamekill.common.util.ThreadUtil;

/**
 * 自动导入模块，用于导入游戏
 */
public class ModuleAutoImport extends BaseModule {

    private NumberProgressBar mProgressBar = null;
    private int mFetchProgress = 0;

    @Override
    public void onResume() {
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);

        ThreadUtil.execute(() -> {
//            boolean exists = GameResourceUtil.checkIfGamePath(mContext.getExternalFilesDir(Constant.GAME_FOLDER));
            boolean exists = false;

            if (exists) {
                mProgressBar.post(() -> {
                    mProgressBar.setProgress(100);
                    mListener.onAutoImportFinished();
                });
            } else {
                GameResourceUtil.copyAssetToGameFolder(getContext(), Constant.GAME_FOLDER, new GameResourceUtil.onCopyListener() {

                    @Override
                    public void onSingleTaskFetch() {
                        mProgressBar.post(() -> {
                            mFetchProgress++;
                            mProgressBar.setProgress(Math.min(mFetchProgress * 20 / 7000, 20));
                        });
                    }
                });
            }
        });
    }

    @Override
    public View getView(Context context) {
        mProgressBar = new NumberProgressBar(context);
        mProgressBar.setProgress(100);
        Resources resources = context.getResources();

        // todo replace with dp params.
        int width = resources.getDimensionPixelOffset(R.dimen.progress_bar_width);
        int height = resources.getDimensionPixelOffset(R.dimen.progress_bar_height);
        int marginBottom = resources.getDimensionPixelOffset(R.dimen.progress_bar_height);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = marginBottom;
        mProgressBar.setLayoutParams(params);

        return mProgressBar;
    }
}
