package com.adair.widget.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.adair.widget.R;

/**
 * 圆弧形进度条
 * <p>
 * created at 2018/9/28 10:23
 *
 * @author XuShuai
 * @version v1.0
 */
public class CircularProgressBar extends View {

    private static final String TAG = "CircularProgressBar";

    /**
     * Duration of smooth progress animations.
     */
    private static final int PROGRESS_ANIM_DURATION = 80;

    //默认弧度
    private static final float DEFAULT_ARC = 360;
    //默认颜色
    private static final int DEFAULT_ARC_COLOR = 0xFFFFFF00;
    //默认进度条宽度,单位px
    private static final int DEFAULT_PROGRESS_WIDTH = 20;
    //默认进度条颜色
    private static final int DEFAULT_PROGRESS_COLOR = 0xFFff0000;
    //默认最大进度值
    private static final int DEFAULT_MAX_PROGRESS = 100;
    //开始角度
    private static final float DEFAULT_START_ABGLE = -90;

    //圆弧弧度,默认360度
    private float mArc = 360;

    private float mArcRadius;

    //圆弧颜色
    private int mArcColor = 0xFFFF00;
    //开始角度
    private float mStartAngle;

    //进度条宽度
    private float mProgressWidth = 20f;
    //进度条颜色
    private int mProgressColor = 0xff0000;
    //最大进度
    private int mMaxProgress = 100;
    //当前进度
    private int mProgress;
    //显示进度文字
    private boolean showProgressText;

    private float mProgressTextSize;
    //画图画笔
    private Paint mPaint;
    //画圆弧的矩形
    private RectF mRectF;

    //文字画笔
    private Paint mTextPaint;
    //文字矩形，用于测量文字宽高
    private Rect mTextRect;

    private int mWidth;
    private int mHeight;

    public CircularProgressBar(Context context) {
        this(context, null);
    }

    public CircularProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar);
        mArc = a.getFloat(R.styleable.CircularProgressBar_arc, DEFAULT_ARC);
        mArcColor = a.getColor(R.styleable.CircularProgressBar_arc_color, DEFAULT_ARC_COLOR);
        mStartAngle = a.getFloat(R.styleable.CircularProgressBar_start_angle, DEFAULT_START_ABGLE);
        mProgressWidth = a.getDimension(R.styleable.CircularProgressBar_progress_width, DEFAULT_PROGRESS_WIDTH);
        mProgressColor = a.getColor(R.styleable.CircularProgressBar_progress_color, DEFAULT_PROGRESS_COLOR);
        mMaxProgress = a.getInt(R.styleable.CircularProgressBar_max_progress, DEFAULT_MAX_PROGRESS);
        mProgress = a.getInt(R.styleable.CircularProgressBar_progress, 0);
        showProgressText = a.getBoolean(R.styleable.CircularProgressBar_show_progress_Text, false);
        mProgressTextSize = a.getDimension(R.styleable.CircularProgressBar_progress_text_size, 32f);
        mArcRadius = a.getDimension(R.styleable.CircularProgressBar_arc_radius, -1f);
        a.recycle();
        init();
    }


    //初始化参数及画笔
    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mRectF = new RectF();
    }

    //初始化文字画笔,在需要画文字时才需要调用
    private void initTextPaint() {
        if (mTextPaint == null) {
            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            mTextRect = new Rect();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        float radius;

        if (mWidth < mHeight) {
            radius = (mWidth - mProgressWidth) / 2f;
        } else {
            radius = (mHeight - mProgressWidth) / 2f;
        }
        if (mArcRadius != -1) {
            radius = mArcRadius > radius ? radius : mArcRadius;
        }
        mRectF.set(mWidth / 2f - radius, mHeight / 2f - radius, mWidth / 2f + radius, mHeight / 2f + radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mArcColor);
        canvas.drawArc(mRectF, mStartAngle, mArc, false, mPaint);
        float percent = mProgress * 1.0f / mMaxProgress;
        float progressArc = mArc * percent;
        mPaint.setColor(mProgressColor);
        canvas.drawArc(mRectF, mStartAngle, progressArc, false, mPaint);
        if (showProgressText) {
            initTextPaint();
            String text = (int) (percent * 100) + "%";
            mTextPaint.setTextSize(mProgressTextSize);
            mTextPaint.getTextBounds(text, 0, text.length(), mTextRect);
            float textWidth = mTextPaint.measureText(text);
            int textHeight = mTextRect.height();
            canvas.drawText(text, mRectF.centerX() - textWidth / 2f, mRectF.centerY() + textHeight / 2f, mTextPaint);
        }
    }

    //=======================================对外方法================================================
    public float getArc() {
        return mArc;
    }

    public void setArc(float arc) {
        mArc = arc;
    }

    public int getArcColor() {
        return mArcColor;
    }

    public void setArcColor(int arcColor) {
        mArcColor = arcColor;
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float startAngle) {
        mStartAngle = startAngle;
    }

    public float getProgressWidth() {
        return mProgressWidth;
    }

    public void setProgressWidth(float progressWidth) {
        mProgressWidth = progressWidth;
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }

    public int getProgress() {
        return mProgress;
    }

    public synchronized void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    public boolean isShowProgressText() {
        return showProgressText;
    }

    public void setShowProgressText(boolean showProgressText) {
        this.showProgressText = showProgressText;
    }

    public float getProgressTextSize() {
        return mProgressTextSize;
    }

    public void setProgressTextSize(float progressTextSize) {
        mProgressTextSize = progressTextSize;
    }
}
