package com.yxy.slowmethod.method;

import com.yxy.slowmethod.Method;

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
            Method.Companion.saveSlowMethod(name, System.currentTimeMillis() - startTime);
        }

    }
}
