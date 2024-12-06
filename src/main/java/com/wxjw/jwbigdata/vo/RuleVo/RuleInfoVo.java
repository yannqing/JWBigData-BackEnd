package com.wxjw.jwbigdata.vo.RuleVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.PrimitiveIterator;

/**
 * @author black
 * @version 1.0.0
 * @className RuleInfoVo
 * @description TODO
 * @date 2024-11-22 19:05
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleInfoVo {
    private Integer userId;
    private RuleVo ruleInfo;
}
