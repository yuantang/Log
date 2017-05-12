package com.coder.demo;

import android.app.Application;

import com.coder.tlog.TLog;
import com.coder.tlog.TLogConfig;
import com.coder.tlog.TLogLevel;

/**
 * Created by TUS on 2017/5/12.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TLogConfig.Builder builder=new TLogConfig.Builder(this)
                .setLogSwitch(true)
                .setLogFilter(TLogLevel.D)
                .setBorderSwitch(true)
                .setLog2FileSwitch(true)
                .setCacheDays(1)
                .setCacheLines(0)
                .setTagIsSpace(true)
                .setGlobalTag("Test Tag")
                .setLogHeadSwitch(true)
                .setOpenCrash(true)
                .setUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());
        TLog.init(builder.build());
    }
}