package com.wxjw.jwbigdata.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.data.CommentData;
import com.alibaba.excel.metadata.data.RichTextStringData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.common.OperType;
import com.wxjw.jwbigdata.domain.Operlog;
import com.wxjw.jwbigdata.domain.Uploadcolumn;
import com.wxjw.jwbigdata.domain.Uploadtable;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.listener.excel.ExcelListener;
import com.wxjw.jwbigdata.listener.excel.UploadListener;
import com.wxjw.jwbigdata.mapper.*;
import com.wxjw.jwbigdata.service.UploadtableService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Paul
 * @description 针对表【uploadtable(上传表参数)】的数据库操作Service实现
 * @createDate 2025-01-03 14:52:04
 */
@Service
public class UploadtableServiceImpl extends ServiceImpl<UploadtableMapper, Uploadtable>
        implements UploadtableService {

    @Resource
    private UploadtableMapper uploadtableMapper;

    @Resource
    private UploadcolumnMapper uploadcolumnMapper;

    @Resource
    private OperlogMapper operlogMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UploadMapper uploadMapper;

    @Override
    public List<Uploadtable> getUploadList() {
        List<Uploadtable> uploadtables = uploadtableMapper.selectList(new QueryWrapper<Uploadtable>());
        return uploadtables;
    }

    /**
     * 上传数据文件
     *
     * @param file     文件
     * @param fileId 表格名
     * @param userId   用户id
     * @throws IOException
     */
    @DSTransactional
    @Override
    public void uploadData(MultipartFile file, Integer fileId, Integer userId) throws BadSqlGrammarException,IOException,IllegalArgumentException {
        //参数校验
        User loginUser = userMapper.selectById(userId);
        if (loginUser == null) {
            throw new IllegalArgumentException( "用户不存在！");
        }
        Uploadtable uploadtable = uploadtableMapper.selectById(fileId);
        if (uploadtable == null) {
            throw new IllegalArgumentException( "所选择的表格不存在！");
        }

        //读取excel
        EasyExcel.read(file.getInputStream(), new UploadListener(userId, uploadtable.getTablename(),fileId, uploadtableMapper, uploadcolumnMapper, uploadMapper, operlogMapper)).doReadAll();

    }

    @Override
    public void exportTemplate(Integer userId, Integer fileId, HttpServletResponse response) throws IOException {
        // 设置响应头
        response.setHeader("Content-Disposition", "attachment; filename=\"uploadTemplate.xlsx\"");
        response.setContentType("application/vnd.ms-excel");

        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");
        OutputStream outputStream = response.getOutputStream();
        // 创建标题行
        Row titleRow = sheet.createRow(0);
        int index = 0;
        try {

            Uploadtable uploadtable = uploadtableMapper.selectById(fileId);
            List<Uploadcolumn> columns = uploadcolumnMapper.selectList(new QueryWrapper<Uploadcolumn>().eq("uploadtable_id", fileId).ne("columnname","id"));
            if(uploadtable == null){
                throw new IllegalArgumentException("所选表格不存在！");
            }
            else if(columns.isEmpty()){
                throw new IllegalArgumentException("所选表格未维护对应列，请联系管理员！");
            }
            else {
                for (Uploadcolumn column : columns) {
                    Cell titleCell = titleRow.createCell(index++);
                    titleCell.setCellValue(column.getComment());
                    if(!column.getDatatype().equals("varchar")){
                        String com = "";
                        String type = column.getDatatype();
                        if(type.equals("int")){
                            com = "整数列";
                        }
                        else if(type.equals("double") || type.equals("float")){
                            com = "数字列";
                        }
                        else if((type.equals("date"))){
                            com = "日期列（格式：2010-09-10）";
                        }
                        Drawing drawing1 = sheet.createDrawingPatriarch();
                        CreationHelper factory = sheet.getWorkbook().getCreationHelper();
                        ClientAnchor anchor = factory.createClientAnchor();
                        Comment comment1 = drawing1.createCellComment(anchor);
                        RichTextString str = factory.createRichTextString(com);
                        comment1.setString(str);
                        titleCell.setCellComment(comment1);
                    }
                }
                // 写入到输出流

                workbook.write(outputStream);
                outputStream.flush();
            }
            Operlog operlog = new Operlog();
            operlog.setUserId(userId);
            operlog.setOperType(OperType.downloadTemplate);
            operlog.setOperData(uploadtable.getComment());
            operlog.setOperTime(new Date());
            operlogMapper.insert(operlog);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
            // 关闭工作簿
            workbook.close();
        }
    }
}




