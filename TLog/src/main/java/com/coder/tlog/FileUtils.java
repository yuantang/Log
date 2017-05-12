package com.coder.tlog;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/***
 * ================================================
 * @时间  2017/5/12 on 9:39
 * @作者
 * @类名  FileUtils
 * @描述  日志文件工具
 * ================================================
 */
public class FileUtils {
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    /**
     * 当天的日志名称
     * @return
     */
    public static String getTodayLogFileName(){
        Date nowTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        return simpleDateFormat.format(nowTime);
    }
    /**
     * 删除过期日志
     */
    public static void delOutDateFile(File fileDir, int saveDays) {
        if(null == fileDir || !fileDir.isDirectory() || saveDays <= 0){
            return;
        }
        File [] files = fileDir.listFiles();
        if(null == files || files.length == 0){
            return;
        }
        for (File file : files) {
            String dateString = file.getName();
            if(canDeleteLog(dateString, saveDays)){
                file.delete();
            }
        }
    }

    /***
     * 是否可以删除日志
     * @param createDay
     * @param saveDays
     * @return
     */
    public static boolean canDeleteLog(String createDay , int saveDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1 * saveDays);
        Date expiredDate = calendar.getTime();
        Date createDate = null;
        try {
            createDate = new SimpleDateFormat(DATE_PATTERN).parse(createDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return createDate != null && createDate.before(expiredDate);
    }

}
