package com.wxjw.jwbigdata.InitController;
import java.util.Date;

import com.wxjw.jwbigdata.domain.FileInfo;
import com.wxjw.jwbigdata.mapper.FileInfoMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InitDatabase {

    @Resource
    private FileInfoMapper fileInfoMapper;

    @Test
    public void initFileInfo() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(1);
        fileInfo.setParentId(0);
        fileInfo.setFileName("公共库");
        fileInfo.setTableName("");
        fileInfo.setIsEnd(0);
        fileInfo.setCreateBy(1);
        fileInfo.setCreateTime(new Date());
        fileInfo.setStatus(1);

        FileInfo fileInfo1 = new FileInfo();
        fileInfo1.setId(2);
        fileInfo1.setParentId(0);
        fileInfo1.setFileName("个人库");
        fileInfo1.setTableName("");
        fileInfo1.setIsEnd(0);
        fileInfo1.setCreateBy(1);
        fileInfo1.setCreateTime(new Date());
        fileInfo1.setStatus(1);

        FileInfo fileInfo2 = new FileInfo();
        fileInfo2.setId(3);
        fileInfo2.setParentId(0);
        fileInfo2.setFileName("结果库");
        fileInfo2.setTableName("");
        fileInfo2.setIsEnd(0);
        fileInfo2.setCreateBy(1);
        fileInfo2.setCreateTime(new Date());
        fileInfo2.setStatus(1);

        fileInfoMapper.insert(fileInfo);
        fileInfoMapper.insert(fileInfo1);
        fileInfoMapper.insert(fileInfo2);
    }
}
