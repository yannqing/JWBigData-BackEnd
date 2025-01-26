package com.wxjw.jwbigdata.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wxjw.jwbigdata.common.Code;
import com.wxjw.jwbigdata.common.OperType;
import com.wxjw.jwbigdata.domain.NewTable;
import com.wxjw.jwbigdata.domain.Operlog;
import com.wxjw.jwbigdata.domain.RelationOfNewtable;
import com.wxjw.jwbigdata.domain.User;
import com.wxjw.jwbigdata.mapper.NewTableMapper;
import com.wxjw.jwbigdata.mapper.OperlogMapper;
import com.wxjw.jwbigdata.service.NewTableService;
import com.wxjw.jwbigdata.service.RelationOfNewtableService;
import com.wxjw.jwbigdata.utils.JwtUtils;
import com.wxjw.jwbigdata.utils.ResultUtils;
import com.wxjw.jwbigdata.vo.BaseResponse;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author panhao
 * @version 1.0.0
 * @className Portrait
 * @description TODO
 * @date 2024-06-16 22:04
 **/
@RestController
public class PortraitController {
    @Resource
    private NewTableService newTableService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private OperlogMapper operlogMapper;

    /**
     * @param type:"1"是人物，“2”是单位
     * @param keyWord:身份证号、姓名、公司名、公司id
     * @return
     */
    @PostMapping("/getPortraitList")
    public BaseResponse<JSONObject> getPortraitList(String type, String keyWord, HttpServletRequest request) throws JsonProcessingException {
        JSONArray result;

        if (type.equals("1")) {
            // 人物画像
            String regex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
            Boolean isId = Pattern.matches(regex, keyWord);
            if (isId) {
                try {
                    // 关键字是身份证
                    result = newTableService.humanListByid(keyWord);
                } catch (Exception ex) {
                    return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
                }

            } else {
                try {
                    // 关键字是姓名
                    result = newTableService.humanListByName(keyWord);
                } catch (Exception ex) {
                    return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
                }
            }
        } else if (type.equals("2")) {
            try {
                // 单位画像
                result = newTableService.companyList(keyWord);
            } catch (Exception ex) {
                return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
            }
        } else return ResultUtils.failure(Code.FAILURE, null, "画像类型错误！");
//        JSONArray result = jwRuleService.getRuleResult(ruleId);

        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);
        Operlog operlog = new Operlog();
        operlog.setUserId(loginUser.getId());
        operlog.setOperType(OperType.getPortraitList);
        operlog.setOperData(keyWord);
        operlog.setOperTime(new Date());
        operlogMapper.insert(operlog);
        return ResultUtils.success(Code.SUCCESS, result, "获取人物画像结果");
    }

    /**
     * @param type:"1"是人物，“2”是单位
     * @param keyInfo：String     //对于个人，是id；对于企业，可能name或idNum（企业可能没有）
     * @return
     */
    @PostMapping("/getResultDetail")
    public BaseResponse<JSONObject> portrait(String type, String keyInfo, HttpServletRequest request) throws JsonProcessingException {
        JSONObject result = new JSONObject();
        //从token得到创建者的userId
        String token = request.getHeader("token");
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfo, User.class);
        if (type.equals("1")) {
            try {
                // 人物画像
                // 关键字是id主键
                result = newTableService.humanIdPortrait(keyInfo, loginUser.getId());
            } catch (Exception ex) {
                return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
            }

        } else if (type.equals("2")) {
            try {
                // 单位画像
                result = newTableService.companyPortrait(keyInfo, loginUser.getId());
            } catch (Exception ex) {
                return ResultUtils.failure(Code.FAILURE, null, ex.getMessage());
            }
        } else
            return ResultUtils.failure(Code.FAILURE, result, "画像类型错误！");
//        JSONArray result = jwRuleService.getRuleResult(ruleId);
        return ResultUtils.success(Code.SUCCESS, result, "获取人物画像结果");
    }


}
