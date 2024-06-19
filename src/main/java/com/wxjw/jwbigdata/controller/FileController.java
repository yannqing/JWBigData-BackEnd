package com.wxjw.jwbigdata.controller;

import com.wxjw.jwbigdata.service.FileInfoService;
import com.wxjw.jwbigdata.vo.BaseResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileController {

    @Resource
    private FileInfoService fileInfoService;

    @PostMapping("/uploadFile")
    public BaseResponse<Object> uploadFile(@RequestParam("file") MultipartFile file, String fileName, String fileType, String userId) throws IOException {
        fileInfoService.uploadFile(file, fileName, fileType, userId);
        return null;
    }
}
