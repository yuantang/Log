package com.coder.tlog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.ArrayMap;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * ================================================
 * @时间  2017/5/9 on 17:24
 * @作者
 * @类名  CrashLogger
 * @描述  收集crash信息
 * ================================================
 */

public class CrashLogger implements CrashHandler.OnCrashExceptionListener {
    private ExecutorService mExecutorService;
    private Context mContext;
    @SuppressLint("NewApi")
    private ArrayMap<String,String> devicesInfo=new ArrayMap<>();
    public CrashLogger(Context context){
        this.mContext=context;
        mExecutorService= Executors.newSingleThreadExecutor();
    }
    @Override
    public void onCaughtCrashException(Thread thread, final Throwable throwable) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                collectDeviceInfo(mContext);
                collectCrashInfo(throwable);
            }
        });
    }
    /**
     * 收集异常信息
     * @param throwable
     */
    @SuppressLint("NewApi")
    private void collectCrashInfo(Throwable throwable) {
        StringBuffer sb = new StringBuffer();
        StringWriter mStringWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
        throwable.printStackTrace(mPrintWriter);
        Throwable cause = throwable.getCause();
        while (cause!=null){
            cause.printStackTrace(mPrintWriter);
            cause=cause.getCause();
        }
        mPrintWriter.close();
        try {
            mStringWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result=mStringWriter.toString();
        sb.append(result);
        for (Map.Entry<String, String> entry : devicesInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        String crash=sb.toString();
        TLog.file(crash);
    }

    /**
     * 收集设备信息
     * @param context
     */
    @SuppressLint("NewApi")
    private void collectDeviceInfo(Context context) {
        try {
            PackageManager pm=context.getPackageManager();
            PackageInfo info=pm.getPackageInfo(context.getPackageName(),PackageManager.GET_ACTIVITIES);
            if (info!=null){
                String versionName= info.versionName;
                String versionCode=info.versionCode+"";
                devicesInfo.put("versionName",versionName);
                devicesInfo.put("versionCode",versionCode);
                Field[] fields = Build.class.getDeclaredFields();
                for (Field field: fields) {
                    field.setAccessible(true);
                    devicesInfo.put(field.getName(), field.get(null).toString());
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
