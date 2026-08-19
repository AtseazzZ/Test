package com.gzhu.csnet.kclab.classics100pojo.dto;

import com.gzhu.csnet.kclab.classics100common.constant.StatusConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddBookDTO {

    private String title;
    private String author;
    private String description;
    private Long categoryId;
    private String coverImage;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
