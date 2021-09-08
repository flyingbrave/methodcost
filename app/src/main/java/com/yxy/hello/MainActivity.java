package com.yxy.hello;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.textview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ggg();
            }
        });
        fff();
//        private List<String> notTraceMethods = new ArrayList(new String[]{})listOf("<init>", "<clinit>")
         List<String> notTraceMethods = new ArrayList<>();
        notTraceMethods.add("<init>");
        notTraceMethods.add("<clinit>");


    }


    private void fff(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void ggg(){
        try {
            Thread.sleep(2200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
