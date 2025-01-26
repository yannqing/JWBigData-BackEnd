package com.wxjw.jwbigdata.vo.UserVo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author black
 * @version 1.0.0
 * @className DeptVo
 * @description TODO
 * @date 2024-11-12 22:08
 **/
@Data
@AllArgsConstructor
public class DeptVo implements Serializable{

    @JsonProperty(value = "deptId")
    private int deptId;

    @JsonProperty(value = "deptName")
    private String deptName;

    private Boolean status;


    private List<Integer> selectedTables;

    public int getDeptId() {
        return deptId;
    }

    public DeptVo() {
    }

    public String getDeptName() {
        return deptName;
    }

    public Boolean getStatus() {
        return status;
    }
}
