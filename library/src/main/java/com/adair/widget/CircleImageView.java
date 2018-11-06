package com.adair.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.IntDef;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 圆形圆角图片,可以带边框
 * <p>
 * created at 2018/10/30 15:46
 *
 * @author XuShuai
 * @version v1.0
 */
public class CircleImageView extends AppCompatImageView {

    public static final int TYPE_ARC = 1;//边框内外圆角一致模式
    public static final int TYPE_WIDTH = 2;//边框圆角宽度与边框宽度一致模式

    @IntDef({TYPE_ARC, TYPE_WIDTH})
    @Retention(RetentionPolicy.SOURCE)
    private @interface CornerType {//圆角类型
    }

    private PorterDuffXfermode mXfermode_DST_IN;

    private PorterDuffXfermode mXfermode_DST_OVER;


    private int mWidth;//控件宽度
    private int mHeight;//控件高度

    private int mRealWidth;//实际显示区域宽度,除去padding
    private int mRealHeight;//实际显示区域高度,除去padding

    private float mBorderWidth;//边框宽度
    private float mHalfBorderWidth;//1/2边框宽度
    private int mBorderColor;//边框颜色

    private boolean isCircle;//是否是圆形图片
    private float mRadius;//圆形图片半径

    private int mCornerType;//实现圆角类型,1边框圆角和图片圆角一致,2在圆角处边框宽度保持一致
    private float mCornerRadius; // 统一设置圆角半径，优先级高于单独设置每个角的半径
    private float mCornerTopLeftRadius; // 左上角圆角半径
    private float mCornerTopRightRadius; // 右上角圆角半径
    private float mCornerBottomLeftRadius; // 左下角圆角半径
    private float mCornerBottomRightRadius; // 右下角圆角半径

    private RectF mSrcRectF;   // 图片占的矩形区域
    private float[] mSrcRadii;
    private Path mSrcPath;//绘图区域path
    private Paint mSrcPaint;//绘图画笔

    private RectF mBorderRectF;//边框矩形区域
    private float[] mBorderRadii;
    private Path mBorderPath;//边框区域Path
    private Paint mBorderPaint;//边框画笔

    private RectF mContentRectF;//实际内容区域

    private int mOriginPaddingLeft;
    private int mOriginPaddingTop;
    private int mOriginPaddingRight;
    private int mOriginPaddingBottom;

