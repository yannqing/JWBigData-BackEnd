package com.wxjw.jwbigdata.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxjw.jwbigdata.domain.*;
import com.wxjw.jwbigdata.mapper.*;
import com.wxjw.jwbigdata.service.JwRuledetailService;
import com.wxjw.jwbigdata.utils.StringUtils;
import com.wxjw.jwbigdata.vo.BaseResponse;
import com.wxjw.jwbigdata.vo.RuleVo.RuleDetailVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
* @author Paul
* @description 针对表【jw_ruledetail】的数据库操作Service实现
* @createDate 2024-06-14 14:07:54
*/
@Service
@Slf4j
public class JwRuledetailServiceImpl extends ServiceImpl<JwRuledetailMapper, JwRuledetail>
    implements JwRuledetailService{

    @Resource
    private JwRuledetailMapper jwRuledetailMapper;

    @Resource
    private JwRuleMapper jwRuleMapper;

    @Resource
    private NewTableMapper newTableMapper;

    @Resource
    private NewColumnMapper newColumnMapper;

    @Override
    public List<RuleDetailVo> getSubrules(String ruleId) {
        if (ruleId == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        List<RuleDetailVo> result = new ArrayList<>();
        List<JwRuledetail> ruledetails = jwRuledetailMapper.selectList(new QueryWrapper<JwRuledetail>().eq("rule_id",ruleId));
        ruledetails.forEach(
                ruledetail ->{
                    Integer tableId1 = ruledetail.getTableId();
                    String fieldName1 = ruledetail.getFieldName();
                    Integer tableId2 = ruledetail.getMatchtableId();
                    String fieldName2 = ruledetail.getMatchfieldName();
                    NewTable table1 = new NewTable();
                    NewColumn field1 = new NewColumn();
                    NewTable table2 = new NewTable();
                    NewColumn field2 = new NewColumn();

                    if(tableId1 > 0 && fieldName1.length()> 0){
                        table1 = newTableMapper.selectById(tableId1);
                        field1 = newColumnMapper.selectOne(new QueryWrapper<NewColumn>().eq("id",tableId1).eq("columnname",fieldName1));
                    }

                    if(tableId2 > 0 && fieldName2.length()> 0) {
                        table2 = newTableMapper.selectById(tableId2);
                        field2 = newColumnMapper.selectOne(new QueryWrapper<NewColumn>().eq("id",tableId2).eq("columnname",fieldName2));
                    }

                    result.add(new RuleDetailVo(ruledetail,table1,field1,table2,field2));
                }
        );
        log.info("查询规则{}的所有子规则", ruleId);
        return result;
    }

    @Override
    public void addSubrule(String ruleId, String note, String tableId, String fieldName, String matchType, String pattern, String matchValue, String matchTableId, String matchFieldName) {
        if (jwRuleMapper.selectById(ruleId) == null) {
            throw new IllegalArgumentException("规则不存在，请重试！");
        }
        JwRuledetail ruledetail = new JwRuledetail();
        ruledetail.setTableId(StringUtils.isEmpty(tableId)?0:Integer.parseInt(tableId));
        ruledetail.setFieldName(fieldName);
        ruledetail.setMatchType(matchType);
        ruledetail.setMatchValue(matchValue);
        ruledetail.setMatchtableId(StringUtils.isEmpty(matchTableId)?0:Integer.parseInt(matchTableId));
        ruledetail.setMatchfieldName(matchFieldName);
        ruledetail.setNote(note);
        ruledetail.setCreateTime(DateTime.now());
        ruledetail.setRuleId(Integer.parseInt(ruleId));
        jwRuledetailMapper.insert(ruledetail);
        //规则设置未未启用状态
        jwRuleMapper.update(new UpdateWrapper<JwRule>()
                .eq("rule_id", ruleId)
                .set("is_on", 0));
        log.info("规则{}中添加子规则成功", ruleId);
    }

    @Override
    public void delSubrule(String subruleId) {
        if (jwRuledetailMapper.selectById(subruleId) == null) {
            throw new IllegalArgumentException("规则不存在，请重试！");
        }
        jwRuledetailMapper.deleteById(subruleId);
        log.info("删除规则明细成功，删除的规则明细id：{}", (Object) subruleId);
    }
}




