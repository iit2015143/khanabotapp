package com.anuragmalti.iamroot.khanabot;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class PopUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

//        WindowManager manager = (WindowManager) getSystemService(Activity.WINDOW_SERVICE);
//        int width, height;
//        WindowManager.LayoutParams params;
//
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
//            width = manager.getDefaultDisplay().getWidth();
//            height = manager.getDefaultDisplay().getHeight();
//        } else {
//            Point point = new Point();
//            manager.getDefaultDisplay().getSize(point);
//            width = point.x;
//            height = point.y;
//        }
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(this.getWindow().getAttributes());
//        lp.width = width;
//        lp.height = height*3/4;
//        this.getWindow().setAttributes(lp);
//
//        this.setFinishOnTouchOutside(true);


    }
}
