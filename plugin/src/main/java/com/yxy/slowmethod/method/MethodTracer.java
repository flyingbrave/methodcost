package com.yxy.slowmethod.method;

import com.android.ddmlib.Log;


import java.util.HashMap;

public class MethodTracer {
    private static long methodStartTime = 0;
    public static final String CLASS_PATH = "com/yxy/slowmethod/method/MethodTracer";
    public static final String METHOD_RECORD_METHOD_START = "recordMethodStart";
    public static final String METHOD_RECORD_METHOD_END = "recordMethodEnd";
    public static final String METHOD_RECORD_METHOD_END_PARAMS = "(Ljava/lang/String;)V";
    public static HashMap<String,Long> timeHm = new HashMap<>();
    public static void recordMethodStart(String name) {
        long methodStartTime = System.currentTimeMillis();
        timeHm.put(name,methodStartTime);
    }

    public static void recordMethodEnd(String name) {
        Long startTime = timeHm.get(name);
        if(startTime!=null && startTime>0){
            timeHm.remove(name);
            Log.d("tag5","测试数据"+(traceToString(3, new Throwable().getStackTrace(), 15)));
//            Method.Companion.saveSlowMethod(name, System.currentTimeMillis() - startTime);
        }

    }

    public static String traceToString(int skipStackCount, StackTraceElement[] stackArray, int maxLineCount){
        if (stackArray==null||stackArray.length==0) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < stackArray.length - skipStackCount; i++) {
            if (i <= skipStackCount) {
                continue;
            }
            b.append(stackArray[i]);
            b.append("\n");
            if (i > maxLineCount) {
                break;
            }
        }
        return b.toString();
    }
}
