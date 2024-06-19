package com.wxjw.jwbigdata.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wxjw.jwbigdata.domain.JwTable;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Paul
* @description 针对表【jw_table】的数据库操作Service
* @createDate 2024-06-14 14:08:08
*/
public interface JwTableService extends IService<JwTable> {

    JSONArray getFieldsList();

    JSONArray humanIdPortrait(String keyWord);

    JSONArray humanNamePortrait(String keyWord);

    JSONArray companyNamePortrait(String keyWord);
}
