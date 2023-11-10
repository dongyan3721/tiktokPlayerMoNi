package com.example.mytiltok;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mytiltok.assets.ProjectSubPath;
import com.example.mytiltok.domain.VideoMessage;
import com.example.mytiltok.util.DeleteFileUtil;
import com.example.mytiltok.util.VideoMessageUtil;

import java.io.File;
import java.io.IOError;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static List<VideoMessage> videoMessages = new ArrayList<>();
    public  static Context context;
    private ProgressDialog mDialog;

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;

    // 用来终止转圈进程的线程
    private final Runnable mCloseDialog = new Runnable() {
        @Override
        public void run() {
            if (mDialog.isShowing()) { // 对话框仍在显示
                mDialog.dismiss(); // 关闭对话框
            }
        }
    };

    // 接受特征值，用来终止转圈进程
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        // 在收到消息时触发
        @Override
        public void handleMessage(Message msg) {
            // 定义的终止加载动作符
            if (msg.what == 1) {
                post(mCloseDialog);
            }

            if (videoMessages == null || videoMessages.isEmpty()) {
                Toast.makeText(MainActivity.this, "未检索到视频文件，请检查", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(MainActivity.this, ActivityTikTok.class));
            }
        }
    };

    @Override
    public void onClick(View v) {
//        if (videoMessages == null || videoMessages.isEmpty()) {
//            Toast.makeText(MainActivity.this, "未检索到视频文件，请检查", Toast.LENGTH_SHORT).show();
//        } else {
//            startActivity(new Intent(MainActivity.this, ActivityTikTok.class));
//        }
    }

    public class SimpleThread extends Thread {
        @Override
        public void run() {
            try {
                //Thread.sleep(200);
                MainActivity.videoMessages.addAll(VideoMessageUtil.sendHttpRequestForVideoDetail(MainActivity.this, 5));
                // 抓取图片、视频、文字的线程结束，向handler发送信息关闭转圈
                mHandler.sendEmptyMessage(1);
            } catch (IOError e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    private void startLoading(){
        mDialog = ProgressDialog.show(this, "请稍候", "正在拼命加载中O.o");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        boolean isAllGranted = checkPermissionAllGranted(
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET
                }
        );

        if (!isAllGranted) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET
                    },
                    MY_PERMISSION_REQUEST_CODE
            );
        } else {
            deleteResources();
            SimpleThread thread = new SimpleThread();
            startLoading();
            thread.start();
        }

//        Button start = (Button) findViewById(R.id.btn_main);
//        start.setOnClickListener(this);

//        start.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                startActivity(new Intent(MainActivity.this, LocalVideoActivity.class));
//                return true;
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != -1) {
//                    T.showShort(mContext,"权限设置成功");
                    new SimpleThread().start();
                } else {
                    //T.showShort(mContext,"拒绝权限");
                    // 权限被拒绝，弹出dialog 提示去开启权限
                    break;
                }
            }
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //Toast.makeText(this, "你确定要退出吗", Toast.LENGTH_SHORT).show();
            if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {

                deleteResources();

                finish();
                System.exit(0);
            }
            return true;// true 事件不继续传递， false 事件继续传递
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void deleteResources() {
        String baseDir = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
        String avatarBaseAddress = baseDir + ProjectSubPath.AVATAR;
        String previewBaseAddress = baseDir + ProjectSubPath.PREVIEW;
        String videoBaseAddress = baseDir + ProjectSubPath.VIDEO;
        DeleteFileUtil.deleteFile(new File(avatarBaseAddress));
        DeleteFileUtil.deleteFile(new File(previewBaseAddress));
        DeleteFileUtil.deleteFile(new File(videoBaseAddress));
    }

}