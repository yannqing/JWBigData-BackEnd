package com.wxjw.jwbigdata.controller;

import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.service.FileInfoService;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.BaseResponse;
import com.wxjw.jwbigdata.vo.FileVo.OnlineFileVo;
import com.wxjw.jwbigdata.vo.FileVo.SaveFileVo;
import com.wxjw.jwbigdata.vo.FileVo.TreeVo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
     * @param fileName
     * @param fileType
     * @param userId
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadFile")
    public BaseResponse<Object> uploadFile(@RequestParam("file") MultipartFile file, String fileName, String fileType, Integer userId) throws IOException {
        fileInfoService.uploadFile(file, fileName, fileType, userId);
        return null;
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
     * @param status
     * @param fileId
     * @return
     */
    @PostMapping("/switchFileStatus")
    public BaseResponse<Object> switchFileStatus(Integer status, Integer fileId) {
        fileInfoService.switchFileStatus(status, fileId);
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
     * 导入在线库文件 ok
     *
     * @param userId
     * @param fileIdArray
     * @return
     */
    @PostMapping("/uploadFileOnline")
    public BaseResponse<Object> uploadFileOnline(Integer userId, Integer[] fileIdArray) {
        fileInfoService.uploadFileOnline(userId, fileIdArray);
        return null;
    }

    /**
     * 获取文件树 ok
     *
     * @param userId
     * @return
     */
    @GetMapping("/getTree")
    public BaseResponse<List<TreeVo>> getTree(Integer userId) {
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
     * @param userId
     * @param fileIdArray
     * @return 返回文件的二进制流
     */
    @PostMapping("/exportFile")
    public BaseResponse<List<byte[]>> exportFile(Integer userId, Integer[] fileIdArray) {
        List<byte[]> fileExport = fileInfoService.exportFile(userId, fileIdArray);
        return ResultUtils.success(Code.SUCCESS, fileExport, "导出文件成功！");
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
        try {
            userId = Integer.parseInt(saveFileVo.getUserId());
            fileId = Integer.parseInt(saveFileVo.getFileId());
            content = saveFileVo.getContent();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }

        fileInfoService.saveFile(userId, fileId, content);
        return ResultUtils.success(Code.SUCCESS, null, "保存文件成功");
    }

    /**
     * 检索内容 ok
     *
     * @param userId
     * @param fileId
     * @param columnArray
     * @param keyWord
     * @return
     */
    @PostMapping("/queryFile")
    public BaseResponse<String[][]> queryFile(Integer userId, Integer fileId, String[] columnArray, String keyWord) {
        String[][] data = fileInfoService.queryFile(userId, fileId, columnArray, keyWord);

        return ResultUtils.success(Code.SUCCESS, data, "检索数据成功！");
    }

    /**
     * 获取比对文件字段 ok
     *
     * @param userId
     * @param fileIdArray
     * @return
     */
    @PostMapping("/getFields")
    public BaseResponse<Object> getFields(Integer userId, Integer[] fileIdArray) {
        String[][] data = fileInfoService.getFields(userId, fileIdArray);
        return ResultUtils.success(Code.SUCCESS, data, "获取比对文件字段成功！");
    }

    /**
     * 比对多个文件 ok
     *
     * @param userId
     * @param fileIdArray    要比对的文件
     * @param fieldArray     选中的字段矩阵，二维矩阵
     * @param saveFieldArray 结果文件中保存的字段列表
     * @param compareType    比对方式，正向或反向
     * @return
     */
    @PostMapping("/compareFiles")
    public BaseResponse<Object> compareFiles(Integer userId, Integer[] fileIdArray, String[][] fieldArray, boolean[][] saveFieldArray, String compareType) {
        String[][] data = fileInfoService.compareFiles(userId, fileIdArray, fieldArray, saveFieldArray, compareType);
        return ResultUtils.success(Code.SUCCESS, data, "比对文件成功！");
    }


}
