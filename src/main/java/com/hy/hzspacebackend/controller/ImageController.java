package com.hy.hzspacebackend.controller;

import com.alibaba.fastjson.JSON;
import com.hy.hzspacebackend.cache.LocalCache;
import com.hy.hzspacebackend.utils.Base64Util;
import com.hy.hzspacebackend.utils.FileUtil;
import com.hy.hzspacebackend.utils.HttpUtil;
import okhttp3.*;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 黄勇
 * @since 2023/5/31
 */
@RestController
@CrossOrigin
public class ImageController {

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    @Value("${api.client_id}")
    private String clientId;
    @Value("${api.client_secret}")
    private String clientSecret;

    public String getAccessToken() throws IOException {
        if (LocalCache.get("api_access_token") == null) {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/oauth/2.0/token?client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=client_credentials")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            Map<String, Object> map = JSON.parseObject(response.body().string());
            LocalCache.put("api_access_token", map.get("access_token").toString(), 30, TimeUnit.DAYS);
            return map.get("access_token").toString();
        }
        return (String) LocalCache.get("api_access_token");
    }

    @PostMapping("/api/ise")
    public ResponseEntity<?> imageSharpnessEnhancement(@org.springframework.web.bind.annotation.RequestBody Map<Object,Object> map) {
        String localImageUrl = (String) map.get("url");
        String url = "https://aip.baidubce.com/rest/2.0/image-process/v1/image_definition_enhance";
        return getNewImage(localImageUrl, url);
    }

    @PostMapping("/api/ice")
    public ResponseEntity<?> imageContrastEnhancement(@org.springframework.web.bind.annotation.RequestBody Map<Object,Object> map) {
        String localImageUrl = (String) map.get("url");
        String url = "https://aip.baidubce.com/rest/2.0/image-process/v1/contrast_enhance";
        return getNewImage(localImageUrl, url);
    }

    @PostMapping("/api/im")
    public ResponseEntity<?> imageMagnify(@org.springframework.web.bind.annotation.RequestBody Map<Object,Object> map) {
        String localImageUrl = (String) map.get("url");
        String url = "https://aip.baidubce.com/rest/2.0/image-process/v1/image_quality_enhance";
        return getNewImage(localImageUrl, url);
    }

    public ResponseEntity<?> getNewImage(String localImageUrl, String url) {
        try {
            // 本地文件路径
            byte[] imgData = FileUtil.readFileByBytes(localImageUrl);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = getAccessToken();

            String result = HttpUtil.post(url, accessToken, param);
            new File(localImageUrl).delete();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

}
