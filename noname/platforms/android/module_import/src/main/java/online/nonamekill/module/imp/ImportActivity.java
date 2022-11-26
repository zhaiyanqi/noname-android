package online.nonamekill.module.imp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;

import com.daimajia.numberprogressbar.NumberProgressBar;

import online.nonamekill.common.Constant;
import online.nonamekill.common.GameLog;
import online.nonamekill.common.util.GameResourceUtil;
import online.nonamekill.common.util.ThreadUtil;

public class ImportActivity extends AppCompatActivity {

    private static final int MSG_UPDATE_PROGRESS = 100;
    private static final int MSG_UPDATE_PROGRESS_DELAY = 100;
    private static final int TEMP_TASK_COUNT = 7000;
    private static final int FETCH_TASK_MAX_PERCENT = 20;

    private static final int STATE_FETCH = 1;
    private static final int STATE_COPY = 2;
    private static final int STATE_FINISH = 3;

    private NumberProgressBar mProgressBar = null;

    private int mImportState = STATE_FINISH;
    private int mFetchCount = 0;
    private int mFinishTaskCount = 0;
    private int mAllTaskCount = 0;
    private final Object mCountLock = new Object();

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_UPDATE_PROGRESS) {
                if (STATE_FETCH == mImportState) {
                    int process = Math.min(mFetchCount * FETCH_TASK_MAX_PERCENT / TEMP_TASK_COUNT, FETCH_TASK_MAX_PERCENT);
                    mProgressBar.setProgress(process);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_UPDATE_PROGRESS), MSG_UPDATE_PROGRESS_DELAY);
                } else if (STATE_COPY == mImportState) {
                    GameLog.e("zyq", "mFinishTaskCount: " + mFinishTaskCount + ", mAllTaskCount: " + mAllTaskCount);
                    int process = FETCH_TASK_MAX_PERCENT + (mFinishTaskCount * 100 / mAllTaskCount);
                    mProgressBar.setProgress(process);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_UPDATE_PROGRESS), MSG_UPDATE_PROGRESS_DELAY);
                } else if (STATE_FINISH == mImportState) {
                    mProgressBar.setProgress(100);
                    onAutoImportFinished();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        mProgressBar = findViewById(R.id.number_progressbar);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
        mImportState = STATE_FETCH;
        mFetchCount = 0;

        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_UPDATE_PROGRESS), MSG_UPDATE_PROGRESS_DELAY);

//        createPackageContext("", )

        ThreadUtil.execute(() -> GameResourceUtil.copyAssetToGameFolder(this, Constant.GAME_FOLDER, new GameResourceUtil.onCopyListener() {
            @Override
            public void onBegin(int sum) {
                synchronized (mCountLock) {
                    mAllTaskCount = sum;
                    mFinishTaskCount = 0;
                    mImportState = STATE_COPY;
                }
            }

            @Override
            public void onSingleTaskFetch() {
                mFetchCount++;
            }

            @Override
            public void onFinish(int count) {
                synchronized (mCountLock) {
                    mFinishTaskCount += count;

                    if (mFinishTaskCount >= mAllTaskCount) {
                        mImportState = STATE_FINISH;
                    }
                }
            }
        }));
    }

    @Override
    public void onBackPressed() {
        if ((STATE_COPY == mImportState) || (STATE_FETCH == mImportState)) {
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mImportState = STATE_FINISH;
        mHandler.removeCallbacksAndMessages(null);
    }

    public void onAutoImportFinished() {
        runOnUiThread(() -> {
            finish();
            overridePendingTransition(0, R.anim.exit_import_anim);
        });
    }
}