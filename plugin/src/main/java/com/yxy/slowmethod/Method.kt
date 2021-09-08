package com.yxy.slowmethod

class Method {

    companion object{
//        fun saveSlowMethod(methodStr: String, time: Long) {
//            if(time>0){
//
//            }
//            val fullClassName = methodStr.split("&").firstOrNull() ?: ""
//            val methodName = methodStr.split("&")[1] ?: ""
//            val classNameStartIndex = fullClassName.lastIndexOf(".")
//            val saveSlowMethodTime = System.currentTimeMillis()
//            if (classNameStartIndex > 0) {
//                val className =
//                    fullClassName.subSequence(classNameStartIndex + 1, fullClassName.length).toString()
//                val pkgName = fullClassName.subSequence(0, classNameStartIndex).toString()
//                var difTime = "noCompare"
//                Log.d(
//                    "pkgName: $pkgName  className: $className  methodName: $methodName" +
//                            "\n costTimeMs: $time \n difTime: $difTime \n currentTime: $saveSlowMethodTime  " +
//                            "\n callStack: ${traceToString(3, Throwable().stackTrace, 15)} "
//                )
//
//
//            }
//        }
//
//        fun traceToString(
//            skipStackCount: Int,
//            stackArray: Array<StackTraceElement>,
//            maxLineCount: Int = 20
//        ): String {
//            if (stackArray.isEmpty()) {
//                return "[]"
//            }
//
//            val b = StringBuilder()
//            for (i in 0 until stackArray.size - skipStackCount) {
//                if (i <= skipStackCount) {
//                    continue
//                }
//                b.append(stackArray[i])
//                b.append("\n")
//                if (i > maxLineCount) {
//                    break
//                }
//            }
//
//            return b.toString()
//        }
    }


}