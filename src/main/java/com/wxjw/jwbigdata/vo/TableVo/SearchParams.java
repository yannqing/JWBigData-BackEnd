package com.wxjw.jwbigdata.vo.TableVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author black
 * @version 1.0.0
 * @className SearchParams
 * @description TODO
 * @date 2025-01-18 19:53
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchParams {
    private String keyWord;
    private List<Integer> tabId;
}
