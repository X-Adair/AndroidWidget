package com.adair.simple;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

/**
 * RecyclerView ItemDecoration
 * <p>
 * created at 2018/9/30 16:00
 *
 * @author XuShuai
 * @version v1.0
 */
public class FreeItemDecoration extends RecyclerView.ItemDecoration {

    private int mDriverWidth = 60;
    private int mDriverHeight = 60;

    private boolean hasLeft;
    private boolean hasRight;
    private boolean hasTop;
    private boolean hasBottom;


    public FreeItemDecoration() {
    }

    public FreeItemDecoration(int driverWidth, int driverHeight) {
        mDriverWidth = driverWidth;
        mDriverHeight = driverHeight;
    }

    public void setBoundary(boolean hasLeft, boolean hasRight, boolean hasTop, boolean hasBottom) {
        this.hasLeft = hasLeft;
        this.hasTop = hasTop;
        this.hasRight = hasRight;
        this.hasBottom = hasBottom;
    }

    //列数
    private int getSpanCount(RecyclerView recyclerView) {
        int spanCount = -1;
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) manager).getSpanCount();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) manager).getSpanCount();
        }
        return spanCount;
    }

    //是否是第一列
    private boolean isFirstColum(RecyclerView parent, int position, int spanCount, int itemCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int orientation;
        if (layoutManager instanceof GridLayoutManager) {
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                return (position + 1) % spanCount == 1;
            } else {
                itemCount = itemCount - itemCount % spanCount;
                return position == itemCount - 1;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                return (position + 1) % spanCount == 1;
            } else {
                itemCount = itemCount - itemCount % spanCount;
                return position == itemCount - 1;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                return position == 0;
            } else {
                return true;
            }
        }
        return false;
    }

    //是否是第一行
    private boolean isFirstRow(RecyclerView parent, int position, int spanCount, int itemCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int orientation;
        if (layoutManager instanceof GridLayoutManager) {
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                return position < spanCount;
            } else {
                return position % spanCount == 1;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                return position < spanCount;
            } else {
                return position % spanCount == 1;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                return true;
            } else {
                return position == 0;
            }
        }
        return false;
    }

    //是否是最后一列
    private boolean isLastColum(RecyclerView parent, int position, int spanCount, int itemCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int orientation;
        if (layoutManager instanceof GridLayoutManager) {
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                return (position + 1) % spanCount == 0;
            } else {
                itemCount = itemCount - itemCount % spanCount;
                return position >= itemCount;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                return (position + 1) % spanCount == 0;
            } else {
                itemCount = itemCount - itemCount % spanCount;
                return position >= itemCount;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                return position == itemCount - 1;
            } else {
                return true;
            }
        }
        return false;
    }

    //是否是最后一行
    private boolean isLastRow(RecyclerView parent, int position, int spanCount, int itemCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int orientation;
        if (layoutManager instanceof GridLayoutManager) {
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                if (itemCount % spanCount == 0) {
                    itemCount = itemCount - spanCount;
                } else {
                    itemCount = itemCount - itemCount % spanCount;
                }
                return position + 1 > itemCount;
            } else {
                return (position + 1) % spanCount == 0;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                itemCount = itemCount - itemCount % spanCount;
                return position + 1 >= itemCount;
            } else {
                return (position + 1) % spanCount == 0;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                return true;
            } else {
                return position == itemCount - 1;
            }
        }
        return false;
    }


    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//        super.onDraw(c, parent, state);
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private Paint mPaint = new Paint();

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin - mDriverWidth;
            final int right = child.getRight() + params.rightMargin
                    + mDriverWidth;
            final int top = child.getTop() - params.topMargin - mDriverWidth;
            final int bottom = child.getBottom() + params.bottomMargin + mDriverHeight;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDriverWidth;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int spanCount = getSpanCount(parent);
        int itemCount = parent.getAdapter().getItemCount();
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();

        outRect.right = mDriverWidth;
        outRect.bottom = mDriverHeight;

        Log.d("getItemOffsets", "getItemOffsets: isFirstColum = " + isFirstColum(parent, position, spanCount, itemCount));
        if (isFirstColum(parent, position, spanCount, itemCount)) {
            if (hasLeft) {
                outRect.left = mDriverWidth;
            } else {
                outRect.left = 0;
            }
        }

        Log.d("getItemOffsets", "getItemOffsets: isLastColum = " + isLastColum(parent, position, spanCount, itemCount));
        if (isLastColum(parent, position, spanCount, itemCount)) {
            if (hasRight) {
                outRect.right = mDriverWidth;
            } else {
                outRect.right = 0;
            }
        }

        Log.d("getItemOffsets", "getItemOffsets: isFirstRow = " + isFirstRow(parent, position, spanCount, itemCount));
        if (isFirstRow(parent, position, spanCount, itemCount)) {
            if (hasTop) {
                outRect.top = mDriverHeight;
            } else {
                outRect.top = 0;
            }
        }

        Log.d("getItemOffsets", "getItemOffsets: isLastRow = " + isLastRow(parent, position, spanCount, itemCount));
        if (isLastRow(parent, position, spanCount, itemCount)) {
            if (hasBottom) {
                outRect.bottom = mDriverHeight;
            } else {
                outRect.bottom = 0;
            }
        }
        Log.d("getItemOffsets", "getItemOffsets: position =  " + position + "  " + outRect.toString());
    }
}
