package com.wxjw.jwbigdata.vo.FileVo;

import java.util.List;

/**
 * @author black
 * @version 1.0.0
 * @className fileData
 * @description TODO
 * @date 2024-11-22 20:04
 **/
public class fileData {

        private int userId;
        private List<Integer> fileIdArray;

        public fileData(int userId, List<Integer> fileIdArray) {
            this.userId = userId;
            this.fileIdArray = fileIdArray;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public List<Integer> getFileIdArray() {
            return fileIdArray;
        }

        public void setFileIdArray(List<Integer> fileIdArray) {
            this.fileIdArray = fileIdArray;
        }

}
