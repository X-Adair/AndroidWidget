package com.adair.simple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adair.widget.layoutManager.GridPagerLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;
    private List<DownloadInfo> mDownloadInfoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        initRecyclerView();
    }


    private void initRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
//        GridPagerLayoutManager manager = new GridPagerLayoutManager(1, 1, GridPagerLayoutManager.HORIZONTAL);
//        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(manager);

        FreeItemDecoration decoration = new FreeItemDecoration();
        decoration.setBoundary(true, true, true, false);
        mRecyclerView.addItemDecoration(decoration);
        mDownloadInfoList = getData();
        mImageAdapter = new ImageAdapter(this, mDownloadInfoList);
        mImageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onDownloadClick(View v, int position) {
                Log.d(TAG, "onDownloadClick: 开始下载");
                download(position);
            }

            @Override
            public void onContentCLick(View v, int position) {
                Toast.makeText(MainActivity.this, "选中选项:" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mImageAdapter);
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
        for (String sPath : path) {
            DownloadInfo info = new DownloadInfo();
            info.setPath(sPath);
            info.setLoading(false);
            info.setDownloadComplete(false);
            info.setProgress(0);
            downloadInfoList.add(info);
        }
        return downloadInfoList;
    }

    private void download(final int position) {
        new Thread(new Runnable() {

            int progress = 0;

            @Override
            public void run() {
                while (progress <= 100) {
                    if (progress != 100) {
                        mDownloadInfoList.get(position).setLoading(true);
                        mDownloadInfoList.get(position).setProgress(progress);
                    } else {
                        mDownloadInfoList.get(position).setDownloadComplete(true);
                        mDownloadInfoList.get(position).setLoading(false);
                        mDownloadInfoList.get(position).setProgress(progress);
                    }
                    progress++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mImageAdapter.notifyItemChanged(position, "progress");
                        }
                    });
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
