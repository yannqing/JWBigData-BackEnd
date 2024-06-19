package com.wxjw.jwbigdata.vo.TableVo;

import com.wxjw.jwbigdata.domain.JwField;
import com.wxjw.jwbigdata.domain.JwTable;
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
    private Integer tableId;
    private String tableName;
    private String description;
    private List<JwField> children;

    public TableVo(JwTable jwTable,List<JwField> jwFields){
        this.tableId = jwTable.getTableId();
        this.tableName = jwTable.getTableName();
        this.description = jwTable.getDescription();
        this.children = jwFields;
    }


}
