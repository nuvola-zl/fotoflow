package com.ff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkDto {

    private Long id;    //主键
    private String  title; //标题
    private String  content;  //内容
    //private String  image;    //图片
    // 原来单图：private String image;
    // 现在标准多图 ✅
    private List<String> imgList;
    private String category;  //分类

}
