package com.coder.tlog;
import android.support.annotation.Nullable;

import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {
    /** 系统默认的UncaughtException处理类 */
    private UncaughtExceptionHandler mDefaultHandler;
    /** 第三方UncaughtException处理类*/
    private UncaughtExceptionHandler originalHandler;
    /** CrashHandler实例 */
    private static CrashHandler instance = new CrashHandler();
    /** 当crash发生后的接口回调 **/
    private OnCrashExceptionListener mOnCrashExceptionListener;
    /**当捕获到Crash异常后会通过该接口回调*/
    public interface OnCrashExceptionListener{
        void onCaughtCrashException(Thread thread, Throwable ex);
    }

    public void setOnCrashExceptionListener(OnCrashExceptionListener onCrashExceptionListener) {
        this.mOnCrashExceptionListener = onCrashExceptionListener;
    }
    private CrashHandler(){

    }
    public static CrashHandler getInstance(){
        return instance;
    }

    public void init(@Nullable UncaughtExceptionHandler uncaughtExceptionHandler) {
        originalHandler=uncaughtExceptionHandler;
        mDefaultHandler=Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
    @Override
    public void uncaughtException(final Thread t,final Throwable e) {
        if (mOnCrashExceptionListener !=null) {
            mOnCrashExceptionListener.onCaughtCrashException(t,e);
        }
        if (originalHandler !=null) {
            originalHandler.uncaughtException(t, e);
        }
        if (mOnCrashExceptionListener == null && originalHandler == null) {
            mDefaultHandler.uncaughtException(t, e);
        }
    }
}
