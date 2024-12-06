package com.wxjw.jwbigdata.vo.AuthVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptVo {
    private Integer deptId;
    private String deptName;
    private Boolean status;
}
