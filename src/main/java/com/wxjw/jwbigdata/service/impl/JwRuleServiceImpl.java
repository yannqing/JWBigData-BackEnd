package com.wxjw.jwbigdata.service.impl;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wxjw.jwbigdata.config.TableConfig;
import com.wxjw.jwbigdata.domain.*;
import com.wxjw.jwbigdata.mapper.*;
import com.wxjw.jwbigdata.service.JwRuleService;
import com.wxjw.jwbigdata.utils.StringUtils;
import com.wxjw.jwbigdata.vo.RuleVo.RuleVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author Paul
* @description 针对表【jw_rule】的数据库操作Service实现
* @createDate 2024-06-14 14:06:48
*/
@Slf4j
@Service
public class JwRuleServiceImpl extends ServiceImpl<JwRuleMapper, JwRule>
    implements JwRuleService{

    @Resource
    private JwRuleMapper jwRuleMapper;
    
    @Resource
    private JwRuledetailMapper jwRuledetailMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private NewTableMapper jwTableMapper;

    @Resource
    private NewColumnMapper jwFieldMapper;

    @Autowired
    private TableConfig tableConfig;

    @Resource
    private ModeltaskMapper modeltaskMapper;

    @Override
    public void addRule(String userId, String ruleName, String ruleComment, String ruleSteps) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("id", userId));
        if(user == null)
            throw new IllegalArgumentException("用户不存在，请重试！");
        JwRule rule = new JwRule();
        rule.setRuleName(ruleName);
        rule.setNote(ruleSteps);
        rule.setDescription(ruleComment);
        rule.setCreateBy(Integer.parseInt(userId));
        rule.setCreateTime(DateTime.now());
        if(user.getRole()==1) //管理员:公有模型
            rule.setStatus(1);
        else rule.setStatus(0); //普通用户：私有模型
        jwRuleMapper.insert(rule);
        log.info("用户{}新增了一个模型{}", user.getUsername(), ruleName);
    }

    @Override
    public void delRule(String[] ruleIds) {
        Arrays.stream(ruleIds).forEach(ruleId ->{
            if (jwRuleMapper.selectById(ruleId) == null) {
                throw new IllegalArgumentException("模型不存在，请重试！");
            }
        });
        Arrays.stream(ruleIds).forEach(ruleId -> jwRuleMapper.deleteById(ruleId));
        log.info("删除模型成功，删除的模型id：{}", (Object) ruleIds);
    }

    @Override
    public void switchRuleStatus(String ruleId, Integer status) {
        //校验参数是否有效
        JwRule rule = jwRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new IllegalArgumentException("模型不存在，请重试！");
        }
        if (status != 1 && status != 0) {
            throw new IllegalArgumentException("状态错误！无法修改");
        }
        //修改状态
        jwRuleMapper.update(new UpdateWrapper<JwRule>()
                .eq("rule_id", ruleId)
                .set("status", status)
        );
        log.info("修改{}模型的状态成功", rule.getRuleName());
    }

    @Override
    public List<RuleVo> getRuleList() {
        List<JwRule> rules = jwRuleMapper.selectList(new QueryWrapper<>());
        List<RuleVo> ruleVos = new ArrayList<>();
        rules.forEach(rule -> {
            RuleVo vo = new RuleVo(rule, userMapper.selectById(rule.getCreateBy()));
            ruleVos.add(vo);
        });
        log.info("查询所有模型成功！");
        return ruleVos;
    }

    @Override
    public void editRule(Integer ruleId, String ruleName, String ruleComment, String ruleSteps) {
        JwRule rule = jwRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new IllegalArgumentException("模型不存在，请重试！");
        }
        jwRuleMapper.update(new UpdateWrapper<JwRule>()
                .eq("rule_id", ruleId)
                .set("rule_name", ruleName)
                .set("description", ruleComment)
                .set("note", ruleSteps));
        log.info("模型{}更新信息", rule.getRuleName());
    }

    @Override
    public JSONObject getRules(String userId) {
        List<JwRule> publicrules = jwRuleMapper.selectList(new QueryWrapper<JwRule>().eq("status",1).ne("create_by",userId));
        List<RuleVo> publicruleVos = new ArrayList<>();
        publicrules.forEach(rule -> {
            RuleVo vo = new RuleVo(rule, userMapper.selectById(rule.getCreateBy()));
            publicruleVos.add(vo);
        });

        List<JwRule> privaterules = jwRuleMapper.selectList(new QueryWrapper<JwRule>().eq("create_by",userId));
        List<RuleVo> privateruleVos = new ArrayList<>();
        privaterules.forEach(rule -> {
            RuleVo vo = new RuleVo(rule, userMapper.selectById(rule.getCreateBy()));
            privateruleVos.add(vo);
        });
        JSONObject result = new JSONObject();
        result.put("publicRules",publicruleVos);
        result.put("privateRules",privateruleVos);
        return result;
    }

    @Override
    public RuleVo getRuleInfo(String ruleId) {
        if (StringUtils.isEmpty(ruleId)) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        JwRule rule = jwRuleMapper.selectById(Integer.parseInt(ruleId));
        if (rule == null) {
            throw new IllegalArgumentException("无该模型");
        }
        log.info("模型{}获取信息成功", rule.getRuleName());
        return new RuleVo(rule, userMapper.selectById(rule.getCreateBy()));
    }

    @Override
    public void switchRuleOn(String ruleId, Integer isOn) {
        //校验参数是否有效
        JwRule rule = jwRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new IllegalArgumentException("模型不存在，请重试！");
        }
        if (isOn != 1 && isOn != 0) {
            throw new IllegalArgumentException("启用状态错误！无法修改");
        }

        if(isOn == 0){
            // 修改为未启用
            //修改状态
            jwRuleMapper.update(new UpdateWrapper<JwRule>()
                    .eq("rule_id", ruleId)
                    .set("is_on", isOn)
            );
            modeltaskMapper.delete(new QueryWrapper<Modeltask>().eq("modelId",ruleId));
        }
        else{
            // 需生成sql_statement
            List<JwRuledetail> ruledetails = jwRuledetailMapper.selectList(new QueryWrapper<JwRuledetail>().eq("rule_id", ruleId));

            //匹配常量
            List<JwRuledetail> ruleDetailsType1 = ruledetails.stream().filter(
                    ruledetail -> ruledetail.getMatchType().equals("1")
            ).toList();
            //关联字段
            List<JwRuledetail> ruleDetailsType2 = ruledetails.stream().filter(
                    ruledetail -> ruledetail.getMatchType().equals("2")
            ).toList();
            //汇总数量
            List<JwRuledetail> ruleDetailsType3 = ruledetails.stream().filter(
                    ruledetail -> ruledetail.getMatchType().equals("3")
            ).toList();
            //汇总合计
            List<JwRuledetail> ruleDetailsType4 = ruledetails.stream().filter(
                    ruledetail -> ruledetail.getMatchType().equals("4")
            ).toList();
            //字段比较
            List<JwRuledetail> ruleDetailsType5 = ruledetails.stream().filter(
                    ruledetail -> ruledetail.getMatchType().equals("5")
            ).toList();
            //比较当前日期
            List<JwRuledetail> ruleDetailsType6 = ruledetails.stream().filter(
                    ruledetail -> ruledetail.getMatchType().equals("6")
            ).toList();

            String selectSql = "";
            String fromSql = "";
            String whereSql = " WHERE 1=1 ";
            String groupbySql = "";
            String havingSql = "";
            Integer type = -1;

            //循环type2:表字段连接
            Set<String> tableset = new HashSet();
            for (JwRuledetail ruleDetail :ruleDetailsType2) {
                NewTable table1 = jwTableMapper.selectById(ruleDetail.getTableId());
                String tableName1 = table1.getTablename();
                NewTable table2 = jwTableMapper.selectById(ruleDetail.getMatchtableId());
                String tableName2 = table2.getTablename();
                tableset.add(tableName1);
                if(ruleDetail.getPattern().equals("=")){
                    tableset.add(table2.getTablename());
                    whereSql += " AND "+tableName1+"."+ruleDetail.getFieldName()+" = "+
                            tableName2+"."+ruleDetail.getMatchfieldName();
                }
                else{
                    whereSql += " AND "+tableName1+"."+ruleDetail.getFieldName()+" NOT IN (SELECT "+
                            ruleDetail.getMatchfieldName() + " FROM "+ tableName2+") ";
                }
            }
            for(String tableName : tableset)
            {
                if(StringUtils.isEmpty(fromSql))
                {
                    fromSql += " FROM "+tableName;
                }
                else
                    fromSql += " ,"+tableName;
            }
            //模型类型
            if(tableset.contains(tableConfig.gethumanTable()))
            {
                type = 0;
                selectSql = "SELECT "+tableConfig.gethumanTable()+'.'+tableConfig.gethumanId()+" '身份证号',"+tableConfig.gethumanTable()+'.'+tableConfig.gethumanName()+" '姓名'";
            }
            else if(tableset.contains(tableConfig.getcompanyTable()))
            {
                type = 1;
                selectSql = "SELECT "+tableConfig.getcompanyTable()+'.'+tableConfig.getcompanyId()+" '单位识别号',"+tableConfig.getcompanyTable()+'.'+tableConfig.getcompanyName()+" '名称'";
            }
            else{
                throw new IllegalArgumentException("模型必须关联人物主表（"+tableConfig.gethumanTable()+"）或单位主表（"+tableConfig.getcompanyTable()+"）！");
            }


            //循环type1:表字段匹配常量
            for (JwRuledetail ruleDetail1:ruleDetailsType1) {
                NewTable table1 = jwTableMapper.selectById(ruleDetail1.getTableId());
                String tableName1 = table1.getTablename();
                if(tableset.contains(tableName1))
                {
                    String marchvalue = ruleDetail1.getMatchValue();
                    String fieldName = ruleDetail1.getFieldName();
                    NewColumn field = jwFieldMapper.selectOne(new QueryWrapper<NewColumn>().eq("id",table1.getId()).eq("columnname",fieldName));
                    //如果不是数字类型，需要特殊拼接处理

                    if(ruleDetail1.getPattern().equals("like"))
                    {
                        marchvalue = "'%"+marchvalue+"%'";
                    }
                    else marchvalue = "'"+marchvalue+"'";

                    selectSql += ","+tableName1+"."+ruleDetail1.getFieldName()+" '"+tableName1+"-"+fieldName+"'";
                    whereSql += " AND "+tableName1+"."+ruleDetail1.getFieldName()+" "+
                            ruleDetail1.getPattern()+marchvalue;
                }
            }

            //循环type5:表字段与字段间匹配差额
            for (JwRuledetail ruleDetail5:ruleDetailsType5) {
                NewTable table1 = jwTableMapper.selectById(ruleDetail5.getTableId());
                String tableName1 = table1.getTablename();
                NewTable table2 = jwTableMapper.selectById(ruleDetail5.getMatchtableId());
                String tableName2 = table2.getTablename();
                String marchvalue = ruleDetail5.getMatchValue();
                String fieldName1 = ruleDetail5.getFieldName();
                String fieldName2 = ruleDetail5.getMatchfieldName();
                if(tableset.contains(tableName1) && tableset.contains(tableName2) && !ruleDetail5.getPattern().contains("s"))
                {
                    selectSql += ","+tableName1+"."+ruleDetail5.getFieldName()+" - "+
                            tableName2+"."+ruleDetail5.getMatchfieldName();
                    whereSql += " AND "+tableName1+"."+ruleDetail5.getFieldName()+" - "+
                            tableName2+"."+ruleDetail5.getMatchfieldName()+ruleDetail5.getPattern()+ marchvalue;
                }
                else if(tableset.contains(tableName1) && tableset.contains(tableName2) && ruleDetail5.getPattern().contains("s"))
                {
                    String pattern = ruleDetail5.getPattern().substring(1);
                    selectSql += ",SUM("+tableName1+"."+ruleDetail5.getFieldName()+") - SUM("+
                            tableName2+"."+ruleDetail5.getMatchfieldName()+") ";
                    if(StringUtils.isEmpty(groupbySql)){
                        if(type == 0)
                            groupbySql = " GROUP BY "+tableConfig.gethumanTable()+"."+tableConfig.gethumanId();
                        else if(type == 1)
                            groupbySql = " GROUP BY "+tableConfig.getcompanyTable()+"."+tableConfig.getcompanyId();
                    }
                    if(StringUtils.isEmpty(havingSql)){
                        havingSql += " HAVING SUM("+tableName1+"."+ruleDetail5.getFieldName()+") - SUM("+
                                tableName2+"."+ruleDetail5.getMatchfieldName()+") "+pattern+ marchvalue;
                    }
                    else{
                        havingSql += " AND SUM("+tableName1+"."+ruleDetail5.getFieldName()+") - SUM("+
                                tableName2+"."+ruleDetail5.getMatchfieldName()+") "+pattern+ marchvalue;
                    }
                }
            }

            //循环type6:日期字段筛选多少天以内
            for (JwRuledetail ruleDetail6:ruleDetailsType6) {
                NewTable table1 = jwTableMapper.selectById(ruleDetail6.getTableId());
                String tableName1 = table1.getTablename();
                if(tableset.contains(tableName1))
                {
                    String marchvalue = ruleDetail6.getMatchValue();
                    whereSql += " AND DATEDIFF(CURDATE(),STR_TO_DATE("+tableName1+"."+ruleDetail6.getFieldName()+")) > "+ marchvalue;
                }
            }

            //循环type3:汇总数量条件
            for (JwRuledetail ruleDetail3 :ruleDetailsType3) {
                NewTable table1 = jwTableMapper.selectById(ruleDetail3.getTableId());
                String tableName1 = table1.getTablename();
                String marchvalue = ruleDetail3.getMatchValue();
                if(tableset.contains(tableName1)){
                    selectSql += ",COUNT("+tableName1+"."+ruleDetail3.getFieldName()+") ";
                    if(StringUtils.isEmpty(groupbySql)){
                        if(type == 0)
                            groupbySql = " GROUP BY "+tableConfig.gethumanTable()+"."+tableConfig.gethumanId();
                        else if(type == 1)
                            groupbySql = " GROUP BY "+tableConfig.getcompanyTable()+"."+tableConfig.getcompanyId();
                    }
                    if(StringUtils.isEmpty(havingSql)){
                        havingSql += " HAVING COUNT("+tableName1+"."+ruleDetail3.getFieldName()+") "+
                                ruleDetail3.getPattern()+ marchvalue;
                    }
                    else{
                        havingSql += " AND COUNT("+tableName1+"."+ruleDetail3.getFieldName()+") "+
                                ruleDetail3.getPattern()+ marchvalue;
                    }
                }
            }

            //循环type4:汇总合计条件
            for (JwRuledetail ruleDetail4 :ruleDetailsType4) {
                NewTable table1 = jwTableMapper.selectById(ruleDetail4.getTableId());
                String tableName1 = table1.getTablename();
                String marchvalue = ruleDetail4.getMatchValue();
                if(tableset.contains(tableName1)){
                    selectSql += ",SUM("+tableName1+"."+ruleDetail4.getFieldName()+") ";
                    if(StringUtils.isEmpty(groupbySql)){
                        if(type == 0)
                            groupbySql = " GROUP BY "+tableConfig.gethumanTable()+"."+tableConfig.gethumanId();
                        else if(type == 1)
                            groupbySql = " GROUP BY "+tableConfig.getcompanyTable()+"."+tableConfig.getcompanyId();
                    }
                    if(StringUtils.isEmpty(havingSql)){
                        havingSql += " HAVING SUM("+tableName1+"."+ruleDetail4.getFieldName()+") "+
                                ruleDetail4.getPattern()+ marchvalue;
                    }
                    else{
                        havingSql += " AND SUM("+tableName1+"."+ruleDetail4.getFieldName()+") "+
                                ruleDetail4.getPattern()+ marchvalue;
                    }
                }
            }

            if(!tableset.isEmpty()){
                String sql = selectSql+fromSql+whereSql+groupbySql+havingSql;
                System.out.println(sql);

                // 创建结果表
                String resultTableName = UUID.randomUUID().toString().replace("-","");
                modeltaskMapper.createNewTable(resultTableName,sql.replace(" 1=1 "," 1=0 "));
                // 修改为启用
                jwRuleMapper.update(new UpdateWrapper<JwRule>()
                        .eq("rule_id", ruleId)
                        .set("sql_statement", sql)
                        .set("result_table",resultTableName)
                        .set("is_on",1)
                );
                Modeltask modeltask = new Modeltask();
                modeltask.setTask(sql);
                modeltask.setResulttable(resultTableName);
                modeltask.setResultcomment(rule.getDescription());
                modeltask.setTimestamp(new Date());
                modeltask.setModelid(Integer.parseInt(ruleId));
                modeltaskMapper.insert(modeltask);

                log.info("修改{}模型的状态成功", rule.getRuleName());
            }

        }

        log.info("修改{}模型的启用状态成功", rule.getRuleName());
    }

    @Override
    public JSONArray getRuleResult(String ruleId) {
        //校验参数是否有效
        JwRule rule = jwRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new IllegalArgumentException("模型不存在，请重试！");
        }
        else if(rule.getIsOn() == 0){
            throw new IllegalArgumentException("模型未启动，无法查看结果！");
        }
        else if(StringUtils.isEmpty(rule.getResultTable())){
            throw new IllegalArgumentException("模型结果表不存在，请联系管理员！");
        }

        String resultTable = rule.getResultTable();
        Map<String, Object> tableExist = modeltaskMapper.tableExist(resultTable);
        if(tableExist != null){
            //结果表存在
            JSONArray details = new JSONArray();
            List<Map<String, Object>> maps = modeltaskMapper.resultTable(resultTable);
            maps.forEach(
                    map ->{
                        JSONObject jsonObject = new JSONObject(map);
                        details.add(jsonObject);
                    }
            );
            if(rule.getType() == 0){
                JSONArray result = new JSONArray();
                result.add(new JSONObject().put("personInfo",details));
                return result;
            }
            else if(rule.getType() == 1){
                JSONArray result = new JSONArray();
                result.add(new JSONObject().put("companyInfo",details));
                return result;
            }
            else
                throw new IllegalArgumentException("模型类型异常！");
        }
        else
        {
            throw new IllegalArgumentException("模型结果表不存在，请联系管理员！");
        }
    }

    @Override
    public JSONObject getResultDetail(String ruleId,String id) {
        //校验参数是否有效
        JwRule rule = jwRuleMapper.selectById(ruleId);
        JSONObject result;
        if (rule == null) {
            throw new IllegalArgumentException("模型不存在，请重试！");
        }else if(rule.getIsOn()<0 || StringUtils.isEmpty(rule.getResultTable())){
            throw new IllegalArgumentException("模型未启用，请联系管理员！");
        }
        else if(rule.getType() == 0){
            //人物模型
            String tableName = rule.getResultTable();
            Map<String, Object> stringObjectMap = modeltaskMapper.resultDetail(tableName, tableConfig.gethumanId(), id);
            result = new JSONObject(stringObjectMap);
            return result;
        }
        else if(rule.getType() == 1){
            //单位模型
            String tableName = rule.getResultTable();
            Map<String, Object> stringObjectMap = modeltaskMapper.resultDetail(tableName, tableConfig.getcompanyId(), id);
            result = new JSONObject(stringObjectMap);
            return result;
        }
        else throw new IllegalArgumentException("模型类别异常，请联系管理员！");
    }


}




