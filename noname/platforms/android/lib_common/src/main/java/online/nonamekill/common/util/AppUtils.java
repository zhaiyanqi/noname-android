package online.nonamekill.common.util;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Process;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * TODO 跟App相关的辅助类
 * <p>
 * 1、获取应用程序名称
 * 2、获取应用程序版本名称信息
 * 3、获取版本号
 * 4、获取所有安装的应用程序,不包含系统应用
 * 5、获取应用程序的icon图标
 * 6、启动安装应用程序
 * 7、获取渠道名
 * 8、双击退出
 */
public class AppUtils {

    private static Context context = null;

    private static Context getContext(){
        return context;
    }

    public static void initialize(Context context){
        AppUtils.context = context;
    }

    /**
     * 8、双击退出
     */
    private static long firstTime = 0;

    /**
     * 获取包名
     *
     * @return
     */
    public static String getAppPackageName() {
        return getContext().getPackageName();
    }

    /**
     * 1获取应用程序名称
     */
    public static String getAppName() {
        try {
            PackageManager packageManager = getContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return getContext().getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 2[获取应用程序版本名称信息]
     *
     * @return 当前应用的版本名称
     */
    public static String getVersionName() {
        try {
            PackageManager packageManager = getContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
            return packageInfo.versionName;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 3获取版本号
     * int
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 4获取所有安装的应用程序,不包含系统应用
     *
     * @return
     */
    public static List<PackageInfo> getInstalledPackages() {
        PackageManager packageManager = getContext().getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<PackageInfo> packageInfoList = new ArrayList<PackageInfo>();
        for (int i = 0; i < packageInfos.size(); i++) {
            if ((packageInfos.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                packageInfoList.add(packageInfos.get(i));
            }
        }
        return packageInfoList;
    }

    /**
     * 5、获取应用程序的icon图标
     *
     * @return 当包名错误时，返回null
     */
    public static Drawable getApplicationIcon() {
        PackageManager packageManager = getContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
            return packageInfo.applicationInfo.loadIcon(packageManager);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 7、获取渠道名
     *
     * @return
     */
    public static String getChannel() {
        try {
            PackageManager pm = getContext().getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (NameNotFoundException ignored) {
        }
        return "";
    }

    public static void againstClick(Activity context) {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(getContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            context.finish();
        }
    }

    /**
     * 9.获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 10、判断是否在主进程
     */
    public static boolean isMainProcess() {
        ActivityManager am = (ActivityManager) getContext().getSystemService(ACTIVITY_SERVICE);
        String mainProcessName = getContext().getPackageName();
        int myPid = Process.myPid();
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        if (processInfos == null) {
            List<ActivityManager.RunningServiceInfo> processList = am.getRunningServices(2147483647);
            if (processList == null) {
                return false;
            } else {
                Iterator var9 = processList.iterator();

                ActivityManager.RunningServiceInfo rsi;
                do {
                    if (!var9.hasNext()) {
                        return false;
                    }

                    rsi = (ActivityManager.RunningServiceInfo) var9.next();
                } while (rsi.pid != myPid || !mainProcessName.equals(rsi.service.getPackageName()));

                return true;
            }
        } else {
            Iterator var5 = processInfos.iterator();

            ActivityManager.RunningAppProcessInfo info;
            do {
                if (!var5.hasNext()) {
                    return false;
                }

                info = (ActivityManager.RunningAppProcessInfo) var5.next();
            } while (info.pid != myPid || !mainProcessName.equals(info.processName));

            return true;
        }
    }

    /**
     * 重新启动App -> 杀进程,会短暂黑屏,启动慢
     */
    public static void restartApp() {
        final Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(getAppPackageName());
        if (Objects.nonNull(launchIntent)) {
            new Handler().postDelayed(() -> {
                //添加activity切换动画效果
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getContext().startActivity(launchIntent);
                killCurrentProcess();
                System.exit(0);
            }, 300);
        }
    }

    /**
     * 终止进程 - 杀死自己
     */
    public static void killCurrentProcess() {
        Process.killProcess(Process.myPid());
    }
}
