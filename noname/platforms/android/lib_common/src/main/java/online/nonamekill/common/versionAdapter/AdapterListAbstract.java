package online.nonamekill.common.versionAdapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Objects;

import online.nonamekill.common.module.BaseModule;
import online.nonamekill.common.util.ThreadUtil;
import online.nonamekill.lib_common.R;


public abstract class AdapterListAbstract extends BaseModule {
    // adapter
    protected VersionListRecyclerAdapter adapter = null;
    // 初始化下拉刷新按钮
    protected SwipeRefreshLayout mRefreshLayout;
    // 正在加载文字
    protected TextView loadingText = null;
    // 刷新按钮
    protected Button refreshButton = null;
    // 预加载标记
    private volatile View mRootView = null;


    @Nullable
    @Override
    public View getView(Context context) {
        if(Objects.nonNull(mRootView)) return mRootView;
        return mRootView = LayoutInflater.from(context).inflate(R.layout.fragment_version_control, null);
    }


    @Override
    public void onVisible(){
        startLoading();
        this.refresh();
    }

    @Override
    public void onPreCreate() {
        super.onPreCreate();
        // 不能做UI操作，例如WebView初始化，可以初始化布局、加载字体、加载资源、初始化文件等
        mRootView =  LayoutInflater.from(getContext()).inflate(R.layout.fragment_version_control, null);
        // 初始化刷新按钮和字体加载
        initButton2TextView(mRootView);
        // 初始化adapter
        initAdapter(mRootView);
        // 初始化数据列表
        initVersionListView(mRootView);
        // 初始化标题
        initTitle(mRootView);
        // 初始化下拉刷新球
        initRefresh(mRootView);
        // 初始化自定义的一些东西
        initCustomView(mRootView);
    }

    // 初始化刷新按钮和字体加载
    protected void initButton2TextView(View view) {
        refreshButton = view.findViewById(R.id.import_game_button);
        refreshButton.setOnClickListener(v -> {
            startLoading();
            refresh();
        });

        loadingText = view.findViewById(R.id.loading_text);
    }

    // 初始化adapter
    protected abstract void initAdapter(View view);

    // 初始化数据列表
    protected void initVersionListView(View view) {
        RecyclerView versionListView = view.findViewById(R.id.version_list_recycler);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        versionListView.setLayoutManager(mLinearLayoutManager);
        versionListView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.versioin_list_anim));
        versionListView.setAdapter(adapter);
    }

    // 初始化标题
    protected abstract void initTitle(View view);

    // 初始化刷新按钮
    protected void initRefresh(View view) {
        mRefreshLayout = view.findViewById(R.id.swipe_refresh);
        // 设置下拉监听
        mRefreshLayout.setOnRefreshListener(() -> {
            startLoading();
            new Handler().postDelayed(this::refresh, 300);
        });
        // 刷新渐变颜色
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light
        );

    }

    protected void setRefreshing(boolean refreshing) {
        mRefreshLayout.setRefreshing(refreshing);
    }

    protected abstract void refresh();

    protected void initCustomView(View view) {
        // 让子类去实现，自定义初始化内容
    }

    // 开始加载的一些操作
    protected void startLoading() {
        if (!mRefreshLayout.isRefreshing())
            setRefreshing(true);

        adapter.clearAll();
        loadingText.setVisibility(View.VISIBLE);
    }

    // 加载结束的操作
    protected void endLoading() {
        if (mRefreshLayout.isRefreshing()) {
            setRefreshing(false);
        }
        loadingText.setVisibility(View.GONE);
    }

    protected void runOnUiThread(Runnable runnable) {
        getActivity().runOnUiThread(runnable);
    }

}
