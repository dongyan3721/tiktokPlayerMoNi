package com.example.mytiltok;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytiltok.jzvd.Jzvd;
import com.example.mytiltok.util.BitmapUtil;
import com.example.mytiltok.util.VideoMessageUtil;
import com.example.mytiltok.view.SharePopupWindow;

import java.io.IOError;

public class ActivityTikTok extends AppCompatActivity {

    private RecyclerView rvTiktok;
    private TikTokRecyclerViewAdapter mAdapter;
    private ViewPagerLayoutManager mViewPagerLayoutManager;
    private int mCurrentPosition = -1;
    private RelativeLayout mLayoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//            hideStatusBar();
//        }
        setContentView(R.layout.activity_tiktok);

        //获取根布局
        mLayoutRoot = (RelativeLayout) findViewById(R.id.mainActivity_root);

        rvTiktok = findViewById(R.id.rv_tiktok);

        mAdapter = new TikTokRecyclerViewAdapter(this);
        mViewPagerLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);
        rvTiktok.setLayoutManager(mViewPagerLayoutManager);
        rvTiktok.setAdapter(mAdapter);

        mViewPagerLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                //自动播放第一条
                autoPlayVideo(0);
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                if (mCurrentPosition == position) {
                    Jzvd.releaseAllVideos();
                }
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                if (mCurrentPosition == position) {
                    if (position == MainActivity.videoMessages.size() - 1) {
                        Toast.makeText(ActivityTikTok.this,"视频正在加载中...", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }


                autoPlayVideo(position);
                mCurrentPosition = position;

                if (MainActivity.videoMessages.size() - position == 3) {
//                    MainActivity.videoMessages.addAll(OnlineVideoPlayerMainPage.sendHttpRequestForVideoDetail(MainActivity.context, 5));
                    SimpleThread thread = new SimpleThread();
                    thread.start();
                }
            }
        });

        rvTiktok.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Jzvd jzvd = view.findViewById(R.id.videoplayer);
                if (jzvd != null && Jzvd.CURRENT_JZVD != null && jzvd.jzDataSource != null &&
                        jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.getCurrentUrl())) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.releaseAllVideos();
                    }
                }
            }
        });



    }


    /**
     * 点击进行分享
     */
    public void share(View view){
        // 设置要分享的内容
        String shareContent="分享。。。";
        SharePopupWindow spw = new SharePopupWindow(this, shareContent);
        // 显示窗口
        spw.showAtLocation(mLayoutRoot, Gravity.BOTTOM, 0, 0);
    }

    private void autoPlayVideo(int postion) {
        if (rvTiktok == null || rvTiktok.getChildAt(0) == null) {
            return;
        }
        JzvdStdTikTok player = rvTiktok.getChildAt(0).findViewById(R.id.videoplayer);
        ImageView avatar = rvTiktok.getChildAt(0).findViewById(R.id.iv_avatar);
        TextView alias = rvTiktok.getChildAt(0).findViewById(R.id.tv_alias);

        TextView tv_forward = rvTiktok.getChildAt(0).findViewById(R.id.tv_forward);
//        tv_forward.setClickable(true);
        tv_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(view);
            }
        });


        if (player != null) {
            player.startVideoAfterPreloading();
        }
        if (avatar != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(MainActivity.videoMessages.get(postion).getPicuser());
            if (bitmap != null) {
                bitmap = BitmapUtil.changeShape(bitmap);
                avatar.setImageBitmap(bitmap);
            }
        }

        if (alias != null) {
            alias.setText("@ " + MainActivity.videoMessages.get(postion).getAlias());
        }

    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //全屏并且隐藏状态栏
    private void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    //全屏并且状态栏透明显示
    private void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    public class SimpleThread extends Thread {
        @Override
        public void run() {
            try {
                //Thread.sleep(200);
                MainActivity.videoMessages.addAll(VideoMessageUtil.sendHttpRequestForVideoDetail(MainActivity.context, 5));
            } catch (IOError e) {
                e.printStackTrace();
            }
        }
    }

}
