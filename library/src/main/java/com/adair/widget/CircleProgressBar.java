package com.adair.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * 圆弧形进度条
 * <p>
 * created at 2018/9/28 10:23
 *
 * @author XuShuai
 * @version v1.0
 */
public class CircleProgressBar extends View {

    private static final String TAG = "CircularProgressBar";

    /**
     * Duration of smooth progress animations.
     */
    private static final int PROGRESS_ANIM_DURATION = 80;

    private static final DecelerateInterpolator PROGRESS_ANIM_INTERPOLATOR = new DecelerateInterpolator();


    //进度条背景色
    private int mBackgroundColor;
    //第二进度条颜色
    private int mSecondProgressColor;
    //进度条颜色
    private int mProgressColor;
    //进度最大值
    private int mMaxProgress;
    //当前第二进度
    private int mSecondProgress;
    //当前进度
    private int mProgress;
    //圆形进度的半径
    private float mRadius;
    //圆形进度条的宽度
    private float mProgressWidth;
    //是否显示文字
    private boolean mShowText;
    //文字颜色
    private int mTextColor;
    //文字内容
    private String mTextContent;
    //文字大小
    private float mTextSize;

    //进度条画笔
    private Paint mProgressPaint;
    //进度条矩形，用于画进度弧形
    private RectF mProgressRectF;


    //文字画笔
    private Paint mTextPaint;
    //文字矩形，用于测量文字宽高
    private Rect mTextRect;

    //进度条控件宽度
    private int mWidth;
    //进度条控件高度
    private int mHeight;

    private boolean mAttach;

    private long mUiThread;
    private RefreshProgressRunnable mRefreshProgressRunnable;
    private boolean mRefreshIsPosted;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mUiThread = Thread.currentThread().getId();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        mBackgroundColor = a.getColor(R.styleable.CircleProgressBar_background_color, 0xFFF38181);
        mSecondProgressColor = a.getColor(R.styleable.CircleProgressBar_second_progress_color, 0xFFFCE38A);
        mProgressColor = a.getColor(R.styleable.CircleProgressBar_progress_color, 0xFF95E1D3);
        mMaxProgress = a.getInt(R.styleable.CircleProgressBar_max, 100);
        mSecondProgress = a.getInt(R.styleable.CircleProgressBar_second_progress, 0);
        mProgress = a.getInt(R.styleable.CircleProgressBar_progress, 0);
        mRadius = a.getDimension(R.styleable.CircleProgressBar_radius, -1);
        mProgressWidth = a.getDimension(R.styleable.CircleProgressBar_progress_width, 20);
        mShowText = a.getBoolean(R.styleable.CircleProgressBar_show_text, false);
        mTextColor = a.getColor(R.styleable.CircleProgressBar_text_color, 0xFF000000);
        mTextSize = a.getDimension(R.styleable.CircleProgressBar_text_size, 20);
        mTextContent = a.getString(R.styleable.CircleProgressBar_text);
        a.recycle();
        init();
    }

    //初始化初始化进度条画笔
    private void init() {
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setStrokeJoin(Paint.Join.ROUND);

        mProgressRectF = new RectF();
    }

    //初始化文字画笔,在需要画文字时才需要调用
    private void initTextPaint() {
        if (mTextPaint == null) {
            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            mTextPaint.setColor(mTextColor);
            mTextRect = new Rect();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        //可用的最大半径
        float maxRadius = (Math.min(mWidth, mHeight) - mProgressWidth) / 2f;
        if (mRadius != -1 && mRadius > 0) {
            mRadius = Math.min(maxRadius, mRadius);
        } else {
            mRadius = maxRadius;
        }

        mProgressRectF.left = mWidth / 2f - mRadius;
        mProgressRectF.top = mHeight / 2f - mRadius;
        mProgressRectF.right = mWidth / 2f + mRadius;
        mProgressRectF.bottom = mWidth / 2f + mRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画背景圆
        mProgressPaint.setColor(mBackgroundColor);
        canvas.drawCircle(mProgressRectF.centerX(), mProgressRectF.centerY(), mRadius, mProgressPaint);
        //画第二进度条
        float secondRadian = mSecondProgress * 1.0f / mMaxProgress * 360;
        mProgressPaint.setColor(mSecondProgressColor);
        canvas.drawArc(mProgressRectF, -90, secondRadian, false, mProgressPaint);
        //画第一进度条
        float radian = mProgress * 1.0f / mMaxProgress * 360;
        mProgressPaint.setColor(mProgressColor);
        canvas.drawArc(mProgressRectF, -90, radian, false, mProgressPaint);

        //画文字
        if (mShowText) {
            initTextPaint();
            if (TextUtils.isEmpty(mTextContent)) {
                mTextContent = mProgress * 100 / mMaxProgress + "%";
            }
            //测量Text
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.getTextBounds(mTextContent, 0, mTextContent.length(), mTextRect);
            float textWidth = mTextPaint.measureText(mTextContent);
            //绘制文字
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float baseLine = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;

            canvas.drawText(mTextContent, mProgressRectF.centerX() - textWidth / 2f, mProgressRectF.centerY() + baseLine, mTextPaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttach = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        mAttach = false;
        super.onDetachedFromWindow();
    }

    //=======================================对外方法================================================

    /**
     * 获取进度
     *
     * @return 进度值
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * 设置进度
     *
     * @param progress 进度
     */
    public synchronized void setProgress(int progress) {
        mProgress = progress;
        refreshProgress();
    }

    /**
     * 获取第二进度
     *
     * @return 进度
     */
    public synchronized int getSecondProgress() {
        return mSecondProgress;
    }

    /**
     * 第二进度
     *
     * @param secondProgress 第二进度
     */
    public synchronized void setSecondProgress(int secondProgress) {
        mSecondProgress = secondProgress;
        refreshProgress();
    }

    //======================================================================================

    private synchronized void refreshProgress() {
        if (mUiThread == Thread.currentThread().getId()) {
            doRefreshProgress();
        } else {
            if (mRefreshProgressRunnable == null) {
                mRefreshProgressRunnable = new RefreshProgressRunnable();
            }

            if (mAttach && !mRefreshIsPosted) {
                post(mRefreshProgressRunnable);
                mRefreshIsPosted = true;
            }
        }
    }

    private synchronized void doRefreshProgress() {
        invalidate();
    }

    private class RefreshProgressRunnable implements Runnable {
        @Override
        public void run() {
            synchronized (CircleProgressBar.class) {
                doRefreshProgress();
                mRefreshIsPosted = false;
            }
        }
    }
}
