package com.wxjw.jwbigdata.vo.AuthVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptVo {
    private Integer deptId;
    private String deptName;
    private Boolean status;
    private List<Integer> selectedTables;
}
