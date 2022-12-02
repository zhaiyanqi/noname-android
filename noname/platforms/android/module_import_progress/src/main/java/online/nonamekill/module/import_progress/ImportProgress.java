package online.nonamekill.module.import_progress;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import online.nonamekill.android.module.import_progress.R;
import online.nonamekill.common.GameLog;
import online.nonamekill.common.module.BaseModule;
import online.nonamekill.module.import_progress.adapter.MessageRecyclerAdapter;
import online.nonamekill.module.import_progress.data.MessageData;


public class ImportProgress extends BaseModule {

    private static final int MSG_UPDATE_PROGRESS = 100;
    private static final int MSG_UPDATE_PROGRESS_DELAY = 100;
    // copy zip或者7z文件进度条数 因为zip4j读取需要文件必须存在
    private static final int FETCH_TASK_MAX_PERCENT = 20;
    private static final int STATE_FETCH = 1;
    private static final int STATE_COPY = 2;
    private static final int STATE_FINISH = 3;
    private static MessageRecyclerAdapter adapter = null;
    private static RecyclerView messageRecyclerView = null;
    private static int TEMP_TASK_COUNT;
    private final Object mCountLock = new Object();
    private NumberProgressBar mProgressBar = null;
    private volatile int mImportState = STATE_FINISH;
    private int mFetchCount = 0;
    private int mFinishTaskCount = 0;
    private int mAllTaskCount = 0;
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
                }
            }
        }
    };

    public static void setTempTaskCount(int tempTaskCount) {
        TEMP_TASK_COUNT = tempTaskCount;
    }

    @Nullable
    @Override
    public View getView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.import_progress, null);
    }

    @Override
    public void onCreateView(View view) {
        initProgressBar(view);
        initAdapterRecyclerView(view);

        adapter.addMessage(new MessageData("设置 0.0", null));
        messageRecyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public String getName() {
        return "导入详情";
    }

    // 初始化Adapter和Recycler插件
    private void initAdapterRecyclerView(@NonNull View view) {
        messageRecyclerView = view.findViewById(R.id.message_import_info_recycler_view);
        adapter = new MessageRecyclerAdapter();
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        messageRecyclerView.setLayoutManager(mLinearLayoutManager);
        messageRecyclerView.setSelected(true);
        messageRecyclerView.setAdapter(adapter);
    }
    // 初始化进度条
    private void initProgressBar(@NonNull View view) {
        mProgressBar = view.findViewById(R.id.number_progressbar);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
        mImportState = STATE_FETCH;
        mFetchCount = 0;
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_UPDATE_PROGRESS), MSG_UPDATE_PROGRESS_DELAY);
    }

}