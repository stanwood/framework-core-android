package io.stanwood.framework.content;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.List;

public class IntentCreator {

    private static final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";

    @Nullable
    public static Intent createShareIntent(@NonNull String subject, @NonNull String text) {
        Intent shareIntent = null;
        if (!TextUtils.isEmpty(subject) || !TextUtils.isEmpty(text)) {
            shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            shareIntent.setType("text/plain");
        }
        return shareIntent;
    }

    @NonNull
    public static Intent createOpenAppSettingsIntent(@NonNull Context context) {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        return intent;
    }

    @NonNull
    public static Intent createOpenLocationSettings() {
        return new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    }

    @NonNull
    public static Intent createPlayStoreIntent(@NonNull Context context) {
        String packageName = context.getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
        if (isPackageInstalled(context, GOOGLE_PLAY_PACKAGE_NAME)) {
            intent.setPackage(GOOGLE_PLAY_PACKAGE_NAME);
        }
        return intent;
    }

    public static boolean isPackageInstalled(@NonNull Context context, @NonNull String targetPackage) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
        for (int size = packages.size(), i = 0; i < size; i++) {
            if (packages.get(i).packageName.equals(targetPackage)) {
                return true;
            }
        }
        return false;
    }

}