    private boolean isFirst = true;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.CENTER_CROP);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        isCircle = a.getBoolean(R.styleable.CircleImageView_isCircle, false);
        mBorderWidth = a.getDimension(R.styleable.CircleImageView_border_width, 0f);
        mHalfBorderWidth = mBorderWidth * 0.5f;
        mBorderColor = a.getColor(R.styleable.CircleImageView_border_color, getResources().getColor(android.R.color.transparent));
        mCornerRadius = a.getDimension(R.styleable.CircleImageView_corner_radius, 0);
        mCornerTopLeftRadius = a.getDimension(R.styleable.CircleImageView_corner_top_left_radius, mCornerRadius);
        mCornerTopRightRadius = a.getDimension(R.styleable.CircleImageView_corner_top_right_radius, mCornerRadius);
        mCornerBottomLeftRadius = a.getDimension(R.styleable.CircleImageView_corner_bottom_left_radius, mCornerRadius);
        mCornerBottomRightRadius = a.getDimension(R.styleable.CircleImageView_corner_bottom_right_radius, mCornerRadius);
        mCornerType = a.getInt(R.styleable.CircleImageView_corner_type, TYPE_WIDTH);
        a.recycle();
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        if (isFirst) {//保存原始Padding
            mOriginPaddingLeft = getPaddingLeft();
            mOriginPaddingTop = getPaddingTop();
            mOriginPaddingRight = getPaddingRight();
            mOriginPaddingBottom = getPaddingBottom();
            isFirst = false;
        }

        //当Border 不覆盖Src时,需要在区域留出画border宽度的区域用于绘制border
        int a = (int) (mBorderWidth + 0.5f);
        setPadding(mOriginPaddingLeft + a, mOriginPaddingTop + a, mOriginPaddingRight + a, mOriginPaddingBottom + a);
        mRealWidth = mWidth - getPaddingLeft() - getPaddingRight();
        mRealHeight = mHeight - getPaddingTop() - getPaddingBottom();

        //初始化圆角矩形半径
        calculateRadii();
        //计算显示
        calculateRectAndPath();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.saveLayer(mContentRectF, null, Canvas.ALL_SAVE_FLAG);
        canvas.saveLayer(mSrcRectF, null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        canvas.drawPath(mSrcPath, mSrcPaint);
        canvas.restore();
        if (mCornerType == 1) {
            mBorderPaint.setStyle(Paint.Style.FILL);
            mBorderPaint.setXfermode(mXfermode_DST_OVER);
        } else if (mCornerType == 2) {
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setXfermode(null);
        }
        canvas.drawPath(mBorderPath, mBorderPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return inTouchableArea(event.getX(), event.getY()) && super.onTouchEvent(event);
    }


    //初始化
    private void init() {
        mXfermode_DST_IN = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mXfermode_DST_OVER = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);

        //初始化绘图信息
        mSrcRectF = new RectF();
        mSrcRadii = new float[8];
        mSrcPath = new Path();
        mSrcPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mSrcPaint.setStyle(Paint.Style.FILL);
        mSrcPaint.setXfermode(mXfermode_DST_IN);

        //初始化边框绘制
        mBorderRectF = new RectF();
        mBorderRadii = new float[8];
        mBorderPath = new Path();
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);

        mContentRectF = new RectF();
    }

    //计算圆角矩形的矩形半径
    private void calculateRadii() {
        if (isCircle) {//如果是圆形,直接返回
            return;
        }
        //border的圆角半径固定
        mBorderRadii[0] = mBorderRadii[1] = mCornerTopLeftRadius;
        mBorderRadii[2] = mBorderRadii[3] = mCornerTopRightRadius;
        mBorderRadii[4] = mBorderRadii[5] = mCornerBottomRightRadius;
        mBorderRadii[6] = mBorderRadii[7] = mCornerBottomLeftRadius;


        if (mCornerType == TYPE_ARC) {//内外同弧度模式,图片与边框圆角半径保持一致
            mSrcRadii[0] = mSrcRadii[1] = mCornerTopLeftRadius;
            mSrcRadii[2] = mSrcRadii[3] = mCornerTopRightRadius;
            mSrcRadii[4] = mSrcRadii[5] = mCornerBottomRightRadius;
            mSrcRadii[6] = mSrcRadii[7] = mCornerBottomLeftRadius;
        } else {//保持边框圆角宽度与边框宽度一致,边框弧度半径比图片弧度半径多半个边框宽度,保证内外圆角中心在同一点上,故圆角半径小于边框宽度,内部图片为直角
            mSrcRadii[0] = mSrcRadii[1] = mCornerTopLeftRadius - mHalfBorderWidth;
            mSrcRadii[2] = mSrcRadii[3] = mCornerTopRightRadius - mHalfBorderWidth;
            mSrcRadii[4] = mSrcRadii[5] = mCornerBottomRightRadius - mHalfBorderWidth;
            mSrcRadii[6] = mSrcRadii[7] = mCornerBottomLeftRadius - mHalfBorderWidth;
        }
    }

    //计算内容矩形区域与绘制路径
    private void calculateRectAndPath() {
        mSrcPath.reset();
        mBorderPath.reset();

        if (isCircle) {//直接显示为圆形图片
            mRadius = Math.min(mRealWidth, mRealHeight) * 0.5f;
            mSrcRectF.left = getPaddingLeft() + mRealWidth * 0.5f - mRadius;
            mSrcRectF.top = getPaddingTop() + mRealHeight * 0.5f - mRadius;
            mSrcRectF.right = getPaddingLeft() + mRealWidth * 0.5f + mRadius;
            mSrcRectF.bottom = getPaddingTop() + mRealHeight * 0.5f + mRadius;

            mBorderRectF.left = mSrcRectF.left - mBorderWidth;
            mBorderRectF.top = mSrcRectF.top - mBorderWidth;
            mBorderRectF.right = mSrcRectF.right + mBorderWidth;
            mBorderRectF.bottom = mSrcRectF.bottom + mBorderWidth;

            mSrcPath.addCircle(mSrcRectF.centerX(), mSrcRectF.centerY(), mRadius, Path.Direction.CW);
            if (mCornerType == TYPE_ARC) {
                mBorderPath.addCircle(mBorderRectF.centerX(), mBorderRectF.centerY(), mRadius + mBorderWidth, Path.Direction.CW);
            } else {
                mBorderPath.addCircle(mBorderRectF.centerX(), mBorderRectF.centerY(), mRadius + mHalfBorderWidth, Path.Direction.CW);
            }
        } else {//圆角矩形
            mSrcRectF.left = getPaddingLeft();
            mSrcRectF.top = getPaddingTop();
            mSrcRectF.bottom = mHeight - getPaddingBottom();
            mSrcRectF.right = mWidth - getPaddingRight();

            if (mCornerType == TYPE_ARC) {
                mBorderRectF.left = mSrcRectF.left - mBorderWidth;
                mBorderRectF.top = mSrcRectF.top - mBorderWidth;
                mBorderRectF.right = mSrcRectF.right + mBorderWidth;
                mBorderRectF.bottom = mSrcRectF.bottom + mBorderWidth;
            } else {
                mBorderRectF.left = mSrcRectF.left - mHalfBorderWidth;
                mBorderRectF.top = mSrcRectF.top - mHalfBorderWidth;
                mBorderRectF.right = mSrcRectF.right + mHalfBorderWidth;
                mBorderRectF.bottom = mSrcRectF.bottom + mHalfBorderWidth;
            }

            mSrcPath.addRoundRect(mSrcRectF, mSrcRadii, Path.Direction.CW);
            mBorderPath.addRoundRect(mBorderRectF, mBorderRadii, Path.Direction.CW);
        }

        //实际整个View显示区域，点击事件范围
        mContentRectF.left = mSrcRectF.left - mBorderWidth;
        mContentRectF.top = mSrcRectF.top - mBorderWidth;
        mContentRectF.right = mSrcRectF.right + mBorderWidth;
        mContentRectF.bottom = mSrcRectF.bottom + mBorderWidth;

    }

    //刷新时重新计算并通知重绘
    private void refresh() {
        calculateRadii();
        calculateRectAndPath();
        invalidate();
    }

    //点击事件区域
    private boolean inTouchableArea(float x, float y) {
        if (isCircle) {
            return Math.pow(x - mContentRectF.centerX(), 2) + Math.pow(y - mContentRectF.centerY(), 2) <= Math.pow(mRadius + mBorderWidth, 2);
        } else {
            Path path = new Path();
            path.addRoundRect(mContentRectF, mBorderRadii, Path.Direction.CW);
            Region region = new Region();
            region.setPath(path, new Region((int) mContentRectF.left, (int) mContentRectF.top, (int) mContentRectF.right, (int) mContentRectF.bottom));
            return region.contains((int) x, (int) y);
        }
    }

    /**
     * 设置View是否显示为圆形
     *
     * @param circle true 圆形,false 圆角矩形
     */
    public void setCircle(boolean circle) {
        if (isCircle == circle) {
            return;
        }
        isCircle = circle;
        refresh();
    }

    /**
     * 判断当前View是否是圆形模式
     *
     * @return true 圆形,false 圆角矩形
     */
    public boolean isCircle() {
        return isCircle;
    }

    /**
     * 设置圆角模式
     *
     * @param cornerType 圆角模式 1 等弧度模式,2等宽模式
     */
    public void setCornerType(@CornerType int cornerType) {
        mCornerType = cornerType;
        requestLayout();
    }

    /**
     * 返回当前View的圆角模式
     *
     * @return 1 等弧度模式,2等宽模式
     */
    public int getCornerType() {
        return mCornerType;
    }

    /**
     * 同时设置4个角的圆角半径
     *
     * @param cornerRadius 圆角半径
     */
    public void setCornerRadius(float cornerRadius) {
        if (isCircle) {
            return;
        }
        mCornerRadius = cornerRadius;
        mCornerTopLeftRadius = mCornerTopRightRadius = mCornerBottomRightRadius = mCornerBottomLeftRadius = mCornerRadius;
        refresh();
    }

    /**
     * 获取公共的圆角半径,可能为0
     *
     * @return 圆角半径
     */
    public float getCornerRadius() {
        return mCornerRadius;
    }

    /**
     * 设置左上角圆角半径,圆形模式无效
     *
     * @param cornerTopLeftRadius 左上角圆角半径
     */
    public void setCornerTopLeftRadius(float cornerTopLeftRadius) {
        if (isCircle) {
            return;
        }

        mCornerTopLeftRadius = cornerTopLeftRadius;
        refresh();
    }

    /**
     * 获取设置的左上角圆形半径
     *
     * @return 获取当前设置的左上角圆形半径
     */
    public float getCornerTopLeftRadius() {
        return mCornerTopLeftRadius;
    }

    /**
     * 设置右上角圆形半径 圆形模式无效
     *
     * @param cornerTopRightRadius 右上角圆形半径
     */
    public void setCornerTopRightRadius(float cornerTopRightRadius) {
        if (isCircle) {
            return;
        }
        mCornerTopRightRadius = cornerTopRightRadius;
        refresh();
    }

    /**
     * 获取右上角圆形半径
     *
     * @return 右上角圆形半径
     */
    public float getCornerTopRightRadius() {
        return mCornerTopRightRadius;
    }

    /**
     * 设置 左下角圆角半径 ，圆形模式无效
     *
     * @param cornerBottomLeftRadius 左下角圆角半径
     */
    public void setCornerBottomLeftRadius(float cornerBottomLeftRadius) {
        if (isCircle) {
            return;
        }
        mCornerBottomLeftRadius = cornerBottomLeftRadius;
        refresh();
    }

    /**
     * 获取左下角圆角半径
     *
     * @return 左下角圆角半径
     */
    public float getCornerBottomLeftRadius() {
        return mCornerBottomLeftRadius;
    }

    /**
     * 设置 右下角圆角半径 ，圆形模式无效
     *
     * @param cornerBottomRightRadius 右下角圆角半径
     */
    public void setCornerBottomRightRadius(float cornerBottomRightRadius) {
        if (isCircle) {
            return;
        }

        mCornerBottomRightRadius = cornerBottomRightRadius;
        refresh();
    }

    /**
     * 获取右下角圆角半径
     *
     * @return 右下角圆角半径
     */
    public float getCornerBottomRightRadius() {
        return mCornerBottomRightRadius;
    }

    /**
     * 设置边框宽度
     *
     * @param borderWidth 边框宽度，单位PX
     */
    public void setBorderWidth(float borderWidth) {
        mBorderWidth = borderWidth;
        mHalfBorderWidth = mBorderWidth * 0.5f;
        mBorderPaint.setStrokeWidth(mBorderWidth);
        requestLayout();
    }

    /**
     * 获取当前边框宽度
     *
     * @return 边框宽度, 单位PX
     */
    public float getBorderWidth() {
        return mBorderWidth;
    }

    /**
     * 设置边框颜色
     *
     * @param borderColor 边框颜色
     */
    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    /**
     * 获取当前边框颜色
     *
     * @return 边框颜色
     */
    public int getBorderColor() {
        return mBorderColor;
    }
}
