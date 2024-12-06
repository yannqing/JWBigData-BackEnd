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
        implements JwRuleService {

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

    @Resource
    private RelationOfNewtableMapper relationOfNewtableMapper;

    @Autowired
    private TableConfig tableConfig;

    @Resource
    private ModeltaskMapper modeltaskMapper;

    @Resource
    private modelResultdbQueryService modelResultdbQueryService;

    @Override
    public void addRule(Integer userId, String ruleName, String ruleComment, String ruleSteps) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("id", userId));
        if (user == null)
            throw new IllegalArgumentException("用户不存在，请重试！");
        JwRule rule = new JwRule();
        rule.setRuleName(ruleName);
        rule.setNote(ruleSteps);
        rule.setDescription(ruleComment);
        rule.setCreateBy(userId);
        rule.setCreateTime(DateTime.now());
        if (user.getRole() == 1) //管理员:公有模型
            rule.setStatus(1);
        else rule.setStatus(0); //普通用户：私有模型
        rule.setIsOn(0);
        jwRuleMapper.insert(rule);
        log.info("用户{}新增了一个模型{}", user.getUsername(), ruleName);
    }

    @Override
    public void delRule(String[] ruleIds) {
        Arrays.stream(ruleIds).forEach(ruleId -> {
            if (jwRuleMapper.selectById(ruleId) == null) {
                throw new IllegalArgumentException("模型不存在，请重试！");
            }
        });
        Arrays.stream(ruleIds).forEach(ruleId -> jwRuleMapper.deleteById(ruleId));
        log.info("删除模型成功，删除的模型id：{}", (Object) ruleIds);
    }

    @Override
    public void switchRuleStatus(Integer ruleId, Integer status) {
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
        List<JwRule> publicrules = jwRuleMapper.selectList(new QueryWrapper<JwRule>().eq("status", 1).ne("create_by", userId));
        List<RuleVo> publicruleVos = new ArrayList<>();
        publicrules.forEach(rule -> {
            RuleVo vo = new RuleVo(rule, userMapper.selectById(rule.getCreateBy()));
            publicruleVos.add(vo);
        });

        List<JwRule> privaterules = jwRuleMapper.selectList(new QueryWrapper<JwRule>().eq("create_by", userId));
        List<RuleVo> privateruleVos = new ArrayList<>();
        privaterules.forEach(rule -> {
            RuleVo vo = new RuleVo(rule, userMapper.selectById(rule.getCreateBy()));
            privateruleVos.add(vo);
        });
        JSONObject result = new JSONObject();
        result.put("publicRules", publicruleVos);
        result.put("privateRules", privateruleVos);
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

        if (isOn == 0) {
            // 修改为未启用
            //修改状态
            jwRuleMapper.update(new UpdateWrapper<JwRule>()
                    .eq("rule_id", ruleId)
                    .set("is_on", isOn)
            );
            modeltaskMapper.delete(new QueryWrapper<Modeltask>().eq("modelId", ruleId));
        } else {
            // 需生成sql_statement
            List<JwRuledetail> ruledetails = jwRuledetailMapper.selectList(new QueryWrapper<JwRuledetail>().eq("rule_id", ruleId));

            //匹配常量
            List<JwRuledetail> ruleDetailsType1 = ruledetails.stream().filter(
                    ruledetail -> ruledetail.getMatchType().equals("2")
            ).toList();
            //关联字段
            List<JwRuledetail> ruleDetailsType2 = ruledetails.stream().filter(
                    ruledetail -> (ruledetail.getMatchType().equals("1") && ruledetail.getPattern().equals("="))
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
                    ruledetail -> (ruledetail.getMatchType().equals("1") && !ruledetail.getPattern().equals("="))
            ).toList();
            //比较当前日期
            List<JwRuledetail> ruleDetailsType6 = ruledetails.stream().filter(
                    ruledetail -> ruledetail.getMatchType().equals("5")
            ).toList();

            String selectSql = "";
            String fromSql = "";
            String whereSql = "";
            String groupbySql = "";
            String havingSql = "";
            int type = -1;

            Set<String> tableset = new HashSet();
            Set<String> cols = new HashSet();

            //循环type2:表字段连接
            for (JwRuledetail ruleDetail : ruleDetailsType2) {
                Integer fieldId = ruleDetail.getFieldId();
                NewColumn field1 = jwFieldMapper.selectById(fieldId);
                NewTable table1 = jwTableMapper.selectById(field1.getNewtableId());
                String tableName1 = table1.getTablename();
                String tableComment1 = table1.getComment();
                String fieldName1 = field1.getColumnname();
                String fieldComment1 = field1.getComment();

                Integer matchFieldId = ruleDetail.getMatchfieldId();
                NewColumn field2 = jwFieldMapper.selectById(matchFieldId);
                NewTable table2 = jwTableMapper.selectById(field2.getNewtableId());
                String tableName2 = table2.getTablename();
                String tableComment2 = table2.getComment();
                String fieldName2 = field2.getColumnname();
                String fieldComment2 = field2.getComment();

                tableset.add(tableName1);
                tableset.add(tableName2);

                whereSql += " AND " + tableName1 + "." + fieldName1 + ruleDetail.getPattern() +
                        tableName2 + "." + fieldName2;
                if (cols.add("`" + tableComment1 + "." + fieldComment1 + "`")) {
                    selectSql += "," + tableName1 + "." + fieldName1 + " '" + tableComment1 + "." + fieldComment1 + "'";
                }
                if (cols.add("`" + tableComment2 + "." + fieldComment2 + "`")) {
                    selectSql += "," + tableName2 + "." + fieldName2 + " '" + tableComment2 + "." + fieldComment2 + "'";
                }
            }

            //循环type1:表字段匹配常量
            for (JwRuledetail ruleDetail1 : ruleDetailsType1) {
                Integer fieldId = ruleDetail1.getFieldId();
                NewColumn field = jwFieldMapper.selectById(fieldId);
                NewTable table = jwTableMapper.selectById(field.getNewtableId());
                String tableName = table.getTablename();
                String tableComment = table.getComment();
                tableset.add(tableName);

                String marchvalue = ruleDetail1.getMatchValue();
                String fieldName = field.getColumnname();
                String fieldComment = field.getComment();
                //如果不是数字类型，需要特殊拼接处理

                if (ruleDetail1.getPattern().equals("like")) {
                    marchvalue = "'%" + marchvalue + "%'";
                } else marchvalue = "'" + marchvalue + "'";

                whereSql += " AND " + tableName + "." + fieldName + " " +
                        ruleDetail1.getPattern() + marchvalue;
                if (cols.add("`" + tableComment + "." + fieldComment + "`")) {
                    selectSql += "," + tableName + "." + fieldName + " '" + tableComment + "." + fieldComment + "'";
                }
            }

            //循环type5:表字段与字段间匹配差额
            for (JwRuledetail ruleDetail5 : ruleDetailsType5) {
                Integer fieldId = ruleDetail5.getFieldId();
                NewColumn field1 = jwFieldMapper.selectById(fieldId);
                NewTable table1 = jwTableMapper.selectById(field1.getNewtableId());
                String tableName1 = table1.getTablename();
                String tableComment1 = table1.getComment();
                String fieldName1 = field1.getColumnname();
                String fieldComment1 = field1.getComment();

                Integer matchFieldId = ruleDetail5.getMatchfieldId();
                NewColumn field2 = jwFieldMapper.selectById(matchFieldId);
                NewTable table2 = jwTableMapper.selectById(field2.getNewtableId());
                String tableName2 = table2.getTablename();
                String tableComment2 = table2.getComment();
                String fieldName2 = field2.getColumnname();
                String fieldComment2 = field2.getComment();

                String marchvalue = ruleDetail5.getMatchValue();

                if (!ruleDetail5.getPattern().contains("s")) {
                    whereSql += " AND " + tableName1 + "." + fieldName1 + " - " +
                            tableName2 + "." + fieldName2 + ruleDetail5.getPattern() + marchvalue;
                    if (cols.add("`" + tableComment1 + "." + fieldComment1 + "-" + tableComment2 + "." + fieldComment2 + "`")) {
                        selectSql += "," + tableName1 + "." + fieldName1 + " - " +
                                tableName2 + "." + fieldName2 + " '" + tableComment1 + "." + fieldComment1 + "-" + tableComment2 + "." + fieldComment2 + "'";
                    }
                } else {
                    String pattern = ruleDetail5.getPattern().substring(1);
                    selectSql += ",SUM(" + tableName1 + "." + fieldName1 + ") - SUM(" +
                            tableName2 + "." + fieldName2 + ") '合计(" + tableComment1 + "." + fieldComment1 + ")-合计(" + tableComment2 + "." + fieldComment2 + ")'";
                    if (StringUtils.isEmpty(groupbySql)) {
                        groupbySql = " GROUP BY ";
                    }
                    if (StringUtils.isEmpty(havingSql)) {
                        havingSql += " HAVING SUM(" + tableName1 + "." + fieldName1 + ") - SUM(" +
                                tableName2 + "." + fieldName2 + ") " + pattern + marchvalue;
                    } else {
                        havingSql += " AND SUM(" + tableName1 + "." + fieldName1 + ") - SUM(" +
                                tableName2 + "." + fieldName2 + ") " + pattern + marchvalue;
                    }
                    cols.add("`合计(" + tableComment1 + "." + fieldComment1 + ")-合计(" + tableComment2 + "." + fieldComment2 + ")`");
                }
            }

            //循环type6:日期字段筛选多少天以内
            for (JwRuledetail ruleDetail6 : ruleDetailsType6) {
                Integer fieldId = ruleDetail6.getFieldId();
                NewColumn field = jwFieldMapper.selectById(fieldId);
                NewTable table = jwTableMapper.selectById(field.getNewtableId());
                String tableName = table.getTablename();
                String tableComment = table.getComment();
                String fieldName = field.getColumnname();
                String fieldComment = field.getComment();
                tableset.add(tableName);

                String marchvalue = ruleDetail6.getMatchValue();
                whereSql += " AND DATEDIFF(CURDATE(),STR_TO_DATE(" + tableName + "." + field.getColumnname() + ")) > " + marchvalue;
                if (cols.add("`" + tableComment + "." + fieldComment + "`")) {
                    selectSql += "," + tableName + "." + fieldName + " '" + tableComment + "." + fieldComment + "'";
                }
            }

            //循环type3:汇总数量条件
            for (JwRuledetail ruleDetail3 : ruleDetailsType3) {
                Integer fieldId = ruleDetail3.getFieldId();
                NewColumn field = jwFieldMapper.selectById(fieldId);
                NewTable table = jwTableMapper.selectById(field.getNewtableId());
                String tableName = table.getTablename();
                String tableComment = table.getComment();
                String marchvalue = ruleDetail3.getMatchValue();
                tableset.add(tableName);

                selectSql += ",COUNT(" + tableName + "." + field.getColumnname() + ") '数量(" + tableComment + "." + field.getComment() + ")'";
                cols.add("`数量(" + tableComment + "." + field.getComment() + ")`");
                if (StringUtils.isEmpty(groupbySql)) {
                    groupbySql = " GROUP BY ";
                }

                if (StringUtils.isEmpty(havingSql)) {
                    havingSql += " HAVING COUNT(" + tableName + "." + field.getColumnname() + ") " +
                            ruleDetail3.getPattern() + marchvalue;
                }
                else {
                    havingSql += " AND COUNT(" + tableName + "." + field.getColumnname() + ") " +
                            ruleDetail3.getPattern() + marchvalue;
                }
            }

            //循环type4:汇总合计条件
            for (JwRuledetail ruleDetail4 : ruleDetailsType4) {
                Integer fieldId = ruleDetail4.getFieldId();
                NewColumn field = jwFieldMapper.selectById(fieldId);
                NewTable table = jwTableMapper.selectById(field.getNewtableId());
                String tableName = table.getTablename();
                String tableComment = table.getComment();
                String marchvalue = ruleDetail4.getMatchValue();
                tableset.add(tableName);

                selectSql += ",SUM(" + tableName + "." + field.getColumnname() + ") '合计(" + tableComment + "." + field.getComment() + ")'";
                cols.add("`合计(" + tableComment + "." + field.getComment() + ")`");
                if (StringUtils.isEmpty(groupbySql)) {
                    groupbySql = " GROUP BY ";
                }
                if (StringUtils.isEmpty(havingSql)) {
                    havingSql += " HAVING SUM(" + tableName + "." + field.getColumnname() + ") " +
                            ruleDetail4.getPattern() + marchvalue;
                } else {
                    havingSql += " AND SUM(" + tableName + "." + field.getColumnname() + ") " +
                            ruleDetail4.getPattern() + marchvalue;
                }
            }

            if (!tableset.isEmpty()) {
                Set<String> newtableset = new HashSet();
                for (String tablename : tableset) {
                    newtableset.add(tablename);
                    if (!tablename.equals(tableConfig.gethumanTable()) && !tablename.equals(tableConfig.getcompanyTable())) {
                        NewTable table = jwTableMapper.selectOne(new QueryWrapper<NewTable>().eq("tablename", tablename));
                        Integer tableId = table.getId();
                        RelationOfNewtable relationOfNewtable = relationOfNewtableMapper.selectOne(new QueryWrapper<RelationOfNewtable>().eq("newtable_id", tableId));
                        String label = relationOfNewtable.getLabel();
                        // 当前表为人物相关
                        if (label.equals("human")) {
                            newtableset.add(tableConfig.gethumanTable());
                            String wSql = " AND " + tableConfig.gethumanTable() + '.' + tableConfig.gethumanPk() + " = " + tablename + '.' + tableConfig.getHumanFk();
                            if (!whereSql.contains(wSql)) {
                                whereSql = wSql + whereSql;
                            }
                        } else if (label.equals("unit")) {
                            newtableset.add(tableConfig.getcompanyTable());
                            String wSql = " AND " + tableConfig.getcompanyTable() + '.' + tableConfig.getcompanyPk() + " = " + tablename + '.' + tableConfig.getCompanyFk();
                            if (!whereSql.contains(wSql)) {
                                whereSql = wSql + whereSql;
                            }
                        }
                    }
                }
                tableset = newtableset;
                //模型类型:优先级：任务-单位-事件
                if (tableset.contains(tableConfig.gethumanTable())) {
                    type = 0;
                    cols.add("身份证号");
                    cols.add("姓名");
                    selectSql = "SELECT " + tableConfig.gethumanTable() + '.' + tableConfig.gethumanId() + " '身份证号'," + tableConfig.gethumanTable() + '.' + tableConfig.gethumanName() + " '姓名'" + selectSql;
                    if (groupbySql == " GROUP BY ") {
                        groupbySql += tableConfig.gethumanTable() + '.' + tableConfig.gethumanPk();
                    }
                } else if (tableset.contains(tableConfig.getcompanyTable())) {
                    type = 1;
                    cols.add("单位识别号");
                    cols.add("名称");
                    selectSql = "SELECT " + tableConfig.getcompanyTable() + '.' + tableConfig.getcompanyId() + " '单位识别号'," + tableConfig.getcompanyTable() + '.' + tableConfig.getcompanyName() + " '名称'" + selectSql;
                    if (groupbySql == " GROUP BY ") {
                        groupbySql += tableConfig.getcompanyTable() + '.' + tableConfig.getcompanyPk();
                    }
                }
                for (String tableName : tableset) {
                    if (StringUtils.isEmpty(fromSql)) {
                        fromSql += " FROM " + tableName;
                    } else
                        fromSql += " ," + tableName;
                }

                whereSql = " WHERE 1=1 " + whereSql;
                String sql = selectSql + fromSql + whereSql + groupbySql + havingSql;
                System.out.println(sql);

                // 创建结果表
                String resultTableName = UUID.randomUUID().toString().replace("-", "");
                String columnNames = " (";
                for (String col : cols) {
                    columnNames += col + " varchar(255),";
                }
                columnNames = columnNames.substring(0, columnNames.length() - 1) + ")";
                modelResultdbQueryService.createNewTable(resultTableName, columnNames);
                // 修改为启用
                jwRuleMapper.update(new UpdateWrapper<JwRule>()
                        .eq("rule_id", ruleId)
                        .set("sql_statement", sql)
                        .set("result_table", resultTableName)
                        .set("type", type)
                        .set("is_on", 1)
                );
                Modeltask modeltask = new Modeltask();
                modeltask.setTask(sql);
                modeltask.setResulttable(resultTableName);
                modeltask.setResultcomment(rule.getDescription());
                modeltask.setTimestamp(new Date());
                modeltask.setModelid(Integer.parseInt(ruleId));
                modeltaskMapper.insert(modeltask);

                log.info("修改{}模型的状态成功", rule.getRuleName());
            } else {
                throw new IllegalArgumentException("规则条目存在问题，请联系管理员！");
            }

        }

        log.info("修改{}模型的启用状态成功", rule.getRuleName());
    }

    @Override
    public JSONObject getRuleResult(String ruleId) {
        //校验参数是否有效
        JwRule rule = jwRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new IllegalArgumentException("模型不存在，请重试！");
        } else if (StringUtils.isEmpty(rule.getResultTable())) {
            throw new IllegalArgumentException("模型结果表不存在，请联系管理员！");
        }

        String resultTable = rule.getResultTable();
        List<Map<String, Object>> tableExist = modelResultdbQueryService.tableExist(resultTable);
        if (tableExist != null) {
            //结果表存在
            JSONArray details = new JSONArray();
            List<Map<String, Object>> maps = modelResultdbQueryService.resultTable(resultTable);
            maps.forEach(
                    map -> {
                        JSONObject jsonObject = new JSONObject(map);
                        details.add(jsonObject);
                    }
            );

            JSONObject result = new JSONObject();
            if (rule.getType() == 0) {
                result.put("personInfo", details);
                result.put("companyInfo", new JSONArray());
            } else if (rule.getType() == 1) {
                result.put("personInfo", new JSONArray());
                result.put("companyInfo", details);
            } else throw new IllegalArgumentException("模型类型异常，请联系管理员！");
            return result;
        } else {
            throw new IllegalArgumentException("模型结果表不存在，请联系管理员！");
        }
    }

    @Override
    public JSONArray getResultDetail(String ruleId, String id) {
        //校验参数是否有效
        JwRule rule = jwRuleMapper.selectById(ruleId);
        JSONArray result;
        if (rule == null) {
            throw new IllegalArgumentException("模型不存在，请重试！");
        } else if (rule.getIsOn() < 0 || StringUtils.isEmpty(rule.getResultTable())) {
            throw new IllegalArgumentException("模型未启用，请联系管理员！");
        }


        String tableName = rule.getResultTable();
        List<Map<String, Object>> stringObjectMap = modelResultdbQueryService.resultDetail(tableName, tableConfig.gethumanId(), id);
        result = new JSONArray(stringObjectMap);
        return result;


    }

    @Override
    public JwRule getRuleById(String ruleId) {
        return jwRuleMapper.selectById(ruleId);
    }


}




