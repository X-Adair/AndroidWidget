package com.adair.simple.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.adair.simple.R;
import com.adair.widget.CircleImageView;
import com.android.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CircleImageViewActivity extends AppCompatActivity {

    @BindView(R.id.civ)
    CircleImageView civ;
    @BindView(R.id.cb_circle)
    CheckBox cbCircle;
    @BindView(R.id.sb_corner_radius)
    SeekBar sbCornerRadius;
    @BindView(R.id.sb_top_left_radius)
    SeekBar sbTopLeftRadius;
    @BindView(R.id.sb_top_right_radius)
    SeekBar sbTopRightRadius;
    @BindView(R.id.sb_bottom_right_radius)
    SeekBar sbBottomRightRadius;
    @BindView(R.id.sb_bottom_left_radius)
    SeekBar sbBottomLeftRadius;
    @BindView(R.id.cb_cover)
    CheckBox cbCover;
    @BindView(R.id.sb_border_width)
    SeekBar sbBorderWidth;


    private int mMaxRadius = 100;

    private int mBorderWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_image_view);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mBorderWidth = DensityUtil.dp2px(getApplicationContext(), 60);

        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"点击事件",Toast.LENGTH_SHORT).show();
            }
        });

        civ.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(),"长点击事件",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        cbCover.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                civ.setCornerType(isChecked ? CircleImageView.TYPE_ARC : CircleImageView.TYPE_WIDTH);
            }
        });
        cbCover.setChecked(civ.getCornerType() == 1);

        cbCircle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                civ.setCircle(isChecked);
            }
        });
        cbCircle.setChecked(civ.isCircle());

        sbCornerRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float radius = progress * 1.0f / 100 * mMaxRadius;
                civ.setCornerRadius(DensityUtil.dp2px(getApplicationContext(), radius));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbTopLeftRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float radius = progress * 1.0f / 100 * mMaxRadius;
                civ.setCornerTopLeftRadius(DensityUtil.dp2px(getApplicationContext(), radius));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        sbTopRightRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float radius = progress * 1.0f / 100 * mMaxRadius;
                civ.setCornerTopRightRadius(DensityUtil.dp2px(getApplicationContext(), radius));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbBottomRightRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float radius = progress * 1.0f / 100 * mMaxRadius;
                civ.setCornerBottomRightRadius(DensityUtil.dp2px(getApplicationContext(), radius));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbBottomLeftRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float radius = progress * 1.0f / 100 * mMaxRadius;
                civ.setCornerBottomLeftRadius(DensityUtil.dp2px(getApplicationContext(), radius));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbBorderWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float borderWidth = progress * 1.0f / 100 * mBorderWidth;
                civ.setBorderWidth(borderWidth);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
