package com.wxjw.jwbigdata.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wxjw.jwbigdata.domain.NewTable;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Paul
* @description 针对表【newtable】的数据库操作Service
* @createDate 2024-09-02 22:32:58
*/
public interface NewTableService extends IService<NewTable> {

    JSONArray getFieldsList();

    JSONObject humanIdPortrait(String keyWord);

    JSONObject companyPortrait(String keyWord);

    JSONArray humanListByid(String keyWord);

    JSONArray humanListByName(String keyWord);

    JSONArray companyList(String keyWord);
}
