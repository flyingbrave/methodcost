package com.yxy.hello;

import android.app.Application;
import android.util.Log;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("tag5","App  onCreate");
    }
}
