package com.wxjw.jwbigdata.vo.FileVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author black
 * @version 1.0.0
 * @className fileVo
 * @description TODO
 * @date 2024-11-22 21:52
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class fileVo {
    private Integer fileId;
    private String fileName;
    private String fileType;
    private String uploadUser;
    private String uploadTime;
    private Boolean status;
}
