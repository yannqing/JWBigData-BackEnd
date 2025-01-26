package com.wxjw.jwbigdata.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wxjw.jwbigdata.domain.FileInfo;
import com.wxjw.jwbigdata.vo.FileVo.OnlineFileVo;
import com.wxjw.jwbigdata.vo.FileVo.SaveFileIdVo;
import com.wxjw.jwbigdata.vo.FileVo.TreeVo;
import com.wxjw.jwbigdata.vo.FileVo.fileVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
* @author 67121
* @description 针对表【file_info】的数据库操作Service
* @createDate 2024-06-08 10:36:47
*/
public interface FileInfoService extends IService<FileInfo> {

    void uploadFile(MultipartFile file, String fileName,Integer userId) throws IOException;

    void delFile(String[] fileId, HttpServletRequest request) throws JsonProcessingException;

    void switchFileStatus(Integer status, Integer fileId, HttpServletRequest request) throws JsonProcessingException;

    List<TreeVo> getTree(Integer userId);

    void uploadFileOnline(Integer userId, List<Integer> fileIdArray);

    void exportFile(Integer userId, List<Integer> fileIdArray, HttpServletResponse response) throws IOException;

    List<OnlineFileVo> getOnlineFiles(HttpServletRequest request) throws JsonProcessingException;

    List<List<String>> queryFile(Integer userId, Integer fileId, String[] columnArray, String keyWord);

    JSONArray getFields(Integer userId, List<Integer> fileIdArray);

    List<List<Object>> compareFiles(Integer userId, Integer[] fileIdArray, String[][] fieldArray, List<SaveFileIdVo> saveFieldArray);

    List<List<Object>> compareFilesReverse(Integer userId, Integer[] fileIdArray, String[][] fieldArray, List<SaveFileIdVo> saveFieldArray);

    String[][] openFile(Integer userId, Integer fileId);

    void saveFile(Integer userId, Integer fileId, String[][] content);

    void addFile(Integer userId, String[][] content);

    List<fileVo> getFileList();
}
