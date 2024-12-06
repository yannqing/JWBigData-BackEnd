package com.wxjw.jwbigdata.vo.FileVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildrenVo {
    private String id;
    private String label;
    private String father;
    private boolean draggable;
    private List<ChildrenVo> children;
}
