package com.wxjw.jwbigdata.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxjw.jwbigdata.domain.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
* @author 67121
* @description 针对表【file_info】的数据库操作Service
* @createDate 2024-06-08 10:36:47
*/
public interface FileInfoService extends IService<FileInfo> {

    void uploadFile(MultipartFile file, String fileName, String fileType, String userId) throws IOException;
}
