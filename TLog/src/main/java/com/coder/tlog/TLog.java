package com.coder.tlog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import static com.coder.tlog.TLogConstant.BOTTOM_BORDER;
import static com.coder.tlog.TLogConstant.LEFT_BORDER;
import static com.coder.tlog.TLogConstant.LINE_SEPARATOR;
import static com.coder.tlog.TLogConstant.MAX_LEN;
import static com.coder.tlog.TLogConstant.TOP_BORDER;

/***
 * ================================================
 * @时间  2017/5/9 on 16:41
 * @作者
 * @类名  TLog
 * @描述  日志管理组件（本地日志/文件日志/邮件发送日志/crash日志收集）
 * ================================================
 */

public class TLog {
    private static final int V = 0x01;
    private static final int D = 0x02;
    private static final int I = 0x04;
    private static final int W = 0x08;
    private static final int E = 0x10;
    private static final int A = 0x20;
    private static final int FILE = 0xF1;
    private static final int JSON = 0xF2;
    private static final int XML  = 0xF4;
    private static Context sAppContext;
    private static TLogConfig mTLogConfig;

    public static void init(final TLogConfig config){
        if (config == null){
            throw new IllegalArgumentException("TLogConfig can not be initialized with null");
        }
        mTLogConfig=config;
        sAppContext=mTLogConfig.getContext();
        if (mTLogConfig.isCrashOpen()){
            CrashHandler.getInstance().init(mTLogConfig.getUncaughtExceptionHandler());
            CrashHandler.getInstance().setOnCrashExceptionListener(new CrashLogger(sAppContext));
        }
        TLogToFile.getInstance().init(mTLogConfig);
    }

    public static void v(Object contents) {
        log(V, null, contents);
    }
    public static void v(String tag, Object... contents) {
        log(V, tag, contents);
    }
    public static void d(Object contents) {
        log(D, null, contents);
    }

    public static void d(String tag, Object... contents) {
        log(D, tag, contents);
    }

    public static void i(Object contents) {
        log(I, null, contents);
    }

    public static void i(String tag, Object... contents) {
        log(I, tag, contents);
    }

    public static void w(Object contents) {
        log(W, null, contents);
    }

    public static void w(String tag, Object... contents) {
        log(W, tag, contents);
    }

    public static void e(Object contents) {
        log(E, null, contents);
    }

    public static void e(String tag, Object... contents) {
        log(E, tag, contents);
    }

    public static void a(Object contents) {
        log(A, null, contents);
    }

    public static void a(String tag, Object... contents) {
        log(A, tag, contents);
    }

    public static void file(Object contents) {
        log(FILE, null, contents);
    }

    public static void file(String tag, Object contents) {
        log(FILE, tag, contents);
    }

    public static void json(String contents) {
        log(JSON, null, contents);
    }

    public static void json(String tag, String contents) {
        log(JSON, tag, contents);
    }

    public static void xml(String contents) {
        log(XML, null, contents);
    }

    public static void xml(String tag, String contents) {
        log(XML, tag, contents);
    }

