package com.example.mytiltok.util;


import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class HttpsManager {
    // 这是接口，访问即可获得条视频和预览图、头像、作者名称的信息
    private static final String resUrl = "https://api.apiopen.top/api/getMiniVideo";
    private static final OkHttpClient client = new OkHttpClient();

    // 获取视频信息
    public static String getVideoMessage(Callback callback) {
        Request request = new Request.Builder().url(resUrl).build();
        try {
            //接收到回复Response
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void download(int index, String url, String outputPath, final OnDownloadListener listener) {

        Request request = new Request.Builder().url(url).build();
        try {
            //接收到回复Response
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                // 处理下载失败的情况
                if (listener != null) {
                    listener.onDownloadFailed("未知错误：" + response, index);
                }
                return;
            }

            // 保存文件
            File outputFile = new File(outputPath);
            BufferedSink sink = Okio.buffer(Okio.sink(outputFile));
            BufferedSource source = Objects.requireNonNull(response.body()).source();
            sink.writeAll(source);
            sink.close();
            source.close();
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public interface OnDownloadListener {
        void onDownloadFailed(String errorMessage, int index);
    }


}