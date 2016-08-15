package com.shark.baseproject.util;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.shark.base.util.StringUtil;
import com.shark.baseproject.manager.ApplicationManager;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by adye on 2015/4/30.
 */
public class SystemUtil {


    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfoList = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfoList.size(); i++) {
            if (processInfoList.get(i).processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equalsIgnoreCase(context.getPackageName())
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPowerScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            isScreenOn = pm.isInteractive();
        } else {
            isScreenOn = pm.isScreenOn();
        }
        return isScreenOn;
    }

    public static String generateUniqueId() {
        String uniqueId = ApplicationManager.getInstance().getUniqueId();
        if (StringUtil.isEmpty(uniqueId)) {
            uniqueId = generateMacAddress();
            if (StringUtil.isEmpty(uniqueId) || "02:00:00:00:00:00".equalsIgnoreCase(uniqueId)) {
                try {
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(ApplicationManager.getInstance().getContext());
                    uniqueId = adInfo.getId();
                } catch (IOException e) {
                    e.printStackTrace();
                    uniqueId = UUID.randomUUID().toString();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    uniqueId = UUID.randomUUID().toString();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                    uniqueId = UUID.randomUUID().toString();
                }

            }
            ApplicationManager.getInstance().setUniqueId(uniqueId);
        }
        return uniqueId;
    }

    public static String generateMacAddress() {
        String macAddress = "";
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            WifiManager wifiManager = (WifiManager) ApplicationManager.getInstance().getContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info;
            if (wifiManager != null) {
                info = wifiManager.getConnectionInfo();
                if (info != null) {
                    macAddress = info.getMacAddress();
                }
            }
        } else {
            macAddress = generateMacAddressFromSdk23();
        }
        return macAddress;
    }

    public static String generateMacAddressFromSdk23() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : all) {
                if (!networkInterface.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] bytes = networkInterface.getHardwareAddress();
                if (bytes == null) {
                    return "02:00:00:00:00:00";
                }
                StringBuilder result = new StringBuilder();
                for (byte macAddressByte : bytes) {
                    result.append(Integer.toHexString(macAddressByte & 0xFF) + ":");
                }

                if (result.length() > 0) {
                    result.deleteCharAt(result.length() - 1);
                }
                return result.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    public static void requestGoogleCloudMessageId(Context context, String senderId) {
        if (isGooglePlayServicesAvailable(context)) {
            String registerId = ApplicationManager.getInstance().getGcmRegisterId();
            if (StringUtil.isEmpty(registerId)) {
                registerGcmIdInBackground(context, senderId);
            }
        }
    }

    private static void registerGcmIdInBackground(final Context context, final String senderId) {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                    String registerId = gcm.register(senderId);
                    ApplicationManager.getInstance().setGcmRegisterId(registerId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                //TODO send gcm id to server - Shark.M.Lin
            }
        }.executeOnExecutor(null, null, null);
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    public static boolean isRunningOnEmulator() {
        return Build.BRAND.equalsIgnoreCase("generic");
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }
}
