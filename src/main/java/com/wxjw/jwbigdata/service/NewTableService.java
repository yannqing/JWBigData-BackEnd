package com.wxjw.jwbigdata.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wxjw.jwbigdata.domain.NewTable;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wxjw.jwbigdata.vo.TableVo.AuthTable;

import java.util.ArrayList;
import java.util.List;

/**
* @author Paul
* @description 针对表【newtable】的数据库操作Service
* @createDate 2024-09-02 22:32:58
*/
public interface NewTableService extends IService<NewTable> {

    JSONArray getFieldsList(Integer userId);

    JSONObject humanIdPortrait(String keyWord,Integer userId);

    JSONObject companyPortrait(String keyWord,Integer userId);

    JSONArray humanListByid(String keyWord);

    JSONArray humanListByName(String keyWord);

    JSONArray companyList(String keyWord);

    JSONObject searchListByKeyWord(List<Integer> tables,String keyWord);

    ArrayList<AuthTable> getUserAuthTables(Integer userId);

    ArrayList<AuthTable> getAllTables();
}
