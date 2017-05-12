package com.coder.tlog;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/***
 * ================================================
 * @时间  2017/5/12 on 9:31
 * @作者  Yuan
 * @类名  TLogToFile
 * @描述  日志写入文件
 * ================================================
 */
public class TLogToFile {
    private static final String TAG = "TLogToFile";
    private static TLogConfig mTLogConfig;
    private List<String> logCache = null;
    private ExecutorService mExecutorService;
    private ReentrantLock mReentrantLock;
    private static TLogToFile mInstance;
    public static TLogToFile getInstance() {
        if (mInstance == null) {
            synchronized (TLogToFile.class) {
                if (mInstance == null) {
                    mInstance = new TLogToFile();
                }
            }
        }
        return mInstance;
    }

    public void init(TLogConfig config) {
        this.mTLogConfig = config;
        logCache = new ArrayList<>(mTLogConfig.getCacheLineSize());
        mExecutorService = Executors.newSingleThreadExecutor();
        mReentrantLock = new ReentrantLock();
    }

    public void saveToFile(final boolean isCrashLog,final String logContent) {
        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadExecutor();
        }
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {

                if (isCrashLog) {
                    saveLog(isCrashLog, logContent);
                } else {
                    addCache(logContent);
                    if (getCacheSize()>= mTLogConfig.getCacheLineSize()) {
                        saveLog(isCrashLog, logContent);
                    }
                }
            }
        });
    }

    /**
     * 添加到缓冲区
     * @param log
     */
    private void addCache(String log) {
        if (log == null) {
            return ;
        }
        mReentrantLock.lock();
        try {
            logCache.add(log);
        } finally {
            mReentrantLock.unlock();
        }
    }

    /**
     * 获取缓存大小
     * @return
     */
    private int getCacheSize(){
        if (logCache==null){
            return 0;
        }
        mReentrantLock.lock();
        try{
            return logCache.size();
        }finally {
            mReentrantLock.unlock();
        }
    }

    /**
     * 清空缓冲区
     */
    private void clearLogCache(){
        mReentrantLock.lock();
        try{
            logCache.clear();
        }finally {
            mReentrantLock.unlock();
        }
    }
    /**
     * 保存日志到文件中
     */
    private void saveLog(@Nullable Boolean isCrash,@Nullable String crashLog) {
        String logFilePath= getLogFile();
        Log.e(TAG, "log to " + logFilePath + " success!");
        if (logFilePath==null || logFilePath.length()<= 0 ){
            return;
        }
        mReentrantLock.lock();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(logFilePath, true), 1024);
            if (isCrash){
                bw.write(crashLog);
                bw.newLine();
            }else {
                for (String log : logCache) {
                    bw.write(log);
                    bw.newLine();
                }
                clearLogCache();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                mReentrantLock.unlock();
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化保存路径
     * @return
     */
    public static String getLogFile(){
        String fullPath=null;
        try {
            final String Path = mTLogConfig.getLogFileDir();
            File file=new File(Path);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileUtils.delOutDateFile(file,TLogConfig.getCacheSaveDay());
            StringBuilder builder=new StringBuilder();
            builder.append(file.toString());
            builder.append(File.separator);
            builder.append(FileUtils.getTodayLogFileName());
            builder.append(TLogConstant.FileSuffix);
            File fullFile = new File(builder.toString());
            if (!fullFile.exists()){
                fullFile.createNewFile();
            }
            fullPath=fullFile.getAbsolutePath();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG,"==========fullPath============="+fullPath);
        return fullPath;
    }

}
