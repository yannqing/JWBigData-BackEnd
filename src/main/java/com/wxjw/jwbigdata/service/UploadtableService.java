package com.wxjw.jwbigdata.service;

import com.wxjw.jwbigdata.domain.Uploadtable;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
* @author Paul
* @description 针对表【uploadtable(上传表参数)】的数据库操作Service
* @createDate 2025-01-03 14:52:04
*/
public interface UploadtableService extends IService<Uploadtable> {
    List<Uploadtable> getUploadList();

    void exportTemplate(Integer userId, Integer fileId, HttpServletResponse response) throws IOException;

    void uploadData(MultipartFile file, Integer fileId,Integer userId) throws BadSqlGrammarException, IOException,IllegalArgumentException;


}
