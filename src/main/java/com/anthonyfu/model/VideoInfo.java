package com.anthonyfu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 回傳給前端的影片完整資訊（掃描檔案 + metadata.json 合併後的結果）*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoInfo {
    private String filename;
    private String title;
    private String description;
    private String category;
    private List<String> tags;
    private String releaseDate;
    private String videoUrl;
    private long sizeBytes;
}
