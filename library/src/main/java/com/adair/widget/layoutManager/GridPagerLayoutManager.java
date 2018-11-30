package com.adair.widget.layoutManager;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.logging.Logger;

/**
 * 网格布局,页数翻页
 * <p>
 * created at 2018/9/29 10:42
 *
 * @author XuShuai
 * @version v1.0
 */
public class GridPagerLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "GridPagerLayoutManager";

    public static final int HORIZONTAL = 1;         // 水平滚动
    public static final int VERTICAL = 0;           // 垂直滚动

    @IntDef({VERTICAL, HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationType {  // 滚动类型
    }

    private Context mContext;

    private int mRows;//有多少行
    private int mCols;//有多少列
    @OrientationType
    private int mOrientation;                       // 默认水平滚动

    private int mPageSize;//每页显示item数量
    private int mPageNum;//总页数

    private int mItemWidth = 0;
    private int mItemHeight = 0;

    private int mMaxOffsetX;//最大X滚动距离
    private int mMaxOffsetY;//最大Y滚动距离

    private int mOffsetX = 0;//当前X滑动距离
    private int mOffsetY = 0;//当前Y滑动距离


    public GridPagerLayoutManager(@IntRange(from = 1, to = 100) int rows,
                                  @IntRange(from = 1, to = 100) int columns,
                                  @OrientationType int orientation) {
        mRows = rows;
        mCols = columns;
        mOrientation = orientation;
        mPageSize = rows * columns;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == VERTICAL;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0 || state.isPreLayout()) {//没有item，直接返回
            removeAndRecycleAllViews(recycler);
            return;
        }

        detachAndScrapAttachedViews(recycler);
        //获取总页数
        mPageNum = getPageNum();
        mMaxOffsetX = (mPageNum - 1) * getWidth();
        mMaxOffsetY = (mPageNum - 1) * getHeight();
        //获取每个item的宽度
        mItemWidth = getUsableWidth() / mCols;
        //获取每个item的高度
        mItemHeight = getUsableHeight() / mRows;
        addViews(recycler, state);
    }


    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        if (mOffsetX + dx > mMaxOffsetX) {
            dx = mMaxOffsetX - mOffsetX;
        }
        if (mOffsetX + dx < 0) {
            dx = 0 - mOffsetX;
        }
        mOffsetX += dx;
        offsetChildrenHorizontal(-dx);
        addViews(recycler, state);
        return dx;
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        if (mOffsetY + dy > mMaxOffsetY) {
            dy = mMaxOffsetY - mOffsetY;
        }
        if (mOffsetY + dy < 0) {
            dy = 0 - mOffsetY;
        }
        mOffsetY += dy;
        offsetChildrenVertical(-dy);
        addViews(recycler, state);
        return dy;
    }

    //================================私有方法=======================================================
    //获取总页数
    private int getPageNum() {
        int itemCount = getItemCount();
        int pageNum;
        if (itemCount % mPageSize == 0) {
            pageNum = getItemCount() / mPageSize;
        } else {
            pageNum = getItemCount() / mPageSize + 1;
        }
        return pageNum;
    }

    private void addViews(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            return;
        }
        //显示当前页和下一页的View
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (canScrollHorizontally()) {
                int left = getDecoratedLeft(childView);
                int right = getDecoratedRight(childView);
                if (right < mOffsetX || left > mOffsetX + getWidth()) {
                    removeAndRecycleView(childView, recycler);
                }
            } else if (canScrollVertically()) {
                int top = getDecoratedTop(childView);
                int bottom = getDecoratedBottom(childView);
                if (bottom < mOffsetY || top > mOffsetY + getHeight()) {
                    removeAndRecycleView(childView, recycler);
                }
            }
        }

        int pageIndex;
        int startViewIndex = 0;
        int endViewIndex = 0;
        if (canScrollHorizontally()) {
            pageIndex = mOffsetX / getWidth();
            startViewIndex = pageIndex * mPageSize;
            endViewIndex = (pageIndex + 2) * mPageSize;
            if (endViewIndex > getItemCount()) {
                endViewIndex = getItemCount();
            }
        } else if (canScrollVertically()) {
            pageIndex = mOffsetY / getHeight();
            startViewIndex = pageIndex * mPageSize;
            endViewIndex = (pageIndex + 2) * mPageSize;
            if (endViewIndex > getItemCount()) {
                endViewIndex = getItemCount();
            }
        }
        for (int i = startViewIndex; i < endViewIndex; i++) {
            int page = i / mPageSize;
            int pagePos = i % mPageSize;
            int cols = pagePos % mCols;
            int rows = pagePos / mCols;
            int left, right, top, bottom;
            if (canScrollHorizontally()) {
                left = getWidth() * page + getPaddingLeft() + cols * mItemWidth;
                right = left + mItemWidth;
                top = getPaddingTop() + rows * mItemHeight;
                bottom = top + mItemHeight;
                View childView = recycler.getViewForPosition(i);
                addView(childView);
                measureChildWithMargins(childView, getUsableWidth() - mItemWidth, getUsableHeight() - mItemHeight);
                layoutDecoratedWithMargins(childView, left - mOffsetX, top, right - mOffsetX, bottom);
            } else {
                left = getPaddingLeft() + cols * mItemWidth;
                right = left + mItemWidth;
                top = getHeight() * page + getPaddingTop() + rows * mItemHeight;
                bottom = top + mItemHeight;
                View childView = recycler.getViewForPosition(i);
                addView(childView);
                measureChildWithMargins(childView, getUsableWidth() - mItemWidth, getUsableHeight() - mItemHeight);
                layoutDecoratedWithMargins(childView, left, top - mOffsetY, right, bottom - mOffsetY);
            }
        }
    }


    //================================通用方法=======================================================

    /**
     * 获取可用的宽度
     *
     * @return 宽度 - padding
     */
    private int getUsableWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * 获取可用的高度
     *
     * @return 高度 - padding
     */
    private int getUsableHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }


    /**
     * 获取某个childView在水平方向所占的空间
     *
     * @param view
     * @return
     */
    private int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

    /**
     * 获取某个childView在竖直方向所占的空间
     *
     * @param view
     * @return
     */
    private int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }
}
