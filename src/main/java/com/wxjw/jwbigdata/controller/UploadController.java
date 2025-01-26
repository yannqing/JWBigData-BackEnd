package com.wxjw.jwbigdata.controller;

import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.domain.Uploadtable;
import com.wxjw.jwbigdata.service.UploadtableService;
import com.wxjw.jwbigdata.service.impl.UploadServiceImpl;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.BaseResponse;
import com.wxjw.jwbigdata.vo.FileVo.fileData;
import com.wxjw.jwbigdata.vo.UserVo.UserVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author black
 * @version 1.0.0
 * @className UploadController
 * @description TODO
 * @date 2024-12-31 10:43
 **/
@RestController
public class UploadController {
    @Resource
    private UploadtableService uploadtableService;
    /**
     * 获取所有上传库中的表格
     * @return
     */
    @GetMapping("/getUploadList")
    public BaseResponse<List<Uploadtable>> getUploadList() {
        List<Uploadtable> uploadList = uploadtableService.getUploadList();
        return ResultUtils.success(Code.SUCCESS, uploadList, "获取所有上传表格");
    }

    /**
     * 导出模板文件
     *
     * @param data
     * @return 返回文件的二进制流
     */
    @PostMapping("/exportTemplate")
    public void exportTemplate(@RequestBody Map<String,Integer> data, HttpServletResponse response){
        try {
            uploadtableService.exportTemplate(data.get("userId"), data.get("fileId"), response);
        }
        catch (IOException e){
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 上传数据文件
     *
     * @param file
     * @param
     * @param fileId
     * @param userId
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadData")
    public BaseResponse<Object> uploadData(@RequestParam("file") MultipartFile file, Integer userId, Integer fileId) {
        String fileType = extractFileNameWithExtension(file);
        if(!fileType.equals("xlsx") && !fileType.equals("xls") && !fileType.equals("XLSX") && !fileType.equals("XLS") ){
            return ResultUtils.failure(Code.FAILURE, null, "请上传excel文件！");
        }
        try{
            uploadtableService.uploadData(file, fileId, userId);
            return ResultUtils.success(Code.SUCCESS, null, "数据上传成功！");
        }
        catch (IllegalArgumentException illegalArgumentException){
            return ResultUtils.failure(Code.FAILURE, null, illegalArgumentException.getMessage());
        }
        catch(BadSqlGrammarException ex){
            return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
        }
        catch (IOException ioException){
            return ResultUtils.failure(Code.FAILURE, null, "数据上传失败，请联系管理员！");
        }

    }

    public static String extractFileNameWithExtension(MultipartFile file) {
        if (file == null) {
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return null;
        }
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }
}
