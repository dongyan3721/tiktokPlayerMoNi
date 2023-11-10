package com.example.mytiltok;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.mytiltok.jzvd.JZDataSource;
import com.example.mytiltok.jzvd.Jzvd;

public class TikTokRecyclerViewAdapter extends RecyclerView.Adapter<TikTokRecyclerViewAdapter.MyViewHolder> {

    public static final String TAG = "AdapterTikTokRecyclerView";
    private int video_num = 1;
    private Context context;

    public TikTokRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_tiktok, parent, false));
        return holder;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //Log.i(TAG, "onBindViewHolder [" + holder.jzvdStd.hashCode() + "] position=" + position);
        //String video_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/tiktok00"+String.valueOf(position+1)+".mp4";
        JZDataSource jzDataSource = new JZDataSource(MainActivity.videoMessages.get(position).getPlayurl().toString());
        jzDataSource.looping = true;
        holder.jzvdStd.setUp(jzDataSource, Jzvd.SCREEN_NORMAL);
        Glide.with(holder.jzvdStd.getContext()).load("").into(holder.jzvdStd.posterImageView);
    }

    @Override
    public int getItemCount() {
        video_num = MainActivity.videoMessages.size();
        return video_num;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        JzvdStdTikTok jzvdStd;

        public MyViewHolder(View itemView) {
            super(itemView);
            jzvdStd = itemView.findViewById(R.id.videoplayer);
        }
    }

    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap b = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            b=retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();

        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return b;
    }
}
