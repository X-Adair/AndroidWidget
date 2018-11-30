package com.adair.simple.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.adair.simple.DownloadInfo;
import com.adair.simple.ImageAdapter;
import com.adair.simple.R;
import com.adair.widget.CircleProgressBar;
import com.adair.widget.layoutManager.GridPagerLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * created at 2018/11/15 17:49
 *
 * @author XuShuai
 * @version v1.0
 */
public class GridPagerActivity extends AppCompatActivity {
    private static final String TAG = "GridPagerLayoutManager";
    private RecyclerView mRecyclerView;
    private List<DownloadInfo> mInfos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_pager);
        mRecyclerView = findViewById(R.id.recycler);

        GridPagerLayoutManager manager = new GridPagerLayoutManager(3, 3, GridPagerLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
//        LinearSnapHelper helper = new LinearSnapHelper();
//        helper.attachToRecyclerView(mRecyclerView);
        mInfos = getData();
        final GridPagerAdapter imageAdapter = new GridPagerAdapter(mInfos, this);
        imageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onDownloadClick(View v, final int position) {
                final DownloadInfo info = mInfos.get(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (info.getProgress() < 100) {
                            info.setProgress(info.getProgress() + 2);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageAdapter.notifyItemChanged(position, "1111111");
                                }
                            });

                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        info.setLoading(false);
                        info.setDownloadComplete(true);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageAdapter.notifyItemChanged(position);
                            }
                        });

                    }
                }).start();
            }

            @Override
            public void onContentCLick(View v, int position) {

            }
        });
        mRecyclerView.setAdapter(imageAdapter);

    }

    private List<DownloadInfo> getData() {
        List<DownloadInfo> downloadInfoList = new ArrayList<>();
        String[] path = new String[]{
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1537511272503&di=7f150864a2e76bb6d9f03a2736c92b42&imgtype=0&src=http%3A%2F%2Fg.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2F9d82d158ccbf6c81ee692f8dba3eb13533fa407c.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1538106105&di=8176cffefbd200b3c1c851d424617478&imgtype=jpg&er=1&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201608%2F02%2F20160802143416_iYArx.jpeg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1538106122&di=fe2f754f4c7697b5aac88cb325786fd7&imgtype=jpg&er=1&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201309%2F15%2F20130915165257_dJVCx.jpeg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1538106135&di=0b9764b1bdaac3cdbcdac58f3f3419a0&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.sy.kuaiyong.com%2Fnews%2Fgl%2F2015-09-22%2Ff26e65e3e451e8330b430d693a69db0c.jpg",
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=904992785,2377307323&fm=26&gp=0.jpg",
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3733063151,1863968573&fm=26&gp=0.jpg",
                "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3990942365,4150051484&fm=26&gp=0.jpg",
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2196040632,1368519294&fm=26&gp=0.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1537511521742&di=1d204bd3ce648a330c90281278b16585&imgtype=0&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201405%2F19%2F20140519170006_aydZz.jpeg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1538106256&di=0c29a889eef4b1d346c4c778a1d03ad8&imgtype=jpg&er=1&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201309%2F08%2F20130908161837_FBysx.thumb.700_0.png"};

        for (int i = 0; i < 100; i++) {
            String s = path[i % 10];
            DownloadInfo info = new DownloadInfo();
            info.setPath(s);
            info.setLoading(false);
            info.setDownloadComplete(false);
            downloadInfoList.add(info);
        }

        return downloadInfoList;
    }


    private static class GridPagerAdapter extends RecyclerView.Adapter<GridPagerAdapter.GridVH> {

        private List<DownloadInfo> mData;
        private Context mContext;
        private ImageAdapter.OnItemClickListener mOnItemClickListener;

        public GridPagerAdapter(List<DownloadInfo> data, Context context) {
            mData = data;
            mContext = context;
        }

        @NonNull
        @Override
        public GridVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_adapter_image, viewGroup, false);
            return new GridVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GridVH holder, int position, @NonNull List<Object> payloads) {
            final DownloadInfo info = mData.get(position);
            if (payloads.isEmpty()) {
                super.onBindViewHolder(holder, position, payloads);
            } else {
                Log.d(TAG, "onBindViewHolder: " + info.getProgress());
                holder.mCPBProgress.setProgress(info.getProgress());
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final GridVH holder, final int i) {
            if (i % 4 == 0) {
                holder.itemView.setBackgroundResource(R.color.color_white);
            } else if (i % 4 == 1) {
                holder.itemView.setBackgroundResource(R.color.colorPrimary);
            } else if (i % 3 == 2) {
                holder.itemView.setBackgroundResource(R.color.color_FF7F24);
            } else {
                holder.itemView.setBackgroundResource(R.color.colorAccent);
            }


            final DownloadInfo info = mData.get(i);
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
                    info.setLoading(true);
                    holder.mIvDownload.setVisibility(View.GONE);
                    holder.mCPBProgress.setVisibility(View.VISIBLE);
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

        public void setOnItemClickListener(ImageAdapter.OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        static class GridVH extends RecyclerView.ViewHolder {

            ImageView mIvContent;
            ImageView mIvDownload;
            FrameLayout mFlDownload;
            CircleProgressBar mCPBProgress;

            public GridVH(@NonNull View itemView) {
                super(itemView);
                mIvContent = itemView.findViewById(R.id.iv_content);
                mIvDownload = itemView.findViewById(R.id.iv_download);
                mFlDownload = itemView.findViewById(R.id.fl_download);
                mCPBProgress = itemView.findViewById(R.id.cpb_progress);
            }
        }
    }
}
