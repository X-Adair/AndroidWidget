package com.adair.simple;

/**
 * created at 2018/9/28 17:50
 *
 * @author XuShuai
 * @version v1.0
 */
public class DownloadInfo {

    private String path;
    private boolean loading;
    private boolean downloadComplete;
    private int progress;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isDownloadComplete() {
        return downloadComplete;
    }

    public void setDownloadComplete(boolean downloadComplete) {
        this.downloadComplete = downloadComplete;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "path='" + path + '\'' +
                ", loading=" + loading +
                ", downloadComplete=" + downloadComplete +
                ", progress=" + progress +
                '}';
    }
}
