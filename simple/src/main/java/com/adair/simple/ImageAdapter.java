package com.adair.simple;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.adair.widget.CircleProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * created at 2018/9/29 9:19
 *
 * @author XuShuai
 * @version v1.0
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private static final String TAG = "ImageAdapter";

    private List<DownloadInfo> mData;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public ImageAdapter(Context context, List<DownloadInfo> data) {
        mData = data;
        mContext = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: position = " + i);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_adapter_image, viewGroup, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            DownloadInfo info = mData.get(position);

            if (info.getProgress() == 0 || info.getProgress() == 100) {
                onBindViewHolder(holder, position);
            } else {
                holder.mCPBProgress.setProgress(info.getProgress());
            }
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int i) {
        DownloadInfo info = mData.get(i);
        if (!info.isDownloadComplete()) {
            holder.mFlDownload.setVisibility(View.VISIBLE);
            if (info.isLoading()) {
                holder.mIvDownload.setVisibility(View.GONE);
                holder.mCPBProgress.setVisibility(View.VISIBLE);
            } else {
                holder.mIvDownload.setVisibility(View.VISIBLE);
                holder.mCPBProgress.setVisibility(View.GONE);
            }
        } else {
            holder.mFlDownload.setVisibility(View.GONE);
        }
        holder.mCPBProgress.setProgress(info.getProgress());

        RequestOptions options = new RequestOptions().centerCrop().frame(100);
        Glide.with(mContext)
             .asBitmap()
             .apply(options)
             .load(info.getPath())
             .into(holder.mIvContent);
        holder.mIvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onDownloadClick(v, holder.getAdapterPosition());
                }
            }
        });

        holder.mFlDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.mIvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onContentCLick(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onDownloadClick(View v, int position);

        void onContentCLick(View v, int position);
    }


    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView mIvContent;
        ImageView mIvDownload;
        FrameLayout mFlDownload;
        CircleProgressBar mCPBProgress;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvContent = itemView.findViewById(R.id.iv_content);
            mIvDownload = itemView.findViewById(R.id.iv_download);
            mFlDownload = itemView.findViewById(R.id.fl_download);
            mCPBProgress = itemView.findViewById(R.id.cpb_progress);
        }
    }


}
