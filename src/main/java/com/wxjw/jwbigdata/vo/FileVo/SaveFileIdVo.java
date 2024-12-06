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
public class SaveFileIdVo {
    private String id;
    private String label;
    private Integer pIndex;
    private String pId;

    public SaveFileIdVo(String id, String label, Integer pIndex, String pId) {
        this.id = id;
        this.label = label;
        this.pIndex = pIndex;
        this.pId = pId;
    }
}
