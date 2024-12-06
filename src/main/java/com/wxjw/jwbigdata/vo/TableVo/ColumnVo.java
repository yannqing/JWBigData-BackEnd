package com.wxjw.jwbigdata.vo.TableVo;

import com.wxjw.jwbigdata.domain.NewColumn;
import lombok.Data;

/**
 * @author panhao
 * @version 1.0.0
 * @className FieldVo
 * @description TODO
 * @date 2024-10-17 21:39
 **/
@Data
public class ColumnVo {
    private Integer id;
    private String label;
    private String father;

    public ColumnVo(NewColumn newColumn, String father){
        this.id = newColumn.getId();
        this.label = newColumn.getComment();
        this.father = father;
    }
}
