package com.wxjw.jwbigdata.vo.TableVo;

import com.wxjw.jwbigdata.domain.NewColumn;
import com.wxjw.jwbigdata.domain.NewTable;
import lombok.Data;

import java.util.List;

/**
 * @author panhao
 * @version 1.0.0
 * @className TableVo
 * @description TODO
 * @date 2024-06-15 12:18
 **/
@Data
public class TableVo {
    private Integer id;
    private String label;
    private List<ColumnVo> children;

    public TableVo(NewTable newTable, List<ColumnVo> newColumns){
        this.id = newTable.getId();
        this.label = newTable.getComment();
        this.children = newColumns;
    }


}
