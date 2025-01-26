package com.wxjw.jwbigdata.vo.TableVo;

import com.wxjw.jwbigdata.controller.TableController;
import com.wxjw.jwbigdata.domain.NewTable;
import lombok.Data;

/**
 * @author black
 * @version 1.0.0
 * @className AuthTable
 * @description TODO
 * @date 2024-12-23 22:52
 **/
@Data
public class AuthTable {
    private Integer tabId;
    private String tabName;

    public AuthTable(Integer tabId, String tabName) {
        this.tabId = tabId;
        this.tabName = tabName;
    }

    public AuthTable(NewTable newTable){
        this.tabId = newTable.getId();
        this.tabName = newTable.getComment();
    }
}
