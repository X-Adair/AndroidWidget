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

    public static final int VERTICAL = 0;           // 垂直滚动
    public static final int HORIZONTAL = 1;         // 水平滚动

    @IntDef({VERTICAL, HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationType {
    }            // 滚动类型

    @OrientationType
    private int mOrientation;                       // 默认水平滚动


    private Context mContext;

    //滚动工具类
    private OrientationHelper mHelper;

    private SparseArray<Rect> mItemFrams;//item显示区域的矩形记录
    private int mRows;//行数
    private int mColumns;//列数
    private int mOnePageSize;//每页显示数量

    private int mItemWidth;//每一条item宽度
    private int mItemHeight;//每一个item的高度

    private int mMaxScrollX;//x轴最大可滑动偏移量
    private int mMaxScrollY;//y轴最大可滑动偏移量

    private int offsetX;//x轴偏移量
    private int offsetY;//y轴偏移量


    private int mPengdingScrollPosition;

    public GridPagerLayoutManager(@IntRange(from = 1, to = 100) int rows,
                                  @IntRange(from = 1, to = 100) int columns,
                                  @OrientationType int orientation) {
        mItemFrams = new SparseArray<>();
        mRows = rows;
        mColumns = columns;
        mOnePageSize = mRows * mColumns;
        mOrientation = orientation;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //没有数据直接移除所有View并返回
        if (getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        //state.isPreLayout() 是判断之前布局时动画有没有处理结束，没有结束返回
        if (state.isPreLayout()) {
            return;
        }
        if (mHelper == null) {
            createHelper();
        }
        detachAndScrapAttachedViews(recycler);

        //计算总的页数
        int totalPage = getItemCount() / mOnePageSize;
        if (getItemCount() % mOnePageSize > 0) {
            totalPage++;
        }
        //计算偏移量及限制最大滑动距离
        if (canScrollHorizontally()) {
            mMaxScrollX = (totalPage - 1) * mHelper.getTotalSpace();
            mMaxScrollY = 0;
            offsetY = 0;
            if (offsetX > mMaxScrollX) {
                offsetX = mMaxScrollX;
            }
        } else {
            mMaxScrollX = 0;
            offsetX = 0;
            mMaxScrollY = (totalPage - 1) * mHelper.getTotalSpace();
            if (offsetY > mMaxScrollY) {
                offsetY = mMaxScrollY;
            }
        }

        if (mItemWidth <= 0) {
            mItemWidth = getAvailableWidth() / mColumns;
        }

        if (mItemHeight <= 0) {
            mItemHeight = getAvailableHeight() / mRows;
        }

        //先添加显示2页的数据量
        for (int i = 0; i < mOnePageSize * 2; i++) {
            getItemFrameByPosition(i);
        }

        if (offsetX == 0 && offsetY == 0) {
            for (int i = 0; i < mOnePageSize; i++) {
                if (i >= getItemCount()) break;
                View child = recycler.getViewForPosition(i);
                addView(child);
                measureChildWithMargins(child, 0, 0);
            }
        }
        fillItem(recycler, state);
    }

    private void fillItem(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            return;
        }

        Rect displayRect = new Rect(offsetX - mItemWidth, offsetY - mItemHeight, getAvailableWidth() + offsetX + mItemWidth, getAvailableHeight() + offsetY + mItemHeight);
        //取交集,限制
        displayRect.intersect(0, 0, mMaxScrollX + getAvailableWidth(), mMaxScrollY + getAvailableHeight());
        int startPos = 0;

        int pageIndex = getPageIndexByOffset();


    }

    private int getPageIndexByOffset() {
        return 0;
    }


    //获取某一个Item在界面的所在位置矩形
    private Rect getItemFrameByPosition(int position) {
        Rect rect = mItemFrams.get(position);
        if (null == rect) {
            rect = new Rect();

            int page = position / mOnePageSize;     //计算当前item在第几页
            int pagePos = position % mOnePageSize;  //在所处页的第几项
            int row = pagePos / mColumns;           // 获取所在行
            int col = pagePos - (row * mColumns);   // 获取所在列

            int offsetX = 0;//左上角偏移量
            int offsetY = 0;//top偏移量

            if (canScrollHorizontally()) {
                offsetX += mHelper.getTotalSpace() * page;
            } else {
                offsetY += mHelper.getTotalSpace() * page;
            }

            offsetX += (col * mItemWidth);
            offsetY += (row * mItemHeight);

            rect.set(offsetX, offsetY, offsetX + mItemWidth, offsetY + mItemHeight);
            mItemFrams.put(position, rect);
        }
        return rect;
    }

    //获取可用的RecyclerView宽度
    private int getAvailableWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    //获取可用的RecyclerView高度
    private int getAvailableHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    /**
     * 允许横向滑动
     *
     * @return
     */
    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    /**
     * 禁止垂直滑动
     *
     * @return
     */
    @Override
    public boolean canScrollVertically() {
        return false;
    }


    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        offsetChildrenHorizontal(-dx);
        offsetX -= dx;
        return -dx;
    }

    private void createHelper() {
        if (mOrientation == HORIZONTAL) {
            mHelper = OrientationHelper.createHorizontalHelper(this);
        } else {
            mHelper = OrientationHelper.createVerticalHelper(this);
        }
    }

}
