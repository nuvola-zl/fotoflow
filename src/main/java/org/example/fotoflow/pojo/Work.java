package org.example.fotoflow.pojo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Work {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String imgUrl;
    private String category;

    private Integer likeCount;      // 点赞
    private Integer collectCount;   // 收藏
    private Integer commentCount;   // 评论
    private Integer viewCount;      // 浏览

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Integer state;    //审核状态 0：待审核 1：已审核 2：已拒绝

    //@JsonProperty
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> imgList;

    private String rejectReason; //拒绝原因
    private String username;  //此处为用户名，附加
    private String avatar;      //此处为用户头像，附加
    private Boolean isLike;     // 是否点赞  附加
    private Boolean isCollect;  // 是否收藏  附加
}