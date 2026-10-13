package org.example.fotoflow.pojo;

import lombok.Data;

@Data

public class WorkImage {
    private Long id;
    private Long workId;
    private String imgUrl;
    private Integer sort;
}
