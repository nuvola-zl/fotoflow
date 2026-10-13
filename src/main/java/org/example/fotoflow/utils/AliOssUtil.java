package org.example.fotoflow.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public class AliOssUtil {

    private static final String ENDPOINT = "oss-cn-hangzhou.aliyuncs.com";
    private static final String ACCESS_KEY_ID = "your-access-key-id";        // 使用前替换
    private static final String ACCESS_KEY_SECRET = "your-access-key-secret"; // 使用前替换
    private static final String BUCKET_NAME = "your-bucket-name";

    /**
     * 上传图片（原来的，不动）
     */
    public static String upload(MultipartFile file) throws Exception {
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = "idle-goods-avatar/" + UUID.randomUUID() + suffix;

        ossClient.putObject(BUCKET_NAME, fileName, file.getInputStream());
        ossClient.shutdown();

        return "https://" + BUCKET_NAME + "." + ENDPOINT + "/" + fileName;
    }

    //------------------------------------------------------------

    /**
     * 上传视频（我新加的，专门存视频）
     */
    public static String uploadVideo(MultipartFile file) throws Exception {
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 视频单独放在 video 文件夹里
        String fileName = "fotoflow-video/" + UUID.randomUUID() + suffix;

        ossClient.putObject(BUCKET_NAME, fileName, file.getInputStream());
        ossClient.shutdown();

        return "https://" + BUCKET_NAME + "." + ENDPOINT + "/" + fileName;
    }


    //------------------------------------------------------------

    /**
     * 上传音乐（MP3）
     */
    public static String uploadMusic(MultipartFile file) throws Exception {
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 音乐单独放在 music 文件夹
        String fileName = "fotoflow-music/" + UUID.randomUUID() + suffix;

        ossClient.putObject(BUCKET_NAME, fileName, file.getInputStream());
        ossClient.shutdown();

        return "https://" + BUCKET_NAME + "." + ENDPOINT + "/" + fileName;

    }
}