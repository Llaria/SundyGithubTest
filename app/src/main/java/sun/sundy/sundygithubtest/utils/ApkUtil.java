package sun.sundy.sundygithubtest.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;

import static sun.sundy.sundygithubtest.SundyApplication.app;


/**
 * apk工具类
 *
 * @author scj
 */
public final class ApkUtil {

    /**
     * 退出应用
     */
    public static final String APK_EXIT = "apk_exit";
    /**
     * 应用重启
     */
    public static final String APK_RESTART = "apk_restart";

    private ApkUtil() {
        throw new RuntimeException("╮(╯▽╰)╭");
    }

    /**
     * 获取应用的版本号
     */
    public static String getVersionName() {
        String version = "";
        try {
            PackageManager packageManager = app.getPackageManager();
            PackageInfo packInfo;
            packInfo = packageManager.getPackageInfo(app.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取应用的版本号
     */
    public static int getVersionCode() {
        int version = 0;
        try {
            PackageManager packageManager = app.getPackageManager();
            PackageInfo packInfo;
            packInfo = packageManager.getPackageInfo(app.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName() {
        try {
            PackageManager packageManager = app.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(app.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return app.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return 获取包名
     */
    public static String getPackageName() {
        try {
            PackageManager packageManager = app.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(app.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取是否安装对应的包名
     *
     * @param packageName 包名
     * @return false 是没有安装 true 安装了
     */
    public static boolean checkApkExist(String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            app.getPackageManager().getApplicationInfo(packageName, PackageManager
                    .GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 重启应用
     */
    public static void restart() {
//        KlLoger.error("重启应用");
        Intent i = app.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(APK_RESTART, true);
        app.startActivity(i);
    }

    /**
     * 退出应用
     * <p/>
     */
    public static void exit() {
        Intent i = app.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(APK_EXIT, true);
        app.startActivity(i);
    }

    /**
     * 启动一个应用程序
     *
     * @param packageName 应用包名
     * @return 不存在此应用返回false
     */
    public static boolean startApp(String packageName) {
        Intent LaunchIntent = app.getPackageManager().getLaunchIntentForPackage(packageName);
        if (LaunchIntent == null) {
            return false;
        } else {
            app.startActivity(LaunchIntent);
            return true;
        }
    }

    /**
     * 创建桌面快捷方式
     */
    public static void createShortCut(Activity activity) {
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutintent.putExtra("duplicate", false);

        // 获取名称和图片
        PackageManager packageManager = app.getPackageManager();
        int iconResId;
        int appnameResId;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(app.getPackageName(), 0);
            iconResId = packageInfo.applicationInfo.icon;
            appnameResId = packageInfo.applicationInfo.labelRes;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (isShortCutExist(activity.getString(appnameResId)))
            return;

        // 设置名称和图标
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, activity.getString(appnameResId));
        Parcelable icon = Intent.ShortcutIconResource.fromContext(activity.getApplicationContext
                (), iconResId);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

        Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");

        // 点击快捷图片，运行的程序主入口
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        activity.sendBroadcast(shortcutintent);
    }

    private static boolean isShortCutExist(String appName) {
        boolean isExist = false;
        int version = android.os.Build.VERSION.SDK_INT;
        Uri uri = null;
        if (version < 2.0) {
            uri = Uri.parse("content://com.android.launcher.settings/favorites");
        } else {
            uri = Uri.parse("content://com.android.launcher2.settings/favorites");
        }
        String selection = " title = ?";
        String[] selectionArgs = new String[]{appName};
        Cursor c = app.getContentResolver().query(uri, null, selection,
                selectionArgs, null);
        if (c == null) {
            return false;
        }
        if (c.getCount() > 0) {
            isExist = true;
        }
        c.close();
        return isExist;
    }

    /**
     * 检查sd卡存储是否已满, 默认已用超过80%返回true
     */
//    public static boolean isStorageFull() {
//        int mb = 1024 * 1024;
//        double totalSize = MemorySpaceCheck.getInstance().getSysTotalSize() / mb;
//        double systemSurplus = MemorySpaceCheck.getInstance().getSystemAvailableSize() / mb;
//        return (systemSurplus / totalSize) > 0.8;
//    }

}
