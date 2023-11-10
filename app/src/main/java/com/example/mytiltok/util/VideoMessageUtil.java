package com.example.mytiltok.util;

import android.content.Context;
import android.os.Environment;

import com.example.mytiltok.assets.ProjectSubPath;
import com.example.mytiltok.domain.RespData;
import com.example.mytiltok.domain.VideoMessage;
import com.example.mytiltok.util.DateUtil;
import com.example.mytiltok.util.HttpsManager;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoMessageUtil {

    public static List<VideoMessage> sendHttpRequestForVideoDetail(Context context, int count) {
        List<VideoMessage> message = new ArrayList<>();
        String videoMessage = HttpsManager.getVideoMessage(null);
        ArrayList<Integer> recordAddressToModify = new ArrayList<>();
        RespData respData = new Gson().fromJson(videoMessage, RespData.class);

        List<VideoMessage> videoMessageList = respData.getResult().getList();

        HttpsManager.OnDownloadListener listener = (errorMsg, index) -> {
            recordAddressToModify.add(index);
        };
        for (int i = 0; i < count; i++) {

            VideoMessage v = new VideoMessage();
            v.setAlias(videoMessageList.get(i).getAlias());
            v.setId(videoMessageList.get(i).getId());
            v.setTitle(videoMessageList.get(i).getTitle());
            // 设置头像、封面图、视频的下载地址

            String baseDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";

            String avatarBaseAddress = baseDir + ProjectSubPath.AVATAR;
            if (!new File(avatarBaseAddress).exists()) {
                new File(avatarBaseAddress).mkdirs();
            }
            String avatarAddress = avatarBaseAddress + DateUtil.getNowDateTimeFull() + ".jpg";

            String previewBaseAddress = baseDir + ProjectSubPath.PREVIEW;
            if (!new File(previewBaseAddress).exists()) {
                new File(previewBaseAddress).mkdirs();
            }
            String previewAddress = previewBaseAddress + DateUtil.getNowDateTimeFull() + ".jpg";

            String videoBaseAddress = baseDir + ProjectSubPath.VIDEO;
            if (!new File(videoBaseAddress).exists()) {
                new File(videoBaseAddress).mkdirs();
            }
            String videoAddress = videoBaseAddress + DateUtil.getNowDateTimeFull() + ".mp4";
            // 开始下载
            HttpsManager.download(i, videoMessageList.get(i).getPicuser(), avatarAddress, listener);
            HttpsManager.download(i, videoMessageList.get(i).getPicurl(), previewAddress, listener);
            HttpsManager.download(i, videoMessageList.get(i).getPlayurl(), videoAddress, listener);

            v.setPlayurl(videoAddress); // 拿到视频的下载路径
            v.setPicuser(avatarAddress); // 拿到头像的下载路径
            v.setPicurl(previewAddress); // 拿到视频预览图的下载路径
            message.add(v);
        }

        System.out.println("OnlineVideoPlayerMainPage.sendHttpRequestForVideoDetail");
        return message;
    }

}