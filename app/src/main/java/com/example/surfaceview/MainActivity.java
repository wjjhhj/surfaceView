package com.example.surfaceview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.surfaceview.surface_view.SurfaceViewPan;

public class MainActivity extends AppCompatActivity {
 private SurfaceViewPan mPan;
 private ImageView mIvStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPan=findViewById(R.id.lucky_pan);
        mIvStart=findViewById(R.id.img_start);
        mIvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPan.isStar()){
                    mPan.luckyStar();
                    mIvStart.setImageResource(R.drawable.stop);
                }else {
                    if (!mPan.isShouldEnd()){
                        mPan.luckyEnd();
                        mIvStart.setImageResource(R.drawable.start);
                    }
                }
            }
        });
    }
}
