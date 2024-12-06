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
                    Integer fieldId = ruledetail.getFieldId()==null?0:ruledetail.getFieldId();
                    Integer matchFieldId = ruledetail.getMatchfieldId()==null?0:ruledetail.getMatchfieldId();
                    NewTable table1 = new NewTable();
                    NewColumn field1 = new NewColumn();
                    NewTable table2 = new NewTable();
                    NewColumn field2 = new NewColumn();

                    if(fieldId > 0 ){
                        field1 = newColumnMapper.selectById(fieldId);
                        table1 = newTableMapper.selectById(field1.getNewtableId());
                    }

                    if(matchFieldId > 0 ){
                        field2 = newColumnMapper.selectById(matchFieldId);
                        table2 = newTableMapper.selectById(field2.getNewtableId());
                    }

                    result.add(new RuleDetailVo(ruledetail,table1,field1,table2,field2));
                }
        );
        log.info("查询规则{}的所有子规则", ruleId);
        return result;
    }

    @Override
    public void addSubrule(String ruleId, String note, String tableId, String fieldId, String matchType, String pattern, String matchValue, String matchTableId, String matchFieldId) {
        if (jwRuleMapper.selectById(ruleId) == null) {
            throw new IllegalArgumentException("规则不存在，请重试！");
        }
        NewColumn field = newColumnMapper.selectById(StringUtils.isEmpty(fieldId) ? -1 : Integer.parseInt(fieldId));
        NewColumn matchfield = newColumnMapper.selectById(StringUtils.isEmpty(matchFieldId) ? -1 : Integer.parseInt(matchFieldId));
        JwRuledetail ruledetail = new JwRuledetail();
        if(field != null){
            ruledetail.setFieldId(field.getId());
        }
        ruledetail.setMatchType(matchType);
        ruledetail.setMatchValue(matchValue);
        ruledetail.setPattern(pattern);
        if(matchfield != null){
            ruledetail.setMatchfieldId(matchfield.getId());
        }
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




