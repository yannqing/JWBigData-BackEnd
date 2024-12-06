package com.wxjw.jwbigdata.controller;

import com.alibaba.fastjson.JSONArray;
import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.service.FileInfoService;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.BaseResponse;
import com.wxjw.jwbigdata.vo.FileVo.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    @Resource
    private FileInfoService fileInfoService;

    /**
     * 上传文件
     *
     * @param file
     * @param
     * @param
     * @param userId
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadFile")
    public BaseResponse<Object> uploadFile(@RequestParam("file") MultipartFile file, Integer userId) {
        String fileName = file.getOriginalFilename();
        String fileType = extractFileNameWithExtension(file);
        try{
            fileInfoService.uploadFile(file, fileName, fileType, userId);
        }
        catch(BadSqlGrammarException ex){
            return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
        }
        catch (IOException ex){
            return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
        }
        return ResultUtils.success(Code.SUCCESS, null, "文件上传成功");
    }

    public static String extractFileNameWithExtension(MultipartFile file) {
        if (file == null) {
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return null;
        }
        return originalFilename.substring(originalFilename.lastIndexOf("/") + 1);
    }

    /**
     * 删除库文件（如果删除的是目录级别，则同时删除其子文件） ok
     *
     * @param fileId
     * @return
     */
    @PostMapping("/delFile")
    public BaseResponse<Object> delFile(@RequestBody LinkedHashMap<String, List<String>> fileId) {
        List<String> fileIds = fileId.get("fileId");
        fileInfoService.delFile(fileIds.toArray(new String[fileIds.size()]));
        return ResultUtils.success(Code.SUCCESS, null, "删除数据成功！");
    }

    /**
     * 更改文件显示状态 ok
     *
     * @return
     */
    @PostMapping("/switchFileStatus")
    public BaseResponse<Object> switchFileStatus(@RequestBody Map<String,Object> data) {
        Integer fileId = (Integer)data.get("fileId");
        Boolean status = (Boolean)data.get("status");
        fileInfoService.switchFileStatus(status==true?1:0, fileId);
        return ResultUtils.success(Code.SUCCESS, null, "更改文件显示状态成功");
    }


    /**
     * 获取在线文件 ok
     *
     * @param userId
     * @return
     */
    @GetMapping("/getOnlineFiles")
    public BaseResponse<List<OnlineFileVo>> getOnlineFiles(Integer userId) {
        List<OnlineFileVo> onlineFileVos = fileInfoService.getOnlineFiles(userId);
        return ResultUtils.success(Code.SUCCESS, onlineFileVos, "获取在线文件成功！");
    }

    /**
     * 获取文件列表 ok
     *
     * @param
     * @return
     */
    @GetMapping("/getFileList")
    public BaseResponse<List<fileVo>> getFileList() {
        List<fileVo> onlineFileVos = fileInfoService.getFileList();
        return ResultUtils.success(Code.SUCCESS, onlineFileVos, "获取在线文件成功！");
    }

    /**
     * 导入在线库文件 ok
     *
     * @param
     * @param
     * @return
     */
    @PostMapping("/uploadFileOnline")
    public BaseResponse<Object> uploadFileOnline(@RequestBody fileData data) {

        fileInfoService.uploadFileOnline(data.getUserId(), data.getFileIdArray());
        return ResultUtils.success(Code.SUCCESS, null, "导入在线文件成功！");
    }



    /**
     * 获取文件树 ok
     *
     * @param userId
     * @return
     */
    @GetMapping("/getTree")
    public BaseResponse<List<TreeVo>> getTree(@RequestParam Integer userId) {//@RequestParam Integer userId
        List<TreeVo> tree = fileInfoService.getTree(userId);
        return ResultUtils.success(Code.SUCCESS, tree, "获取文件树成功！");
    }

    /**
     * 获取文件内容 ok
     *
     * @param userId
     * @param fileId
     * @return
     */
    @PostMapping("/openFile")
    public BaseResponse<Object> openFile(Integer userId, Integer fileId) {
        String[][] data = fileInfoService.openFile(userId, fileId);

        return ResultUtils.success(Code.SUCCESS, data, "获取文件内容成功！");
    }

    /**
     * 导出文件 ok
     *
     * @param data
     * @return 返回文件的二进制流
     */
    @PostMapping("/exportFile")
    public void exportFile(@RequestBody fileData data, HttpServletResponse response) {
        try {
            fileInfoService.exportFile(data.getUserId(), data.getFileIdArray(), response);
        }
        catch (IOException e){
            throw new IllegalArgumentException(e);
        }
//        return fileExport;
    }

    /**
     * 保存文件 ok
     * @param saveFileVo
     * @return
     */
    @PostMapping("/saveFile")
    public BaseResponse<Object> saveFile(@RequestBody SaveFileVo saveFileVo) {
        Integer userId = null;
        Integer fileId = null;
        String[][] content = null;
        String fid = saveFileVo.getFileId();
        if(fid == null || fid.isEmpty()){
            // 保存检索/比对结果
            try {
                userId = Integer.parseInt(saveFileVo.getUserId());
                content = saveFileVo.getContent();
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(e);
            }
            fileInfoService.addFile(userId, content);
        }
        else{
            // 修改已有文件
            try {
                userId = Integer.parseInt(saveFileVo.getUserId());
                fileId = Integer.parseInt(fid);
                content = saveFileVo.getContent();
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(e);
            }
            fileInfoService.saveFile(userId, fileId, content);
        }
        return ResultUtils.success(Code.SUCCESS, null, "保存文件成功");
    }

    /**
     * 检索内容 ok
     *
     * @param qdata
     * @return
     */
    @PostMapping("/queryFile")
    public BaseResponse<List<List<String>>> queryFile(@RequestBody queryData qdata) {
        List<List<String>> data = fileInfoService.queryFile(qdata.getUserId(),qdata.getFileId(),qdata.getColumnArray(),qdata.getKeyWord());

        return ResultUtils.success(Code.SUCCESS, data, "检索数据成功！");
    }


    /**
     * 获取比对文件字段 ok
     *
     * @param filedata
     * @return
     */
    @PostMapping("/getFields")
    public BaseResponse<Object> getFields(@RequestBody fileData filedata) {
        JSONArray data = fileInfoService.getFields(filedata.getUserId(), filedata.getFileIdArray());
        return ResultUtils.success(Code.SUCCESS, data, "获取比对文件字段成功！");
    }

    /**
     * 比对多个文件 ok
     *
     * @return
     */
    @PostMapping("/compareFile")
    public BaseResponse<Object> compareFiles(@RequestBody compareQueryData compareQueryData) {
        if(compareQueryData.getCompareType() == 1){
            // 正向比对
            List<List<Object>> data = fileInfoService.compareFiles(compareQueryData.getUserId(), compareQueryData.getFileIdArray(), compareQueryData.getFieldArray(), compareQueryData.getSaveFieldArray());
            return ResultUtils.success(Code.SUCCESS, data, "比对文件成功！");
        }
        else{
            // 反向比对
            List<List<Object>> data = fileInfoService.compareFilesReverse(compareQueryData.getUserId(), compareQueryData.getFileIdArray(), compareQueryData.getFieldArray(), compareQueryData.getSaveFieldArray());
            return ResultUtils.success(Code.SUCCESS, data, "比对文件成功！");
        }

    }


}
