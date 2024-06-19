package com.wxjw.jwbigdata.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.domain.FileInfo;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.listener.excel.ExcelListener;
import com.wxjw.jwbigdata.mapper.FileInfoMapper;
import com.wxjw.jwbigdata.mapper.UserMapper;
import com.wxjw.jwbigdata.service.FileInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
* @author yannqing
* @description 针对表【file_info】的数据库操作Service实现
* @createDate 2024-06-08 10:36:47
*/
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo>
    implements FileInfoService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private FileInfoMapper fileInfoMapper;

    @Override
    public void uploadFile(MultipartFile file, String fileName, String fileType, String userId) throws IOException {
        //参数校验
        User loginUser = userMapper.selectById(userId);
        if (loginUser == null) {
            throw new IllegalArgumentException("用户不存在！");
        }
        if (file == null) {
            throw new IllegalArgumentException("上传文件为空！");
        }
        //读取excel
        EasyExcel.read(file.getInputStream(), new ExcelListener(loginUser.getRole(), "", fileInfoMapper)).sheet().doRead();
    }
}




