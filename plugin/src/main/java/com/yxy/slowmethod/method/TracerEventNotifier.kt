package com.yxy.slowmethod.method

object TracerEventNotifier {

    @JvmField
    var appSpeedNotifier: TracerEvent = FakeEventListener()

    @JvmField
    var methodCostNotifier:MethodCostEvent = FakeMethodCostListener()

    interface TracerEvent {
        fun applicationCreateTime(attachBaseContextTime:Long, createEndTime:Long){}
        fun activityDrawFinish(activityName:String, time: Long){}
        fun activityCreateEnd(activity: Any, time: Long){}
        fun activityCreateStart(activity: Any, time: Long){}
        fun activityResumeEnd(activity: Any, time: Long){}
    }

    interface MethodCostEvent{
        fun methodCost(method:String, time:Long){}
    }

    class FakeEventListener:TracerEvent

    class FakeMethodCostListener:MethodCostEvent

}