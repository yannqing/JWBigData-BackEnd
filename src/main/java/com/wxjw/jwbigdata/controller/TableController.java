package com.wxjw.jwbigdata.controller;

import com.alibaba.fastjson.JSONArray;
import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.service.NewTableService;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.BaseResponse;
import com.wxjw.jwbigdata.vo.TableVo.AuthTable;
import com.wxjw.jwbigdata.vo.UserVo.UserVo;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author panhao
 * @version 1.0.0
 * @className TableController
 * @description TODO
 * @date 2024-06-15 12:10
 **/
@RestController
public class TableController {
    @Resource
    private NewTableService newTableService;

    @PostMapping("/getFieldsList")
    public BaseResponse<Object> getFieldsList(@RequestParam Integer userId){
        JSONArray result = newTableService.getFieldsList(userId);
        return ResultUtils.success(Code.SUCCESS,result,"获取所有字段");
    }

    @PostMapping("/getUserAuthTables")
    public BaseResponse<Object> getUserAuthTables(@RequestBody Map<String, Integer> data){
        ArrayList<AuthTable> result = newTableService.getUserAuthTables(data.get("userId"));
        return ResultUtils.success(Code.SUCCESS,result,"获取用户关联权限的表格");
    }

    @GetMapping("/getAlTables")
    public BaseResponse<Object> getAllTables(){
        ArrayList<AuthTable> result = newTableService.getAllTables();
        return ResultUtils.success(Code.SUCCESS,result,"获取所有表格");
    }
}