    private static void log(int type, @Nullable String tag, Object... contents) {
        if (!mTLogConfig.isLogSwitch())
            return;
        final String[] processContents = processContents(type, tag, contents);
        tag = processContents[0];
        String msg = processContents[1];
        switch (type) {
            case V:
            case D:
            case I:
            case W:
            case E:
            case A:
                if (type >= mTLogConfig.getLogFilter()) {
                    printLog(type, tag, msg);
                }
                if (mTLogConfig.isLog2FileSwitch()) {
                    print2File(tag, msg,false);
                }
                break;
            case FILE:
                print2File(tag, msg,true);
                break;
            case JSON:
                printLog(D, tag, msg);
                break;
            case XML:
                printLog(D, tag, msg);
                break;
        }
    }
    public static void print2File(final String tag, final String msg,boolean isCrashLog) {
        String time = new SimpleDateFormat("MM-dd HH:mm:ss.SSS ", Locale.getDefault()).format(new Date());
        StringBuilder sb = new StringBuilder();
        if (mTLogConfig.isLogBorderSwitch()) {
            sb.append(TLogConstant.TOP_BORDER)
                    .append(LINE_SEPARATOR)
                    .append(TLogConstant.LEFT_BORDER)
                    .append(time)
                    .append(tag)
                    .append(LINE_SEPARATOR)
                    .append(msg);
            sb.append(BOTTOM_BORDER).append(LINE_SEPARATOR);
        } else {
            sb.append(time)
                    .append(tag)
                    .append(LINE_SEPARATOR)
                    .append(msg)
                    .append(LINE_SEPARATOR);
        }
        sb.append(LINE_SEPARATOR);
        String dateLogContent = sb.toString();
        TLogToFile.getInstance().saveToFile(isCrashLog,dateLogContent);
    }
    public static String[] processContents(int type, String tag, Object... contents) {
        StackTraceElement targetElement = Thread.currentThread().getStackTrace()[5];
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) {
            className = classNameInfo[classNameInfo.length - 1];
        }
        if (className.contains("$")) {
            className = className.split("\\$")[0];
        }
        if (!mTLogConfig.isTagIsSpace()) {// 如果全局tag不为空，那就用全局tag
            tag = mTLogConfig.getGlobalTag();
        } else {// 全局tag为空时，如果传入的tag为空那就显示类名，否则显示tag
            tag = isSpace(tag) ? className : tag;
        }
        String head = mTLogConfig.isLogHeadSwitch()
                ? new Formatter()
                .format("Thread: %s, %s(%s.java:%d)" + LINE_SEPARATOR,
                        Thread.currentThread().getName(),
                        targetElement.getMethodName(),
                        className,
                        targetElement.getLineNumber()).toString()
                : "";
        String body = TLogConstant.NULL_TIPS;
        if (contents != null) {
            if (contents.length == 1) {
                Object object = contents[0];
                body = object == null ?  TLogConstant.NULL : object.toString();
                if (type == JSON) {
                    body = formatJson(body);
                } else if (type == XML) {
                    body = formatXml(body);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, len = contents.length; i < len; ++i) {
                    Object content = contents[i];
                    sb.append(TLogConstant.ARGS)
                            .append("[")
                            .append(i)
                            .append("]")
                            .append(" = ")
                            .append(content == null ?  TLogConstant.NULL : content.toString())
                            .append(TLogConstant.LINE_SEPARATOR);
                }
                body = sb.toString();
            }
        }
        String msg = head + body;
        if (mTLogConfig.isLogBorderSwitch()) {
            StringBuilder sb = new StringBuilder();
            String[] lines = msg.split(LINE_SEPARATOR);
            for (String line : lines) {
                sb.append(LEFT_BORDER).append(line).append(LINE_SEPARATOR);
            }
            msg = sb.toString();
        }
        return new String[]{tag, msg};
    }

    private static String formatJson(String json) {
        try {
            if (json.startsWith("{")) {
                json = new JSONObject(json).toString(4);
            } else if (json.startsWith("[")) {
                json = new JSONArray(json).toString(4);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static String formatXml(String xml) {
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
            xml = xmlOutput.getWriter().toString().replaceFirst(">", ">" + LINE_SEPARATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    private static void printLog(int type, String tag, String msg) {
        if (mTLogConfig.isLogBorderSwitch())
            print(type, tag, TOP_BORDER);

        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            print(type, tag, msg.substring(0, MAX_LEN));
            String sub;
            int index = MAX_LEN;
            for (int i = 1; i < countOfSub; i++) {
                sub = msg.substring(index, index + MAX_LEN);
                print(type, tag, (mTLogConfig.isLogBorderSwitch() ? LEFT_BORDER : "") + sub);
                index += MAX_LEN;
            }
            sub = msg.substring(index, len);
            print(type, tag, (mTLogConfig.isLogBorderSwitch() ? LEFT_BORDER : "") + sub);
        } else {
            print(type, tag, msg);
        }
        if (mTLogConfig.isLogBorderSwitch())
            print(type, tag, BOTTOM_BORDER);
    }

    private static void print(final int type, final String tag, String msg) {
        switch (type) {
            case V:
                Log.v(tag, msg);
                break;
            case D:
                Log.d(tag, msg);
                break;
            case I:
                Log.i(tag, msg);
                break;
            case W:
                Log.w(tag, msg);
                break;
            case E:
                Log.e(tag, msg);
                break;
            case A:
                Log.wtf(tag, msg);
                break;
        }
    }

    private static boolean isSpace(String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void sendToCustomerService(Context context,String userEmail,String serviceEmail,String emailSubject,@Nullable String emailContent,String appName){
        StringBuilder content=new StringBuilder();
        content.append(SystemUtils.getPhoneInfos()).append(emailContent);
        Intent data = new Intent(Intent.ACTION_SEND);
        data.putExtra(Intent.EXTRA_EMAIL, new String[]{serviceEmail});
        data.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        data.putExtra(Intent.EXTRA_TEXT, content.toString());
        String path="file://"+ TLogToFile.getLogFile();
        Log.e("TAG","=====path=======" + path);
        if (!TextUtils.isEmpty(path))
            data.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        data.setType("message/rfc882");
        context.startActivity(Intent.createChooser(data,appName));
    }
}
