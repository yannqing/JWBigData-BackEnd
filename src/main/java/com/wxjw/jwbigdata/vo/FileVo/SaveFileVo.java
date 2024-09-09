package com.wxjw.jwbigdata.vo.FileVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 保存文件
 * @author: yannqing
 * @create: 2024-09-09 10:06
 * @from: <更多资料：yannqing.com>
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveFileVo {
    private String userId;
    private String fileId;
    private String[][] content;
}
