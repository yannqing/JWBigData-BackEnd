package com.wxjw.jwbigdata.vo.FileVo;

import lombok.Data;

/**
 * @author black
 * @version 1.0.0
 * @className queryData
 * @description TODO
 * @date 2024-11-15 12:21
 **/
@Data
public class queryData {
    private int userId;
    private int fileId;
    private String[] columnArray;
    private String keyWord;

    public queryData(int userId, int fileId, String[] columnArray, String keyWord) {
        this.userId = userId;
        this.fileId = fileId;
        this.columnArray = columnArray;
        this.keyWord = keyWord;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String[] getColumnArray() {
        return columnArray;
    }

    public void setColumnArray(String[] columnArray) {
        this.columnArray = columnArray;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
}
