package com.adair.simple.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.SeekBar;

import com.adair.simple.R;
import com.adair.widget.CircleProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.btn_1)
    Button btn1;
    @BindView(R.id.btn_2)
    Button btn2;
    @BindView(R.id.btn_3)
    Button btn3;

    private CircleProgressBar mProgressBar;
    private SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initProgress();
    }

    private void initProgress() {
        mProgressBar = findViewById(R.id.cpb);
        mSeekBar = findViewById(R.id.seek);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgressBar.setSecondProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofInt(mProgressBar, "progress", 0, 100);
                animator.setDuration(1000);
                animator.setInterpolator(new LinearInterpolator());
                animator.start();
            }
        });
    }


    @OnClick({R.id.btn_1, R.id.btn_2})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_1:
                intent = new Intent(this, CircleImageViewActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_2:
                intent = new Intent(this, GridPagerActivity.class);
                startActivity(intent);
                break;
        }
    }

    @OnClick(R.id.btn_3)
    public void onViewClicked() {
        new Thread(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                while (i < 100) {
                    i += 2;
                    mProgressBar.setProgress(i);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
