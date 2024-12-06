package com.wxjw.jwbigdata.vo.FileVo;

import lombok.Data;

import java.util.List;

/**
 * @author black
 * @version 1.0.0
 * @className compareQueryData
 * @description TODO
 * @date 2024-11-15 12:22
 **/
@Data
public class compareQueryData {
    private Integer userId;
    private Integer[] fileIdArray;
    private String[][] fieldArray;
    private List<SaveFileIdVo> saveFieldArray;
    private Integer compareType;

    public compareQueryData(Integer userId, Integer[] fileIdArray, String[][] fieldArray, List<SaveFileIdVo> saveFieldArray, Integer compareType) {
        this.userId = userId;
        this.fileIdArray = fileIdArray;
        this.fieldArray = fieldArray;
        this.saveFieldArray = saveFieldArray;
        this.compareType = compareType;
    }
}
