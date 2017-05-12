package com.coder.tlog;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/***
 * ================================================
 * @时间  2017/5/9 on 17:55
 * @作者  Yuan
 * @类名  TLogConfig
 * @描述  日志配置
 * ================================================
 */
public class TLogConfig {
    private Context mContext;
    /**log存储目录*/
    private static String          dir;
    /**log总开关，默认开*/
    private static boolean sLogSwitch       = true;
    /**log标签是否为空白*/
    private static boolean sTagIsSpace      = true;
    /**log标签*/
    private static String  sGlobalTag       = null;
    /**log头部开关，默认开*/
    private static boolean sLogHeadSwitch   = true;
    /**og写入文件开关，默认关*/
    private static boolean sLog2FileSwitch  = false;
    /**log边框开关，默认开*/
    private static boolean sLogBorderSwitch = true;
    /**log过滤器*/
    private static int     sLogFilter      = TLogLevel.V;
    /**文件log文件夹*/
    private static String sLogFileDir;
    /**crash收集开启*/
    private static   boolean sCrashOpen=true;
    /**crash收集*/
    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;
    /**缓存多少行写一次到文件*/
    private static int cacheLineSize=20;
    /**日志本地几天删除一次*/
    private static int cacheSaveDay=7;

    /**
     * 创建实例，没有设置的字段全部赋给默认值
     */

    public TLogConfig(Builder builder){
        this.mContext = builder.mContext;
        this.sLogSwitch = builder.logSwitch;
        this.sGlobalTag = builder.logTag;
        this.sTagIsSpace = builder.tagIsSpace;
        this.sLogHeadSwitch = builder.logHeadSwitch;
        this.sLog2FileSwitch = builder.log2FileSwitch;
        this.sLogBorderSwitch = builder.logBorderSwitch;
        this.sLogFilter = builder.logFilter;
        this.sLogFileDir = builder.logFileDir;
        this.sCrashOpen = builder.openCrash;
        this.mUncaughtExceptionHandler = builder.mUncaughtExceptionHandler;
        this.cacheLineSize = builder.cacheLines;
        this.cacheSaveDay = builder.cacheDays;
    }

    public static int getCacheLineSize() {
        return cacheLineSize;
    }

    public static int getCacheSaveDay() {
        return cacheSaveDay;
    }

    public Context getContext() {
        return mContext;
    }

    public static boolean isCrashOpen() {
        return sCrashOpen;
    }

    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return mUncaughtExceptionHandler;
    }
    public static boolean isLogSwitch() {
        return sLogSwitch;
    }

    public static String getGlobalTag() {
        return sGlobalTag;
    }

    public static boolean isTagIsSpace() {
        return sTagIsSpace;
    }

    public static boolean isLogHeadSwitch() {
        return sLogHeadSwitch;
    }

    public static boolean isLog2FileSwitch() {
        return sLog2FileSwitch;
    }

    public static boolean isLogBorderSwitch() {
        return sLogBorderSwitch;
    }

    public static int getLogFilter() {
        return sLogFilter;
    }

    public static String getLogFileDir() {
        return sLogFileDir;
    }

    public static class Builder {

        private Context mContext;
        private boolean logSwitch;
        private String logTag;
        private boolean tagIsSpace;
        private boolean logHeadSwitch;
        private boolean log2FileSwitch;
        private boolean logBorderSwitch;
        private int logFilter;
        private String logFileDir;
        private boolean openCrash;
        private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;

        private int cacheLines;
        private int cacheDays;

        public Builder(Context context) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                dir= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + context.getPackageName()+"_log" + File.separator;
            } else if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && context.getExternalCacheDir() != null)
                dir = context.getExternalCacheDir() + File.separator +context.getPackageName()+ "_log" + File.separator;
            else {
                dir = context.getCacheDir() + File.separator + context.getPackageName()+"_log" + File.separator;
            }
            Log.e("Builder","======dir========="+dir);
            this.logFileDir=dir;
            this.mContext=context;
        }

        public TLogConfig build(){
            return new TLogConfig(this);
        }
        /**
         * 日志开关
         * @param logSwitch
         * @return
         */
        public Builder setLogSwitch(boolean logSwitch) {
            this.logSwitch = logSwitch;
            return this;
        }

        /**
         * 日志标签
         * @param tag
         * @return
         */
        public Builder setGlobalTag(String tag) {
            this.logTag = tag;
            return this;
        }

        /**
         * 日志头开关
         * @param logHeadSwitch
         * @return
         */
        public Builder setLogHeadSwitch(boolean logHeadSwitch) {
            this.logHeadSwitch = logHeadSwitch;
            return this;
        }

        /**
         * 日志写入文件开关
         * @param log2FileSwitch
         * @return
         */
        public Builder setLog2FileSwitch(boolean log2FileSwitch) {
            this.log2FileSwitch = log2FileSwitch;
            return this;
        }

        /**
         * 日志边框开关
         * @param borderSwitch
         * @return
         */
        public Builder setBorderSwitch(boolean borderSwitch) {
            this.logBorderSwitch = borderSwitch;
            return this;
        }

        /**
         * 日志过滤等级
         * @param logFilter
         * @return
         */
        public Builder setLogFilter(int logFilter) {
            this.logFilter = logFilter;
            return this;
        }

        /**
         *
         * @param tagIsSpace
         * @return
         */
        public Builder setTagIsSpace(boolean tagIsSpace){
            this.tagIsSpace = tagIsSpace;
            return this;
        }

        /**
         * 开启crash收集
         * @param isOpenCrash
         * @return
         */
        public Builder setOpenCrash(boolean isOpenCrash){
            this.openCrash=isOpenCrash;
            return this;
        }

        /**
         * 第三方crash收集
         * @param uncaughtExceptionHandler
         * @return
         */
        public Builder setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler){
            this.mUncaughtExceptionHandler = uncaughtExceptionHandler;
            return this;
        }

        /**
         * 缓存多少行
         * @param lines
         * @return
         */
        public Builder setCacheLines(int lines){
            this.cacheLines = lines;
            return this;
        }

        /**
         * 缓存多少天
         * @param days
         * @return
         */
        public Builder setCacheDays(int days){
            this.cacheDays=days;
            return this;
        }
    }

}
