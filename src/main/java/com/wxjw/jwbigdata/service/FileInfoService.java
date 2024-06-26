package com.wxjw.jwbigdata.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxjw.jwbigdata.domain.FileInfo;
import com.wxjw.jwbigdata.vo.FileVo.OnlineFileVo;
import com.wxjw.jwbigdata.vo.FileVo.TreeVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


/**
* @author 67121
* @description 针对表【file_info】的数据库操作Service
* @createDate 2024-06-08 10:36:47
*/
public interface FileInfoService extends IService<FileInfo> {

    void uploadFile(MultipartFile file, String fileName, String fileType, Integer userId) throws IOException;

    void delFile(Integer[] fileId);

    void switchFileStatus(Integer status, Integer fileId);

    List<TreeVo> getTree(Integer userId);

    void uploadFileOnline(Integer userId, Integer[] fileIdArray);

    List<byte[]> exportFile(Integer userId, Integer[] fileIdArray);

    List<OnlineFileVo> getOnlineFiles(Integer userId);
}
