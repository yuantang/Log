package com.coder.tlog;
import android.os.Build;

import java.util.Locale;

/***
 * ================================================
 * @时间  2017/5/12 on 9:51
 * @作者
 * @类名  SystemUtils
 * @描述  手机系统信息
 * ================================================
 */
public class SystemUtils {
    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    private static String getSystemLanguage(){
        return Locale.getDefault().getLanguage();
    }
    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    private static Locale[] getSystemLanguageList(){
        return Locale.getAvailableLocales();
    }
    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    private static String getSystemVersion(){
        return Build.VERSION.RELEASE;
    }
    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    private static String getSystemModel(){
        return Build.MODEL;
    }
    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    private static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }
    public static String getPhoneInfos(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("=======phone system info=======")
                .append( TLogConstant.LINE_SEPARATOR)
                .append("phone system language:")
                .append(getSystemLanguage())
                .append( TLogConstant.LINE_SEPARATOR)
                .append("phone system version:")
                .append(getSystemVersion())
                .append( TLogConstant.LINE_SEPARATOR)
                .append("phone system model:")
                .append(getSystemModel())
                .append( TLogConstant.LINE_SEPARATOR)
                .append("phone system brand:")
                .append(getDeviceBrand())
                .append(TLogConstant.LINE_SEPARATOR)
                .append("======phone system info end======")
                .append(TLogConstant.LINE_SEPARATOR);

        return stringBuilder.toString();
    }
}
