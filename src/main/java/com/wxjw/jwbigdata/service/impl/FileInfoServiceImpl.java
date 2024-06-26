package com.wxjw.jwbigdata.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.domain.FileInfo;
import com.wxjw.jwbigdata.domain.JwTable;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.listener.excel.ExcelListener;
import com.wxjw.jwbigdata.mapper.FileInfoMapper;
import com.wxjw.jwbigdata.mapper.JwTableMapper;
import com.wxjw.jwbigdata.mapper.OperationMapper;
import com.wxjw.jwbigdata.mapper.UserMapper;
import com.wxjw.jwbigdata.service.FileInfoService;
import com.wxjw.jwbigdata.vo.FileVo.ChildrenVo;
import com.wxjw.jwbigdata.vo.FileVo.OnlineFileVo;
import com.wxjw.jwbigdata.vo.FileVo.TreeVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author yannqing
* @description 针对表【file_info】的数据库操作Service实现
* @createDate 2024-06-08 10:36:47
*/
@Service
@Slf4j
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo>
    implements FileInfoService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private FileInfoMapper fileInfoMapper;
    @Resource
    private JwTableMapper jwTableMapper;
    @Autowired
    private OperationMapper operationMapper;

    @Override
    public void uploadFile(MultipartFile file, String fileName, String fileType, Integer userId) throws IOException {
        //参数校验
        User loginUser = userMapper.selectById(userId);
        if (loginUser == null) {
            throw new IllegalArgumentException("用户不存在！");
        }
        if (file == null) {
            throw new IllegalArgumentException("上传文件为空！");
        }
        //读取excel
        EasyExcel.read(file.getInputStream(), new ExcelListener(loginUser.getRole(), fileName, userId, fileInfoMapper)).sheet().doRead();
    }

    @Override
    public void delFile(Integer[] fileId) {
        //参数校验
        if (fileId.length == 0) {
            throw new IllegalArgumentException("参数为空！");
        }
        //遍历所有的文件id
        Arrays.stream(fileId).forEach(id -> {
            //查找是否存在子文件
            List<FileInfo> childrenFiles = fileInfoMapper.selectList(new QueryWrapper<FileInfo>().eq("parent_id", id));
            if (!childrenFiles.isEmpty()) {
                //如果存在则删除
                List<Integer> childrenIds = childrenFiles.stream().map(FileInfo::getId).collect(Collectors.toList());
                fileInfoMapper.deleteByIds(childrenIds);
                //删除数据表
                for (FileInfo child : childrenFiles) {
                    operationMapper.dropTable(child.getTableName());
                }
            }
            //删除父文件
            fileInfoMapper.deleteById(id);
        });
        log.info("删除库文件");
    }

    @Override
    public void switchFileStatus(Integer status, Integer fileId) {
        fileInfoMapper.update(new UpdateWrapper<FileInfo>().eq("id", fileId).set("status", status));
        log.info("更改文件{}显示状态{}", fileId, status);
    }

    @Override
    public List<TreeVo> getTree(Integer userId) {
        List<TreeVo> tree = new ArrayList<>();
        TreeVo privateTree = getSingleTree(1, userId);
        TreeVo resultTree = getSingleTree(2, userId);
        TreeVo publicTree = getSingleTree(3, userId);
        tree.add(privateTree);
        tree.add(resultTree);
        tree.add(publicTree);
        return tree;
    }

    /**
     * 获取单个库的所有节点
     * @param fileId 1个人库，2结果库，3公共库
     * @param userId 如果是个人库需要判断
     * @return
     */
    private TreeVo getSingleTree(Integer fileId, Integer userId) {
        TreeVo tree = new TreeVo();
        switch (fileId) {
            case 1: {
                //个人库
                tree.setLabel("个人库");
                tree.setDraggable(false);
                tree.setChildren(getChildren(1));
            };
            case 2: {
                //结果库
                tree.setLabel("结果库");
                tree.setDraggable(false);
                tree.setChildren(getChildren(2));

            };
            case 3: {
                //公共库
                tree.setLabel("公共库");
                tree.setDraggable(false);
                tree.setChildren(getChildren(3));

            }
        }
        return tree;
    }

    private List<ChildrenVo> getChildren(Integer parentId) {
        List<FileInfo> childrenFileInfos = fileInfoMapper.selectList(new QueryWrapper<FileInfo>().eq("parent_id", parentId));

        List<ChildrenVo> children = new ArrayList<>();
        for (FileInfo fileInfo : childrenFileInfos) {
            ChildrenVo child = new ChildrenVo();
            child.setId(fileInfo.getId().toString());
            child.setLabel(fileInfo.getFileName() == null ? fileInfo.getTableName() : fileInfo.getFileName());
            child.setDraggable(fileInfo.getIsEnd() == 1);
            child.setChildren(fileInfo.getIsEnd() == 1 ? null : getChildren(fileInfo.getId()));
            children.add(child);
        }
        return children;
    }

    @Override
    public void uploadFileOnline(Integer userId, Integer[] fileIdArray) {
        if (userId == null || fileIdArray == null) {
            throw new IllegalArgumentException("参数为空！");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        int parentId = 0;
        if (user.getRole() == 1) {
            //admin
            parentId = 3;
        }else {
            //user
            parentId = 1;
        }
        for (Integer integer : fileIdArray) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setParentId(parentId);
            fileInfo.setFileName(null);
            fileInfo.setTableName(jwTableMapper.selectById(integer).getTableName());
            fileInfo.setIsEnd(1);
            fileInfo.setCreateBy(null);
            fileInfoMapper.insert(fileInfo);
        }
    }

    @Override
    public List<byte[]> exportFile(Integer userId, Integer[] fileIdArray) {
        List<byte[]> excelBytesList = new ArrayList<>();
        for (Integer fileId : fileIdArray) {
            FileInfo fileInfo = fileInfoMapper.selectById(fileId);
            if (fileInfo == null) {
                // 处理文件不存在的情况，例如抛出异常或记录日志
                log.error("此id {} 不存在，请检查！", fileId);
                continue;
            }
            String tableName = fileInfo.getTableName();
            List<List<Object>> dataList = operationMapper.getData(tableName);
            try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                 ExcelWriter excelWriter = EasyExcel.write(out).build()) {

                WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").build();
                excelWriter.write(dataList, writeSheet);

                excelWriter.finish(); // 完成写入

                excelBytesList.add(out.toByteArray()); // 将生成的 Excel 字节数组添加到列表中
            } catch (IOException e) {
                throw new RuntimeException("导出 Excel 文件失败", e);
            }
        }
        return excelBytesList;
    }

    @Override
    public List<OnlineFileVo> getOnlineFiles(Integer userId) {
        List<OnlineFileVo> onlineFiles = new ArrayList<>();

        List<JwTable> jwTables = jwTableMapper.selectList(null);
        for (JwTable jw : jwTables) {
            OnlineFileVo onlineFileVo = new OnlineFileVo(jw.getTableId().toString(), jw.getTableName());
            onlineFiles.add(onlineFileVo);
        }
        return onlineFiles;
    }
}




